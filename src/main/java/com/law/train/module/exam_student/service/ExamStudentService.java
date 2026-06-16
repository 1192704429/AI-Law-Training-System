package com.law.train.module.exam_student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.law.train.module.exam_student.entity.ExamStudent;

import java.util.List;

/**
 * @author wuming
 * @description: 学生Service接口
 */
public interface ExamStudentService extends IService<ExamStudent> {

    /**
     * 分页查询
     */
    IPage<ExamStudent> pageQuery(Integer current, Integer size, ExamStudent entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamStudent> listQuery(ExamStudent entity);

    /**
     * 根据ID查询
     */
    ExamStudent getById(Long id);

}
