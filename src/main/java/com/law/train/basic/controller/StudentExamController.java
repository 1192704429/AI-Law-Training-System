package com.law.train.basic.controller;

import cn.hutool.core.util.StrUtil;
import com.law.train.common.Result;
import com.law.train.basic.controller.ExamStudentLoginController.StudentLoginUser;
import com.law.train.module.exam_course.entity.ExamCourse;
import com.law.train.module.exam_course.service.ExamCourseService;
import com.law.train.module.exam_paper.entity.ExamPaper;
import com.law.train.module.exam_paper.service.ExamPaperService;
import com.law.train.module.exam_paper_question.entity.ExamPaperQuestion;
import com.law.train.module.exam_paper_question.service.ExamPaperQuestionService;
import com.law.train.module.exam_question.entity.ExamQuestion;
import com.law.train.module.exam_question_option.entity.ExamQuestionOption;
import com.law.train.module.exam_question_option.service.ExamQuestionOptionService;
import com.law.train.module.exam_question.service.ExamQuestionService;
import com.law.train.module.exam_question_practice_record.entity.ExamQuestionPracticeRecord;
import com.law.train.module.exam_question_practice_record.service.ExamQuestionPracticeRecordService;
import com.law.train.module.exam_student_paper.entity.ExamStudentPaper;
import com.law.train.module.exam_student_paper.service.ExamStudentPaperService;
import com.law.train.module.exam_student_paper_answer.entity.ExamStudentPaperAnswer;
import com.law.train.module.exam_student_paper_answer.service.ExamStudentPaperAnswerService;
import com.law.train.service.ForgettingCurveService;
import com.law.train.util.AiChatClient;
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
 * 学生端模拟考试：按课程选试卷、试卷详情、提交阅卷、错题入库
 */
@RestController
@RequestMapping("/student")
public class StudentExamController {

    @Autowired
    private ExamCourseService examCourseService;
    @Autowired
    private ExamPaperService examPaperService;
    @Autowired
    private ExamPaperQuestionService examPaperQuestionService;
    @Autowired
    private ExamQuestionService examQuestionService;
    @Autowired
    private ExamQuestionOptionService examQuestionOptionService;
    @Autowired
    private ExamQuestionPracticeRecordService practiceRecordService;
    @Autowired
    private ExamStudentPaperService examStudentPaperService;
    @Autowired
    private ExamStudentPaperAnswerService examStudentPaperAnswerService;
    @Autowired
    private ForgettingCurveService forgettingCurveService;
    @Autowired
    private JwtUtil<StudentLoginUser> jwtUtil;
    @Autowired
    private AiChatClient aiChatClient;

    private Long getStudentId(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StrUtil.isBlank(authorization)) return null;
        if (ExamStudentLoginController.STUDENT_TOKEN_CACHE_MAP.get(authorization) == null) return null;
        if (!jwtUtil.verifyToken(authorization)) return null;
        StudentLoginUser user = jwtUtil.parseToken(authorization, StudentLoginUser.class);
        return user != null ? user.getId() : null;
    }

    /**
     * 学生端：推荐题目（遗忘曲线：next_review_time <= 当前时间，按 recommend_priority desc, next_review_time asc）
     * @param courseId 可选，计划练习时传当日课程ID则只返回该课程推荐题
     */
    @GetMapping("/recommend/questions")
    public Result recommendQuestions(HttpServletRequest request,
                                     @RequestParam(required = false) Long courseId,
                                     @RequestParam(defaultValue = "20") Integer limit) {
        Long studentId = getStudentId(request);
        if (studentId == null) return Result.error(401, "请先登录");
        if (limit == null || limit <= 0) limit = 20;
        if (limit > 100) limit = 100;
        return Result.success(forgettingCurveService.listRecommend(studentId, courseId, limit));
    }

    /**
     * 学生端：分页获取启用课程列表（与刷题选课同一接口，这里仅列出供前端统一用）
     * 前端可直接用 /student/courses，本接口为别名或可去掉
     */
    @GetMapping("/exam/courses")
    public Result pageCourses(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "8") Integer size,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) Integer difficultyLevel) {
        ExamCourse query = new ExamCourse();
        query.setStatus(1);
        if (StrUtil.isNotBlank(courseName)) query.setCourseName(courseName.trim());
        if (difficultyLevel != null) query.setDifficultyLevel(difficultyLevel);
        return Result.success(examCourseService.pageQuery(current, size, query));
    }

    /**
     * 学生端：按课程获取启用试卷列表
     */
    @GetMapping("/exam/papers")
    public Result listPapersByCourse(@RequestParam Long courseId) {
        ExamPaper query = new ExamPaper();
        query.setCourseId(courseId);
        query.setStatus(1);
        List<ExamPaper> list = examPaperService.listQuery(query);
        return Result.success(list);
    }

    /**
     * 学生端：获取试卷详情（含全部题目及选项）
     */
    @GetMapping("/exam/paper/{paperId}")
    public Result getPaperDetail(@PathVariable Long paperId) {
        ExamPaper paper = examPaperService.getById(paperId);
        if (paper == null) return Result.error("试卷不存在");
        if (paper.getStatus() == null || paper.getStatus() != 1) return Result.error("试卷未启用");
        ExamPaperQuestion q = new ExamPaperQuestion();
        q.setPaperId(paperId);
        List<ExamPaperQuestion> paperQuestions = examPaperQuestionService.listQuery(q);
        List<Map<String, Object>> items = new ArrayList<>();
        for (ExamPaperQuestion pq : paperQuestions) {
            ExamQuestion question = examQuestionService.getById(pq.getQuestionId());
            if (question == null) continue;
            Map<String, Object> item = new HashMap<>();
            item.put("paperQuestionId", pq.getId());
            item.put("questionId", question.getId());
            item.put("questionTitle", question.getQuestionTitle());
            item.put("questionType", question.getQuestionType());
            item.put("score", pq.getScore());
            item.put("sortNo", pq.getSortNo());
            item.put("standardAnswer", question.getStandardAnswer());
            item.put("answerAnalysis", question.getAnswerAnalysis());
            if (question.getQuestionType() != null && (question.getQuestionType() == 1 || question.getQuestionType() == 2)) {
                ExamQuestionOption optQuery = new ExamQuestionOption();
                optQuery.setQuestionId(question.getId());
                item.put("options", examQuestionOptionService.listQuery(optQuery));
            } else {
                item.put("options", Collections.emptyList());
            }
            items.add(item);
        }
        items.sort(Comparator
                .comparing((Map<String, Object> m) -> (Integer) m.get("questionType"))
                .thenComparing(m -> (Integer) m.get("sortNo")));
        Map<String, Object> data = new HashMap<>();
        data.put("paper", paper);
        data.put("questions", items);
        return Result.success(data);
    }

    /**
     * 学生端：提交试卷（后端判题、主观题AI打分、保存作答记录、错题入库）
     */
    @PostMapping("/exam/submit")
    public Result submitExam(HttpServletRequest request, @RequestBody SubmitExamRequest body) {
        Long studentId = getStudentId(request);
        if (studentId == null) return Result.error(401, "请先登录");
        if (body.getPaperId() == null) return Result.error("缺少试卷ID");
        ExamPaper paper = examPaperService.getById(body.getPaperId());
        if (paper == null) return Result.error("试卷不存在");
        ExamPaperQuestion qq = new ExamPaperQuestion();
        qq.setPaperId(body.getPaperId());
        List<ExamPaperQuestion> paperQuestions = examPaperQuestionService.listQuery(qq);
        if (paperQuestions.isEmpty()) return Result.error("试卷无题目");
        Map<Long, String> answerMap = body.getAnswers() != null
                ? body.getAnswers().stream().collect(Collectors.toMap(AnswerItem::getQuestionId, AnswerItem::getAnswer, (a, b) -> b))
                : new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        ExamStudentPaper studentPaper = new ExamStudentPaper();
        studentPaper.setStudentId(studentId);
        studentPaper.setPaperId(paper.getId());
        studentPaper.setSubmitTime(now);
        studentPaper.setDurationSec(body.getUseTimeSec() != null ? body.getUseTimeSec() : 0);
        studentPaper.setTotalScore(BigDecimal.ZERO);
        studentPaper.setExamStatus(1);
        studentPaper.setCreateTime(now);
        examStudentPaperService.save(studentPaper);
        Long studentPaperId = studentPaper.getId();

        int correctCount = 0;
        int wrongCount = 0;
        BigDecimal totalScore = BigDecimal.ZERO;
        for (ExamPaperQuestion pq : paperQuestions) {
            ExamQuestion question = examQuestionService.getById(pq.getQuestionId());
            if (question == null) continue;
            String userAnswer = answerMap.getOrDefault(question.getId(), "").trim();
            boolean correct = false;
            BigDecimal score = BigDecimal.ZERO;
            String judgeResult = null;
            Integer judgeType = 1;
            if (question.getQuestionType() != null && question.getQuestionType() == 5) {
                if (userAnswer.isEmpty()) {
                    correct = false;
                    score = BigDecimal.ZERO;
                } else {
                    String aiRes = aiScoreSubjective(question.getQuestionTitle(), question.getStandardAnswer(), userAnswer);
                    try {
                        com.alibaba.fastjson.JSONObject obj = com.alibaba.fastjson.JSON.parseObject(aiRes);
                        int s = obj.getIntValue("score");
                        if (s < 0) s = 0;
                        if (s > 100) s = 100;
                        score = pq.getScore() != null ? pq.getScore().multiply(BigDecimal.valueOf(s)).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
                        judgeResult = obj.getString("comment");
                        correct = s >= 60;
                    } catch (Exception e) {
                        score = BigDecimal.ZERO;
                        judgeResult = "AI评分解析失败";
                        correct = false;
                    }
                    judgeType = 2;
                }
            } else {
                String std = normalizeAnswer(question.getStandardAnswer(), question.getQuestionType());
                String user = normalizeAnswer(userAnswer, question.getQuestionType());
                if (question.getQuestionType() != null && question.getQuestionType() == 2) {
                    user = normalizeMultiAnswer(userAnswer);
                    std = normalizeMultiAnswer(question.getStandardAnswer());
                }
                correct = std.equals(user);
                score = correct && pq.getScore() != null ? pq.getScore() : BigDecimal.ZERO;
            }
            if (correct) correctCount++; else wrongCount++;
            totalScore = totalScore.add(score);

            BigDecimal scoreRate = pq.getScore() != null && pq.getScore().compareTo(BigDecimal.ZERO) > 0
                    ? score.divide(pq.getScore(), 4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
            ExamQuestionPracticeRecord record = new ExamQuestionPracticeRecord();
            record.setStudentId(studentId);
            record.setCourseId(paper.getCourseId());
            record.setQuestionId(question.getId());
            record.setPracticeType(2);
            record.setSourceType(2);
            record.setPaperId(paper.getId());
            record.setStudentAnswer(userAnswer);
            record.setStandardAnswer(question.getStandardAnswer());
            record.setIsCorrect(correct ? 1 : 0);
            record.setScore(score);
            record.setScoreRate(scoreRate);
            record.setUseTimeSec(body.getUseTimeSec() != null ? body.getUseTimeSec() : 0);
            record.setJudgeType(judgeType);
            record.setJudgeResult(judgeResult);
            record.setDifficultyLevel(question.getDifficultyLevel());
            record.setAnswerTime(now);
            record.setCreateTime(now);
            practiceRecordService.save(record);

            ExamStudentPaperAnswer pa = new ExamStudentPaperAnswer();
            pa.setStudentPaperId(studentPaperId);
            pa.setStudentId(studentId);
            pa.setPaperId(paper.getId());
            pa.setQuestionId(question.getId());
            pa.setStudentAnswer(userAnswer);
            pa.setIsCorrect(correct ? 1 : 0);
            pa.setScore(score);
            pa.setScoreRate(scoreRate);
            pa.setJudgeType(judgeType);
            pa.setJudgeResult(judgeResult);
            pa.setCreateTime(now);
            examStudentPaperAnswerService.save(pa);
        }
        studentPaper.setTotalScore(totalScore);
        examStudentPaperService.updateById(studentPaper);

        Map<String, Object> data = new HashMap<>();
        data.put("totalCount", paperQuestions.size());
        data.put("correctCount", correctCount);
        data.put("wrongCount", wrongCount);
        data.put("totalScore", totalScore);
        data.put("paperTotalScore", paper.getTotalScore());
        return Result.success(data);
    }

    /**
     * 学生端：考试记录列表（来源于 exam_student_paper）
     */
    @GetMapping("/exam/records")
    public Result examRecords(HttpServletRequest request) {
        Long studentId = getStudentId(request);
        if (studentId == null) return Result.error(401, "请先登录");
        ExamStudentPaper query = new ExamStudentPaper();
        query.setStudentId(studentId);
        List<ExamStudentPaper> list = examStudentPaperService.listQuery(query);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ExamStudentPaper sp : list) {
            ExamStudentPaperAnswer aq = new ExamStudentPaperAnswer();
            aq.setStudentPaperId(sp.getId());
            List<ExamStudentPaperAnswer> answers = examStudentPaperAnswerService.listQuery(aq);
            int correctCount = answers.stream().mapToInt(a -> a.getIsCorrect() != null && a.getIsCorrect() == 1 ? 1 : 0).sum();
            Map<String, Object> item = new HashMap<>();
            item.put("id", sp.getId());
            item.put("paperId", sp.getPaperId());
            item.put("paperName", sp.getPaperName());
            item.put("courseName", sp.getCourseName());
            item.put("answerTime", sp.getSubmitTime());
            item.put("totalScore", sp.getTotalScore());
            item.put("paperTotalScore", sp.getPaperTotalScore());
            item.put("totalCount", answers.size());
            item.put("correctCount", correctCount);
            result.add(item);
        }
        return Result.success(result);
    }

    /**
     * 学生端：某次考试提交详情（来源于 exam_student_paper_answer，按 studentPaperId）
     */
    @GetMapping("/exam/record/detail")
    public Result examRecordDetail(HttpServletRequest request, @RequestParam Long id) {
        Long studentId = getStudentId(request);
        if (studentId == null) return Result.error(401, "请先登录");
        ExamStudentPaper sp = examStudentPaperService.getById(id);
        if (sp == null) return Result.error("记录不存在");
        if (!sp.getStudentId().equals(studentId)) return Result.error(403, "无权查看");
        ExamStudentPaperAnswer query = new ExamStudentPaperAnswer();
        query.setStudentPaperId(id);
        List<ExamStudentPaperAnswer> list = examStudentPaperAnswerService.listQuery(query);
        list.sort(Comparator.comparing(ExamStudentPaperAnswer::getCreateTime));
        return Result.success(list);
    }

    /**
     * 答案规范化。当前系统仅支持单选(1)、多选(2)、主观题(5)；填空(3)、判断(4)逻辑保留供后续使用。
     */
    private String normalizeAnswer(String val, Integer questionType) {
        if (val == null) return "";
        String s = val.trim().toUpperCase().replaceAll("\\s+", "");
        // 判断题(4)：保留供后续使用
        if (questionType != null && questionType == 4) {
            if (s.contains("正确") || "对".equals(s)) return "对";
            if (s.contains("错误") || "错".equals(s)) return "错";
        }
        return s;
    }

    /**
     * 多选题答案规范化：支持 "AB"、"BA"、"A,B"、"B,A" 等，统一为按字母序的 "A,B" 再比较。
     */
    private String normalizeMultiAnswer(String val) {
        if (val == null) return "";
        String s = val.trim().toUpperCase().replaceAll("\\s+", "");
        java.util.Set<String> set = new java.util.TreeSet<>();
        for (String part : s.split("[,，、;；]+")) {
            String p = part.trim();
            for (int i = 0; i < p.length(); i++) {
                char c = p.charAt(i);
                if (Character.isLetter(c)) set.add(String.valueOf(c));
            }
        }
        return String.join(",", set);
    }

    private String aiScoreSubjective(String title, String standardAnswer, String userAnswer) {
        String systemPrompt = "你是一名法考阅卷老师，请根据题目、标准答案对考生答案进行打分和简短点评。只考虑要点与表述。请严格按以下 JSON 格式返回，不要包含其他内容：{\"score\": 分数(0-100的整数), \"comment\": \"简短点评\"}";
        String userPrompt = String.format("题目：%s\n\n标准答案：%s\n\n考生答案：%s\n\n请给出 score 和 comment。",
                title != null ? title : "", standardAnswer != null ? standardAnswer : "", userAnswer);
        String res = aiChatClient.chat(systemPrompt, userPrompt);
        if (res == null || res.trim().isEmpty()) return "{\"score\":0,\"comment\":\"AI未返回\"}";
        String cleaned = res.trim();
        if (cleaned.startsWith("```json")) cleaned = cleaned.substring(7);
        if (cleaned.startsWith("```")) cleaned = cleaned.substring(3);
        if (cleaned.endsWith("```")) cleaned = cleaned.substring(0, cleaned.length() - 3);
        return cleaned.trim();
    }

    @Data
    public static class SubmitExamRequest {
        private Long paperId;
        private Integer useTimeSec;
        private List<AnswerItem> answers;
    }

    @Data
    public static class AnswerItem {
        private Long questionId;
        private String answer;
    }
}
