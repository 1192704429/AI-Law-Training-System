package com.law.train.module.exam_paper_question.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.law.train.module.exam_paper_question.entity.ExamPaperQuestion;
import com.law.train.module.exam_paper_question.mapper.ExamPaperQuestionMapper;
import com.law.train.module.exam_paper_question.service.ExamPaperQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 试卷题目关联Service实现类
 */
@Service
public class ExamPaperQuestionServiceImpl extends ServiceImpl<ExamPaperQuestionMapper, ExamPaperQuestion> implements ExamPaperQuestionService {

    @Autowired
    private ExamPaperQuestionMapper examPaperQuestionMapper;

    @Override
    public IPage<ExamPaperQuestion> pageQuery(Integer current, Integer size, ExamPaperQuestion entity) {
        Long offset = (long) (current - 1) * size;
        Long limit = (long) size;
        List<ExamPaperQuestion> records = examPaperQuestionMapper.pageQuery(entity, offset, limit);
        Long total = examPaperQuestionMapper.countQuery(entity);

        Page<ExamPaperQuestion> page = new Page<>(current, size);
        page.setRecords(records);
        page.setTotal(total);
        return page;
    }

    @Override
    public List<ExamPaperQuestion> listQuery(ExamPaperQuestion entity) {
        return examPaperQuestionMapper.listQuery(entity);
    }

    @Override
    public ExamPaperQuestion getById(Long id) {
        return examPaperQuestionMapper.getById(id);
    }

    /**
     * 下拉数据：exam_paper
     */
    @Override
    public List<SelectVO> listExamPaper() {
        return examPaperQuestionMapper.listExamPaper();
    }

    /**
     * 下拉数据：exam_question
     */
    @Override
    public List<SelectVO> listExamQuestion() {
        return examPaperQuestionMapper.listExamQuestion();
    }

}
