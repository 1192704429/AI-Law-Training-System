package com.law.train.module.exam_question.controller;

import com.law.train.common.Result;
import com.law.train.module.exam_question.entity.ExamQuestion;
import com.law.train.module.exam_question.service.ExamQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import com.law.train.vo.SelectVO;

/**
 * @author wuming
 * @description: 题目Controller
 */
@RestController
@RequestMapping("/exam_question")
public class ExamQuestionController {

    @Autowired
    private ExamQuestionService examQuestionService;

    /**
     * 分页查询题目列表
     */
    @PostMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestBody(required = false) ExamQuestion entity) {
        if (entity == null) {
            entity = new ExamQuestion();
        }
        return Result.success(examQuestionService.pageQuery(current, size, entity));
    }

    /**
     * 查询所有题目列表
     */
    @PostMapping("/list")
    public Result list(@RequestBody(required = false) ExamQuestion entity) {
        if (entity == null) {
            entity = new ExamQuestion();
        }
        List<ExamQuestion> list = examQuestionService.listQuery(entity);
        return Result.success(list);
    }

    /**
     * 根据ID查询题目
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        ExamQuestion examQuestion = examQuestionService.getById(id);
        if (examQuestion == null) {
            return Result.error("题目不存在");
        }
        return Result.success(examQuestion);
    }

    /**
     * 新增题目
     */
    @PostMapping
    public Result save(@RequestBody ExamQuestion examQuestion) {
        examQuestion.setCreateTime(LocalDateTime.now());
        boolean success = examQuestionService.save(examQuestion);
        if (success) {
            return Result.success("新增成功", examQuestion);
        }
        return Result.error("新增失败");
    }

    /**
     * 更新题目
     */
    @PutMapping
    public Result update(@RequestBody ExamQuestion examQuestion) {
        boolean success = examQuestionService.updateById(examQuestion);
        if (success) {
            return Result.success("更新成功", examQuestion);
        }
        return Result.error("更新失败");
    }

    /**
     * 根据ID删除题目
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        boolean success = examQuestionService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除题目
     */
    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        boolean success = examQuestionService.removeByIds(ids);
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
        List<SelectVO> list = examQuestionService.listExamCourse();
        return Result.success(list);
    }

}
