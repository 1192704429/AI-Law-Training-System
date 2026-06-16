package com.law.train.module.exam_student.controller;

import com.law.train.common.Result;
import com.law.train.module.exam_student.entity.ExamStudent;
import com.law.train.module.exam_student.service.ExamStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wuming
 * @description: 学生Controller
 */
@RestController
@RequestMapping("/exam_student")
public class ExamStudentController {

    @Autowired
    private ExamStudentService examStudentService;

    /**
     * 分页查询学生列表
     */
    @PostMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestBody(required = false) ExamStudent entity) {
        if (entity == null) {
            entity = new ExamStudent();
        }
        return Result.success(examStudentService.pageQuery(current, size, entity));
    }

    /**
     * 查询所有学生列表
     */
    @PostMapping("/list")
    public Result list(@RequestBody(required = false) ExamStudent entity) {
        if (entity == null) {
            entity = new ExamStudent();
        }
        List<ExamStudent> list = examStudentService.listQuery(entity);
        return Result.success(list);
    }

    /**
     * 根据ID查询学生
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        ExamStudent examStudent = examStudentService.getById(id);
        if (examStudent == null) {
            return Result.error("学生不存在");
        }
        return Result.success(examStudent);
    }

    /**
     * 新增学生
     */
    @PostMapping
    public Result save(@RequestBody ExamStudent examStudent) {
        examStudent.setCreateTime(LocalDateTime.now());
        boolean success = examStudentService.save(examStudent);
        if (success) {
            return Result.success("新增成功", examStudent);
        }
        return Result.error("新增失败");
    }

    /**
     * 更新学生
     */
    @PutMapping
    public Result update(@RequestBody ExamStudent examStudent) {
        boolean success = examStudentService.updateById(examStudent);
        if (success) {
            return Result.success("更新成功", examStudent);
        }
        return Result.error("更新失败");
    }

    /**
     * 根据ID删除学生
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        boolean success = examStudentService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除学生
     */
    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        boolean success = examStudentService.removeByIds(ids);
        if (success) {
            return Result.success("批量删除成功");
        }
        return Result.error("批量删除失败");
    }

}
