package com.law.train.basic.controller;

import cn.hutool.core.util.StrUtil;
import com.law.train.common.Result;
import com.law.train.basic.controller.ExamStudentLoginController.StudentLoginUser;
import com.law.train.module.exam_question.entity.ExamQuestion;
import com.law.train.module.exam_question_option.entity.ExamQuestionOption;
import com.law.train.module.exam_question_option.service.ExamQuestionOptionService;
import com.law.train.module.exam_question.service.ExamQuestionService;
import com.law.train.module.exam_question_practice_record.entity.ExamQuestionPracticeRecord;
import com.law.train.module.exam_question_practice_record.service.ExamQuestionPracticeRecordService;
import com.law.train.module.exam_wrong_question_book.entity.ExamWrongQuestionBook;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.law.train.module.exam_wrong_question_book.service.ExamWrongQuestionBookService;
import com.law.train.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学生端错题库：按课程分组、错题列表、单题详情、错题重刷提交（触发遗忘曲线）
 */
@RestController
@RequestMapping("/student")
public class StudentWrongController {

    @Autowired
    private ExamWrongQuestionBookService wrongQuestionBookService;
    @Autowired
    private ExamQuestionService examQuestionService;
    @Autowired
    private ExamQuestionOptionService examQuestionOptionService;
    @Autowired
    private ExamQuestionPracticeRecordService practiceRecordService;
    @Autowired
    private JwtUtil<StudentLoginUser> jwtUtil;

    private Long getStudentId(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StrUtil.isBlank(authorization)) return null;
        if (ExamStudentLoginController.STUDENT_TOKEN_CACHE_MAP.get(authorization) == null) return null;
        if (!jwtUtil.verifyToken(authorization)) return null;
        StudentLoginUser user = jwtUtil.parseToken(authorization, StudentLoginUser.class);
        return user != null ? user.getId() : null;
    }

    /**
     * 学生端：错题本中按课程分组后的课程列表（当前登录学生）
     */
    @GetMapping("/wrong/courses")
    public Result wrongCourses(HttpServletRequest request) {
        Long studentId = getStudentId(request);
        if (studentId == null) return Result.error(401, "请先登录");
        ExamWrongQuestionBook query = new ExamWrongQuestionBook();
        query.setStudentId(studentId);
        query.setStatus(1);
        List<ExamWrongQuestionBook> list = wrongQuestionBookService.listQuery(query);
        List<Map<String, Object>> courses = list.stream()
                .filter(w -> w.getCourseId() != null && w.getCourseName() != null)
                .collect(Collectors.groupingBy(ExamWrongQuestionBook::getCourseId))
                .entrySet().stream()
                .map(e -> {
                    ExamWrongQuestionBook first = e.getValue().get(0);
                    Map<String, Object> m = new HashMap<>();
                    m.put("courseId", e.getKey());
                    m.put("courseName", first.getCourseName());
                    m.put("wrongCount", e.getValue().size());
                    return m;
                })
                .sorted(Comparator.comparing(m -> (String) m.get("courseName")))
                .collect(Collectors.toList());
        return Result.success(courses);
    }

    /**
     * 学生端：错题列表（支持按课程、题型、关键词筛选，分页）
     */
    @GetMapping("/wrong/list")
    public Result wrongList(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Integer questionType,
            @RequestParam(required = false) String keyword) {
        Long studentId = getStudentId(request);
        if (studentId == null) return Result.error(401, "请先登录");
        if (current == null || current < 1) current = 1;
        if (size == null || size < 1) size = 10;
        if (size > 100) size = 100;
        ExamWrongQuestionBook query = new ExamWrongQuestionBook();
        query.setStudentId(studentId);
        query.setStatus(1);
        if (courseId != null) query.setCourseId(courseId);
        if (questionType != null) query.setQuestionType(questionType);
        if (StrUtil.isNotBlank(keyword)) query.setQuestionTitle(keyword.trim());
        IPage<ExamWrongQuestionBook> page = wrongQuestionBookService.pageQuery(current, size, query);
        Map<String, Object> data = new HashMap<>();
        data.put("records", page.getRecords());
        data.put("total", page.getTotal());
        data.put("size", page.getSize());
        data.put("current", page.getCurrent());
        return Result.success(data);
    }

    /**
     * 学生端：获取单题详情（用于重刷弹框：题目+选项）
     */
    @GetMapping("/wrong/question/{questionId}")
    public Result wrongQuestionDetail(HttpServletRequest request, @PathVariable Long questionId) {
        Long studentId = getStudentId(request);
        if (studentId == null) return Result.error(401, "请先登录");
        ExamQuestion question = examQuestionService.getById(questionId);
        if (question == null) return Result.error("题目不存在");
        List<ExamQuestionOption> options = new ArrayList<>();
        if (question.getQuestionType() != null && (question.getQuestionType() == 1 || question.getQuestionType() == 2)) {
            ExamQuestionOption optQuery = new ExamQuestionOption();
            optQuery.setQuestionId(questionId);
            options = examQuestionOptionService.listQuery(optQuery);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("question", question);
        data.put("options", options);
        return Result.success(data);
    }

    /** 练习类型：1=普通练习 2=模拟考试 3=错题重刷 */
    private static final int PRACTICE_TYPE_WRONG_REDO = 3;
    /** 题目来源：1=练习 2=试卷 3=错题重刷 */
    private static final int SOURCE_TYPE_WRONG_REDO = 3;

    /**
     * 学生端：提交错题重刷作答记录。
     * 写入练习记录后由 ForgettingCurvePracticeRecordAspect 拦截 save，调用 ForgettingCurveService.afterPracticeRecordSaved，
     * 会参与遗忘曲线记忆更新、错题本 syncWrongBook、计划明细完成标记等算法逻辑。
     */
    @PostMapping("/wrong/record")
    public Result submitWrongRedoRecord(HttpServletRequest request, @RequestBody WrongRedoRecordRequest body) {
        Long studentId = getStudentId(request);
        if (studentId == null) return Result.error(401, "请先登录");
        if (body.getCourseId() == null || body.getQuestionId() == null) return Result.error("缺少课程或题目");
        LocalDateTime now = LocalDateTime.now();
        ExamQuestionPracticeRecord record = new ExamQuestionPracticeRecord();
        record.setStudentId(studentId);
        record.setCourseId(body.getCourseId());
        record.setQuestionId(body.getQuestionId());
        record.setPracticeType(PRACTICE_TYPE_WRONG_REDO);
        record.setSourceType(SOURCE_TYPE_WRONG_REDO);
        record.setPlanId(body.getPlanId());
        record.setStudentAnswer(body.getStudentAnswer());
        record.setStandardAnswer(body.getStandardAnswer());
        record.setIsCorrect(body.getIsCorrect() != null ? body.getIsCorrect() : 0);
        record.setScore(body.getScore() != null ? body.getScore() : BigDecimal.ZERO);
        record.setScoreRate(body.getScoreRate() != null ? body.getScoreRate() : BigDecimal.ZERO);
        record.setUseTimeSec(body.getUseTimeSec() != null ? body.getUseTimeSec() : 0);
        record.setJudgeType(body.getJudgeType() != null ? body.getJudgeType() : 1);
        record.setJudgeResult(body.getJudgeResult());
        record.setDifficultyLevel(body.getDifficultyLevel());
        record.setAnswerTime(now);
        record.setCreateTime(now);
        practiceRecordService.save(record);
        return Result.success(record);
    }

    /**
     * 学生端：从错题本移除一条（软删：status=0，仅限本人）
     */
    @DeleteMapping("/wrong/{id}")
    public Result removeWrong(HttpServletRequest request, @PathVariable Long id) {
        Long studentId = getStudentId(request);
        if (studentId == null) return Result.error(401, "请先登录");
        if (id == null) return Result.error("参数错误");
        ExamWrongQuestionBook book = wrongQuestionBookService.getById(id);
        if (book == null) return Result.error("记录不存在");
        if (!studentId.equals(book.getStudentId())) return Result.error(403, "无权操作");
        book.setStatus(0);
        wrongQuestionBookService.updateById(book);
        return Result.success(true);
    }

    @Data
    public static class WrongRedoRecordRequest {
        private Long courseId;
        private Long questionId;
        private Long planId;
        private String studentAnswer;
        private String standardAnswer;
        private Integer isCorrect;
        private BigDecimal score;
        private BigDecimal scoreRate;
        private Integer useTimeSec;
        private Integer judgeType;
        private String judgeResult;
        private Integer difficultyLevel;
    }
}
