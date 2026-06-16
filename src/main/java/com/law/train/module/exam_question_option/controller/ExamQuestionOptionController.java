package com.law.train.module.exam_question_option.controller;

import com.law.train.common.Result;
import com.law.train.module.exam_question_option.entity.ExamQuestionOption;
import com.law.train.module.exam_question_option.service.ExamQuestionOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import com.law.train.vo.SelectVO;

/**
 * @author wuming
 * @description: 题目选项Controller
 */
@RestController
@RequestMapping("/exam_question_option")
public class ExamQuestionOptionController {

    @Autowired
    private ExamQuestionOptionService examQuestionOptionService;

    /**
     * 分页查询题目选项列表
     */
    @PostMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestBody(required = false) ExamQuestionOption entity) {
        if (entity == null) {
            entity = new ExamQuestionOption();
        }
        return Result.success(examQuestionOptionService.pageQuery(current, size, entity));
    }

    /**
     * 查询所有题目选项列表
     */
    @PostMapping("/list")
    public Result list(@RequestBody(required = false) ExamQuestionOption entity) {
        if (entity == null) {
            entity = new ExamQuestionOption();
        }
        List<ExamQuestionOption> list = examQuestionOptionService.listQuery(entity);
        return Result.success(list);
    }

    /**
     * 根据ID查询题目选项
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        ExamQuestionOption examQuestionOption = examQuestionOptionService.getById(id);
        if (examQuestionOption == null) {
            return Result.error("题目选项不存在");
        }
        return Result.success(examQuestionOption);
    }

    /**
     * 新增题目选项
     */
    @PostMapping
    public Result save(@RequestBody ExamQuestionOption examQuestionOption) {
        boolean success = examQuestionOptionService.save(examQuestionOption);
        if (success) {
            return Result.success("新增成功", examQuestionOption);
        }
        return Result.error("新增失败");
    }

    /**
     * 更新题目选项
     */
    @PutMapping
    public Result update(@RequestBody ExamQuestionOption examQuestionOption) {
        boolean success = examQuestionOptionService.updateById(examQuestionOption);
        if (success) {
            return Result.success("更新成功", examQuestionOption);
        }
        return Result.error("更新失败");
    }

    /**
     * 根据ID删除题目选项
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        boolean success = examQuestionOptionService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除题目选项
     */
    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        boolean success = examQuestionOptionService.removeByIds(ids);
        if (success) {
            return Result.success("批量删除成功");
        }
        return Result.error("批量删除失败");
    }

    /**
     * 下拉数据接口：exam_question
     */
    @GetMapping("/select/listExamQuestion")
    public Result listExamQuestion() {
        List<SelectVO> list = examQuestionOptionService.listExamQuestion();
        return Result.success(list);
    }

}
