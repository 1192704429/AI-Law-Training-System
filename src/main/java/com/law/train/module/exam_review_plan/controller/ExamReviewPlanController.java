package com.law.train.module.exam_review_plan.controller;

import com.law.train.common.Result;
import com.law.train.module.exam_review_plan.entity.ExamReviewPlan;
import com.law.train.module.exam_review_plan.service.ExamReviewPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import com.law.train.vo.SelectVO;

/**
 * @author wuming
 * @description: 学生复习计划Controller
 */
@RestController
@RequestMapping("/exam_review_plan")
public class ExamReviewPlanController {

    @Autowired
    private ExamReviewPlanService examReviewPlanService;

    /**
     * 分页查询学生复习计划列表
     */
    @PostMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestBody(required = false) ExamReviewPlan entity) {
        if (entity == null) {
            entity = new ExamReviewPlan();
        }
        return Result.success(examReviewPlanService.pageQuery(current, size, entity));
    }

    /**
     * 查询所有学生复习计划列表
     */
    @PostMapping("/list")
    public Result list(@RequestBody(required = false) ExamReviewPlan entity) {
        if (entity == null) {
            entity = new ExamReviewPlan();
        }
        List<ExamReviewPlan> list = examReviewPlanService.listQuery(entity);
        return Result.success(list);
    }

    /**
     * 根据ID查询学生复习计划
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        ExamReviewPlan examReviewPlan = examReviewPlanService.getById(id);
        if (examReviewPlan == null) {
            return Result.error("学生复习计划不存在");
        }
        return Result.success(examReviewPlan);
    }

    /**
     * 新增学生复习计划
     */
    @PostMapping
    public Result save(@RequestBody ExamReviewPlan examReviewPlan) {
        examReviewPlan.setCreateTime(LocalDateTime.now());
        boolean success = examReviewPlanService.save(examReviewPlan);
        if (success) {
            return Result.success("新增成功", examReviewPlan);
        }
        return Result.error("新增失败");
    }

    /**
     * 更新学生复习计划
     */
    @PutMapping
    public Result update(@RequestBody ExamReviewPlan examReviewPlan) {
        boolean success = examReviewPlanService.updateById(examReviewPlan);
        if (success) {
            return Result.success("更新成功", examReviewPlan);
        }
        return Result.error("更新失败");
    }

    /**
     * 根据ID删除学生复习计划
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        boolean success = examReviewPlanService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除学生复习计划
     */
    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        boolean success = examReviewPlanService.removeByIds(ids);
        if (success) {
            return Result.success("批量删除成功");
        }
        return Result.error("批量删除失败");
    }

    /**
     * 下拉数据接口：exam_student
     */
    @GetMapping("/select/listExamStudent")
    public Result listExamStudent() {
        List<SelectVO> list = examReviewPlanService.listExamStudent();
        return Result.success(list);
    }

}
