package com.law.train.module.exam_question_option.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.law.train.module.exam_question_option.entity.ExamQuestionOption;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 题目选项Service接口
 */
public interface ExamQuestionOptionService extends IService<ExamQuestionOption> {

    /**
     * 分页查询
     */
    IPage<ExamQuestionOption> pageQuery(Integer current, Integer size, ExamQuestionOption entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamQuestionOption> listQuery(ExamQuestionOption entity);

    /**
     * 根据ID查询
     */
    ExamQuestionOption getById(Long id);

    /**
     * 下拉数据：exam_question
     */
    List<SelectVO> listExamQuestion();

}
