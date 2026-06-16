package com.law.train.module.exam_review_plan_detail.controller;

import com.law.train.common.Result;
import com.law.train.module.exam_review_plan_detail.entity.ExamReviewPlanDetail;
import com.law.train.module.exam_review_plan_detail.service.ExamReviewPlanDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import com.law.train.vo.SelectVO;

/**
 * @author wuming
 * @description: 复习计划明细Controller
 */
@RestController
@RequestMapping("/exam_review_plan_detail")
public class ExamReviewPlanDetailController {

    @Autowired
    private ExamReviewPlanDetailService examReviewPlanDetailService;

    /**
     * 分页查询复习计划明细列表
     */
    @PostMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestBody(required = false) ExamReviewPlanDetail entity) {
        if (entity == null) {
            entity = new ExamReviewPlanDetail();
        }
        return Result.success(examReviewPlanDetailService.pageQuery(current, size, entity));
    }

    /**
     * 查询所有复习计划明细列表
     */
    @PostMapping("/list")
    public Result list(@RequestBody(required = false) ExamReviewPlanDetail entity) {
        if (entity == null) {
            entity = new ExamReviewPlanDetail();
        }
        List<ExamReviewPlanDetail> list = examReviewPlanDetailService.listQuery(entity);
        return Result.success(list);
    }

    /**
     * 根据ID查询复习计划明细
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        ExamReviewPlanDetail examReviewPlanDetail = examReviewPlanDetailService.getById(id);
        if (examReviewPlanDetail == null) {
            return Result.error("复习计划明细不存在");
        }
        return Result.success(examReviewPlanDetail);
    }

    /**
     * 新增复习计划明细
     */
    @PostMapping
    public Result save(@RequestBody ExamReviewPlanDetail examReviewPlanDetail) {
        examReviewPlanDetail.setCreateTime(LocalDateTime.now());
        boolean success = examReviewPlanDetailService.save(examReviewPlanDetail);
        if (success) {
            return Result.success("新增成功", examReviewPlanDetail);
        }
        return Result.error("新增失败");
    }

    /**
     * 更新复习计划明细
     */
    @PutMapping
    public Result update(@RequestBody ExamReviewPlanDetail examReviewPlanDetail) {
        boolean success = examReviewPlanDetailService.updateById(examReviewPlanDetail);
        if (success) {
            return Result.success("更新成功", examReviewPlanDetail);
        }
        return Result.error("更新失败");
    }

    /**
     * 根据ID删除复习计划明细
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        boolean success = examReviewPlanDetailService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除复习计划明细
     */
    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        boolean success = examReviewPlanDetailService.removeByIds(ids);
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
        List<SelectVO> list = examReviewPlanDetailService.listExamCourse();
        return Result.success(list);
    }

    /**
     * 下拉数据接口：exam_question_practice_record
     */
    @GetMapping("/select/listExamQuestionPracticeRecord")
    public Result listExamQuestionPracticeRecord() {
        List<SelectVO> list = examReviewPlanDetailService.listExamQuestionPracticeRecord();
        return Result.success(list);
    }

    /**
     * 下拉数据接口：exam_review_plan
     */
    @GetMapping("/select/listExamReviewPlan")
    public Result listExamReviewPlan() {
        List<SelectVO> list = examReviewPlanDetailService.listExamReviewPlan();
        return Result.success(list);
    }

    /**
     * 下拉数据接口：exam_student
     */
    @GetMapping("/select/listExamStudent")
    public Result listExamStudent() {
        List<SelectVO> list = examReviewPlanDetailService.listExamStudent();
        return Result.success(list);
    }

    /**
     * 下拉数据接口：exam_question
     */
    @GetMapping("/select/listExamQuestion")
    public Result listExamQuestion() {
        List<SelectVO> list = examReviewPlanDetailService.listExamQuestion();
        return Result.success(list);
    }

}
