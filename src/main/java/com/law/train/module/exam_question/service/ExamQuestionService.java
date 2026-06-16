package com.law.train.module.exam_question.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.law.train.module.exam_question.entity.ExamQuestion;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 题目Service接口
 */
public interface ExamQuestionService extends IService<ExamQuestion> {

    /**
     * 分页查询
     */
    IPage<ExamQuestion> pageQuery(Integer current, Integer size, ExamQuestion entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamQuestion> listQuery(ExamQuestion entity);

    /**
     * 随机取一道题（按 entity 条件）
     */
    ExamQuestion getRandomOne(ExamQuestion entity);

    /**
     * 根据ID查询
     */
    ExamQuestion getById(Long id);

    /**
     * 下拉数据：exam_course
     */
    List<SelectVO> listExamCourse();

}
