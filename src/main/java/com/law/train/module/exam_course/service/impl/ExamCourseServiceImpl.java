package com.law.train.module.exam_course.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.law.train.module.exam_course.entity.ExamCourse;
import com.law.train.module.exam_course.mapper.ExamCourseMapper;
import com.law.train.module.exam_course.service.ExamCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wuming
 * @description: 课程Service实现类
 */
@Service
public class ExamCourseServiceImpl extends ServiceImpl<ExamCourseMapper, ExamCourse> implements ExamCourseService {

    @Autowired
    private ExamCourseMapper examCourseMapper;

    @Override
    public IPage<ExamCourse> pageQuery(Integer current, Integer size, ExamCourse entity) {
        Long offset = (long) (current - 1) * size;
        Long limit = (long) size;
        List<ExamCourse> records = examCourseMapper.pageQuery(entity, offset, limit);
        Long total = examCourseMapper.countQuery(entity);

        Page<ExamCourse> page = new Page<>(current, size);
        page.setRecords(records);
        page.setTotal(total);
        return page;
    }

    @Override
    public List<ExamCourse> listQuery(ExamCourse entity) {
        return examCourseMapper.listQuery(entity);
    }

    @Override
    public ExamCourse getById(Long id) {
        return examCourseMapper.getById(id);
    }

}
