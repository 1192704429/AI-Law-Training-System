package com.law.train.basic.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.law.train.common.Result;
import com.law.train.module.exam_course.entity.ExamCourse;
import com.law.train.module.exam_course.service.ExamCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 学生端课程相关接口（刷题等）
 */
@RestController
@RequestMapping("/student")
public class StudentCourseController {

    @Autowired
    private ExamCourseService examCourseService;

    /**
     * 分页获取状态启用的课程列表（学生端刷题选课用）
     */
    @GetMapping("/courses")
    public Result pageEnabledCourses(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "8") Integer size,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) Integer difficultyLevel) {
        ExamCourse query = new ExamCourse();
        query.setStatus(1);
        if (courseName != null && !courseName.trim().isEmpty()) {
            query.setCourseName(courseName.trim());
        }
        if (difficultyLevel != null) {
            query.setDifficultyLevel(difficultyLevel);
        }
        IPage<ExamCourse> page = examCourseService.pageQuery(current, size, query);
        return Result.success(page);
    }
}
