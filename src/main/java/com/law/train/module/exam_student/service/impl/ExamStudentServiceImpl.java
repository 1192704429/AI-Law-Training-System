package com.law.train.module.exam_student.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.law.train.module.exam_student.entity.ExamStudent;
import com.law.train.module.exam_student.mapper.ExamStudentMapper;
import com.law.train.module.exam_student.service.ExamStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wuming
 * @description: 学生Service实现类
 */
@Service
public class ExamStudentServiceImpl extends ServiceImpl<ExamStudentMapper, ExamStudent> implements ExamStudentService {

    @Autowired
    private ExamStudentMapper examStudentMapper;

    @Override
    public IPage<ExamStudent> pageQuery(Integer current, Integer size, ExamStudent entity) {
        Long offset = (long) (current - 1) * size;
        Long limit = (long) size;
        List<ExamStudent> records = examStudentMapper.pageQuery(entity, offset, limit);
        Long total = examStudentMapper.countQuery(entity);

        Page<ExamStudent> page = new Page<>(current, size);
        page.setRecords(records);
        page.setTotal(total);
        return page;
    }

    @Override
    public List<ExamStudent> listQuery(ExamStudent entity) {
        return examStudentMapper.listQuery(entity);
    }

    @Override
    public ExamStudent getById(Long id) {
        return examStudentMapper.getById(id);
    }

}
