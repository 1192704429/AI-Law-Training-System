package com.law.train.module.exam_question.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.law.train.module.exam_question.entity.ExamQuestion;
import com.law.train.module.exam_question.mapper.ExamQuestionMapper;
import com.law.train.module.exam_question.service.ExamQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 题目Service实现类
 */
@Service
public class ExamQuestionServiceImpl extends ServiceImpl<ExamQuestionMapper, ExamQuestion> implements ExamQuestionService {

    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    @Override
    public IPage<ExamQuestion> pageQuery(Integer current, Integer size, ExamQuestion entity) {
        Long offset = (long) (current - 1) * size;
        Long limit = (long) size;
        List<ExamQuestion> records = examQuestionMapper.pageQuery(entity, offset, limit);
        Long total = examQuestionMapper.countQuery(entity);

        Page<ExamQuestion> page = new Page<>(current, size);
        page.setRecords(records);
        page.setTotal(total);
        return page;
    }

    @Override
    public List<ExamQuestion> listQuery(ExamQuestion entity) {
        return examQuestionMapper.listQuery(entity);
    }

    @Override
    public ExamQuestion getRandomOne(ExamQuestion entity) {
        return examQuestionMapper.getRandomOne(entity);
    }

    @Override
    public ExamQuestion getById(Long id) {
        return examQuestionMapper.getById(id);
    }

    /**
     * 下拉数据：exam_course
     */
    @Override
    public List<SelectVO> listExamCourse() {
        return examQuestionMapper.listExamCourse();
    }

}
