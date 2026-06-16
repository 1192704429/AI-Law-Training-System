package com.law.train.module.exam_course.controller;

import com.law.train.common.Result;
import com.law.train.module.exam_course.entity.ExamCourse;
import com.law.train.module.exam_course.service.ExamCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wuming
 * @description: 课程Controller
 */
@RestController
@RequestMapping("/exam_course")
public class ExamCourseController {

    @Autowired
    private ExamCourseService examCourseService;

    /**
     * 分页查询课程列表
     */
    @PostMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestBody(required = false) ExamCourse entity) {
        if (entity == null) {
            entity = new ExamCourse();
        }
        return Result.success(examCourseService.pageQuery(current, size, entity));
    }

    /**
     * 查询所有课程列表
     */
    @PostMapping("/list")
    public Result list(@RequestBody(required = false) ExamCourse entity) {
        if (entity == null) {
            entity = new ExamCourse();
        }
        List<ExamCourse> list = examCourseService.listQuery(entity);
        return Result.success(list);
    }

    /**
     * 根据ID查询课程
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        ExamCourse examCourse = examCourseService.getById(id);
        if (examCourse == null) {
            return Result.error("课程不存在");
        }
        return Result.success(examCourse);
    }

    /**
     * 新增课程
     */
    @PostMapping
    public Result save(@RequestBody ExamCourse examCourse) {
        examCourse.setCreateTime(LocalDateTime.now());
        boolean success = examCourseService.save(examCourse);
        if (success) {
            return Result.success("新增成功", examCourse);
        }
        return Result.error("新增失败");
    }

    /**
     * 更新课程
     */
    @PutMapping
    public Result update(@RequestBody ExamCourse examCourse) {
        boolean success = examCourseService.updateById(examCourse);
        if (success) {
            return Result.success("更新成功", examCourse);
        }
        return Result.error("更新失败");
    }

    /**
     * 根据ID删除课程
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        boolean success = examCourseService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除课程
     */
    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        boolean success = examCourseService.removeByIds(ids);
        if (success) {
            return Result.success("批量删除成功");
        }
        return Result.error("批量删除失败");
    }

}
