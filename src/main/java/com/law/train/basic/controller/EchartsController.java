package com.law.train.basic.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.law.train.basic.service.EchartsService;
import com.law.train.module.exam_question_practice_record.entity.ExamQuestionPracticeRecord;
import com.law.train.module.exam_question_practice_record.service.ExamQuestionPracticeRecordService;
import com.law.train.module.exam_review_plan.service.ExamReviewPlanService;
import com.law.train.module.exam_student_question_memory.entity.ExamStudentQuestionMemory;
import com.law.train.module.exam_student_question_memory.service.ExamStudentQuestionMemoryService;
import com.law.train.module.exam_wrong_question_book.entity.ExamWrongQuestionBook;
import com.law.train.module.exam_wrong_question_book.service.ExamWrongQuestionBookService;
import com.law.train.vo.EchartsDataVO;
import com.law.train.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 图表可视化接口
 */
@RestController
@RequestMapping("/echarts")
public class EchartsController {

    @Autowired
    private EchartsService echartsService;
    @Autowired
    private ExamStudentQuestionMemoryService examStudentQuestionMemoryService;
    @Autowired
    private ExamWrongQuestionBookService examWrongQuestionBookService;
    @Autowired
    private ExamQuestionPracticeRecordService examQuestionPracticeRecordService;
    @Autowired
    private ExamReviewPlanService examReviewPlanService;

    /**
     * 学生性别分布统计
     */
    @PostMapping("/countStudentByGender")
    public Result countStudentByGender(@RequestBody Map<String, Object> params) {
        // 从 RequestBody 中提取参数
        Integer status = Optional.ofNullable(params.get("status"))
                .map(Object::toString)
                .filter(s -> !s.isBlank())
                .map(Integer::valueOf)
                .orElse(null);
        List<EchartsDataVO> data = echartsService.countStudentByGender(status);
        return Result.success(data);
    }

    /**
     * 课程难度分布统计
     */
    @PostMapping("/countCourseByDifficulty")
    public Result countCourseByDifficulty(@RequestBody Map<String, Object> params) {
        // 从 RequestBody 中提取参数
        Integer status = Optional.ofNullable(params.get("status"))
                .map(Object::toString)
                .filter(s -> !s.isBlank())
                .map(Integer::valueOf)
                .orElse(null);
        List<EchartsDataVO> data = echartsService.countCourseByDifficulty(status);
        return Result.success(data);
    }

    /**
     * 题目类型分布统计
     */
    @PostMapping("/countQuestionByType")
    public Result countQuestionByType(@RequestBody Map<String, Object> params) {
        // 从 RequestBody 中提取参数
        Integer status = Optional.ofNullable(params.get("status"))
                .map(Object::toString)
                .filter(s -> !s.isBlank())
                .map(Integer::valueOf)
                .orElse(null);
        List<EchartsDataVO> data = echartsService.countQuestionByType(status);
        return Result.success(data);
    }

    /**
     * 练习类型统计
     */
    @PostMapping("/countPracticeByType")
    public Result countPracticeByType(@RequestBody Map<String, Object> params) {
        // 从 RequestBody 中提取参数
        String startTime = Optional.ofNullable(params.get("startTime"))
                .map(Object::toString)
                .filter(s -> !s.isBlank())
                .orElse(null);
        String endTime = Optional.ofNullable(params.get("endTime"))
                .map(Object::toString)
                .filter(s -> !s.isBlank())
                .orElse(null);
        List<EchartsDataVO> data = echartsService.countPracticeByType(startTime, endTime);
        return Result.success(data);
    }

    /**
     * 复习计划状态统计
     */
    @GetMapping("/countReviewPlanByStatus")
    public Result countReviewPlanByStatus() {
        List<EchartsDataVO> data = echartsService.countReviewPlanByStatus();
        return Result.success(data);
    }

    /**
     * 试卷难度分布统计
     */
    @PostMapping("/countPaperByDifficulty")
    public Result countPaperByDifficulty(@RequestBody Map<String, Object> params) {
        // 从 RequestBody 中提取参数
        Integer paperType = Optional.ofNullable(params.get("paperType"))
                .map(Object::toString)
                .filter(s -> !s.isBlank())
                .map(Integer::valueOf)
                .orElse(null);
        Integer status = Optional.ofNullable(params.get("status"))
                .map(Object::toString)
                .filter(s -> !s.isBlank())
                .map(Integer::valueOf)
                .orElse(null);
        List<EchartsDataVO> data = echartsService.countPaperByDifficulty(paperType, status);
        return Result.success(data);
    }

    /**
     * 首页仪表盘：算法与业务数据统计（记忆表、错题本、练习记录、今日练习、待复习、复习计划）
     */
    @GetMapping("/dashboardStats")
    public Result dashboardStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        // 记忆表记录数（有效 status=1）
        long memoryCount = examStudentQuestionMemoryService.count(
            new LambdaQueryWrapper<ExamStudentQuestionMemory>().eq(ExamStudentQuestionMemory::getStatus, 1));

        // 错题本数量（有效 status=1）
        long wrongBookCount = examWrongQuestionBookService.count(
            new LambdaQueryWrapper<ExamWrongQuestionBook>().eq(ExamWrongQuestionBook::getStatus, 1));

        // 练习记录总数
        long practiceRecordCount = examQuestionPracticeRecordService.count();

        // 今日练习数（作答时间 >= 今日0点）
        long todayPracticeCount = examQuestionPracticeRecordService.count(
            new LambdaQueryWrapper<ExamQuestionPracticeRecord>()
                .ge(ExamQuestionPracticeRecord::getAnswerTime, todayStart));

        // 待复习题目数（下次复习时间已到且有效）
        long overdueReviewCount = examStudentQuestionMemoryService.count(
            new LambdaQueryWrapper<ExamStudentQuestionMemory>()
                .eq(ExamStudentQuestionMemory::getStatus, 1)
                .le(ExamStudentQuestionMemory::getNextReviewTime, now));

        // 复习计划总数
        long reviewPlanCount = examReviewPlanService.count();

        Map<String, Object> data = new HashMap<>();
        data.put("memoryCount", memoryCount);
        data.put("wrongBookCount", wrongBookCount);
        data.put("practiceRecordCount", practiceRecordCount);
        data.put("todayPracticeCount", todayPracticeCount);
        data.put("overdueReviewCount", overdueReviewCount);
        data.put("reviewPlanCount", reviewPlanCount);
        return Result.success(data);
    }

}
