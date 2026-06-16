package com.law.train.module.exam_course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.law.train.module.exam_course.entity.ExamCourse;

import java.util.List;

/**
 * @author wuming
 * @description: 课程Service接口
 */
public interface ExamCourseService extends IService<ExamCourse> {

    /**
     * 分页查询
     */
    IPage<ExamCourse> pageQuery(Integer current, Integer size, ExamCourse entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamCourse> listQuery(ExamCourse entity);

    /**
     * 根据ID查询
     */
    ExamCourse getById(Long id);

}
