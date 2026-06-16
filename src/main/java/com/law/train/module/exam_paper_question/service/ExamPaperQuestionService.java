package com.law.train.module.exam_paper_question.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.law.train.module.exam_paper_question.entity.ExamPaperQuestion;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 试卷题目关联Service接口
 */
public interface ExamPaperQuestionService extends IService<ExamPaperQuestion> {

    /**
     * 分页查询
     */
    IPage<ExamPaperQuestion> pageQuery(Integer current, Integer size, ExamPaperQuestion entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamPaperQuestion> listQuery(ExamPaperQuestion entity);

    /**
     * 根据ID查询
     */
    ExamPaperQuestion getById(Long id);

    /**
     * 下拉数据：exam_paper
     */
    List<SelectVO> listExamPaper();

    /**
     * 下拉数据：exam_question
     */
    List<SelectVO> listExamQuestion();

}
