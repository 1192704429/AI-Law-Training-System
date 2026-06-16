package com.law.train.module.exam_question_option.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.law.train.module.exam_question_option.entity.ExamQuestionOption;
import com.law.train.module.exam_question_option.mapper.ExamQuestionOptionMapper;
import com.law.train.module.exam_question_option.service.ExamQuestionOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 题目选项Service实现类
 */
@Service
public class ExamQuestionOptionServiceImpl extends ServiceImpl<ExamQuestionOptionMapper, ExamQuestionOption> implements ExamQuestionOptionService {

    @Autowired
    private ExamQuestionOptionMapper examQuestionOptionMapper;

    @Override
    public IPage<ExamQuestionOption> pageQuery(Integer current, Integer size, ExamQuestionOption entity) {
        Long offset = (long) (current - 1) * size;
        Long limit = (long) size;
        List<ExamQuestionOption> records = examQuestionOptionMapper.pageQuery(entity, offset, limit);
        Long total = examQuestionOptionMapper.countQuery(entity);

        Page<ExamQuestionOption> page = new Page<>(current, size);
        page.setRecords(records);
        page.setTotal(total);
        return page;
    }

    @Override
    public List<ExamQuestionOption> listQuery(ExamQuestionOption entity) {
        return examQuestionOptionMapper.listQuery(entity);
    }

    @Override
    public ExamQuestionOption getById(Long id) {
        return examQuestionOptionMapper.getById(id);
    }

    /**
     * 下拉数据：exam_question
     */
    @Override
    public List<SelectVO> listExamQuestion() {
        return examQuestionOptionMapper.listExamQuestion();
    }

}
