package com.law.train.module.exam_question_practice_record.controller;

import com.law.train.common.Result;
import com.law.train.module.exam_question_practice_record.entity.ExamQuestionPracticeRecord;
import com.law.train.module.exam_question_practice_record.service.ExamQuestionPracticeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import com.law.train.vo.SelectVO;

/**
 * @author wuming
 * @description: 题目作答记录Controller
 */
@RestController
@RequestMapping("/exam_question_practice_record")
public class ExamQuestionPracticeRecordController {

    @Autowired
    private ExamQuestionPracticeRecordService examQuestionPracticeRecordService;

    /**
     * 分页查询题目作答记录列表
     */
    @PostMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestBody(required = false) ExamQuestionPracticeRecord entity) {
        if (entity == null) {
            entity = new ExamQuestionPracticeRecord();
        }
        return Result.success(examQuestionPracticeRecordService.pageQuery(current, size, entity));
    }

    /**
     * 查询所有题目作答记录列表
     */
    @PostMapping("/list")
    public Result list(@RequestBody(required = false) ExamQuestionPracticeRecord entity) {
        if (entity == null) {
            entity = new ExamQuestionPracticeRecord();
        }
        List<ExamQuestionPracticeRecord> list = examQuestionPracticeRecordService.listQuery(entity);
        return Result.success(list);
    }

    /**
     * 根据ID查询题目作答记录
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        ExamQuestionPracticeRecord examQuestionPracticeRecord = examQuestionPracticeRecordService.getById(id);
        if (examQuestionPracticeRecord == null) {
            return Result.error("题目作答记录不存在");
        }
        return Result.success(examQuestionPracticeRecord);
    }

    /**
     * 新增题目作答记录
     */
    @PostMapping
    public Result save(@RequestBody ExamQuestionPracticeRecord examQuestionPracticeRecord) {
        examQuestionPracticeRecord.setCreateTime(LocalDateTime.now());
        if (examQuestionPracticeRecord.getAnswerTime() == null) {
            examQuestionPracticeRecord.setAnswerTime(LocalDateTime.now());
        }
        boolean success = examQuestionPracticeRecordService.save(examQuestionPracticeRecord);
        if (success) {
            return Result.success("新增成功", examQuestionPracticeRecord);
        }
        return Result.error("新增失败");
    }

    /**
     * 更新题目作答记录
     */
    @PutMapping
    public Result update(@RequestBody ExamQuestionPracticeRecord examQuestionPracticeRecord) {
        boolean success = examQuestionPracticeRecordService.updateById(examQuestionPracticeRecord);
        if (success) {
            return Result.success("更新成功", examQuestionPracticeRecord);
        }
        return Result.error("更新失败");
    }

    /**
     * 根据ID删除题目作答记录
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        boolean success = examQuestionPracticeRecordService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除题目作答记录
     */
    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        boolean success = examQuestionPracticeRecordService.removeByIds(ids);
        if (success) {
            return Result.success("批量删除成功");
        }
        return Result.error("批量删除失败");
    }

    /**
     * 下拉数据接口：exam_course
     */
    @GetMapping("/select/listExamCourse")
    public Result listExamCourse() {
        List<SelectVO> list = examQuestionPracticeRecordService.listExamCourse();
        return Result.success(list);
    }

    /**
     * 下拉数据接口：exam_student
     */
    @GetMapping("/select/listExamStudent")
    public Result listExamStudent() {
        List<SelectVO> list = examQuestionPracticeRecordService.listExamStudent();
        return Result.success(list);
    }

    /**
     * 下拉数据接口：exam_review_plan
     */
    @GetMapping("/select/listExamReviewPlan")
    public Result listExamReviewPlan() {
        List<SelectVO> list = examQuestionPracticeRecordService.listExamReviewPlan();
        return Result.success(list);
    }

    /**
     * 下拉数据接口：exam_paper
     */
    @GetMapping("/select/listExamPaper")
    public Result listExamPaper() {
        List<SelectVO> list = examQuestionPracticeRecordService.listExamPaper();
        return Result.success(list);
    }

    /**
     * 下拉数据接口：exam_question
     */
    @GetMapping("/select/listExamQuestion")
    public Result listExamQuestion() {
        List<SelectVO> list = examQuestionPracticeRecordService.listExamQuestion();
        return Result.success(list);
    }

}
