package com.law.train.module.exam_wrong_question_book.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.law.train.module.exam_wrong_question_book.entity.ExamWrongQuestionBook;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 学生错题本Service接口
 */
public interface ExamWrongQuestionBookService extends IService<ExamWrongQuestionBook> {

    /**
     * 分页查询
     */
    IPage<ExamWrongQuestionBook> pageQuery(Integer current, Integer size, ExamWrongQuestionBook entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamWrongQuestionBook> listQuery(ExamWrongQuestionBook entity);

    /**
     * 根据ID查询
     */
    ExamWrongQuestionBook getById(Long id);

    /**
     * 下拉数据：exam_course
     */
    List<SelectVO> listExamCourse();

    /**
     * 下拉数据：exam_question_practice_record
     */
    List<SelectVO> listExamQuestionPracticeRecord();

    /**
     * 下拉数据：exam_student
     */
    List<SelectVO> listExamStudent();

    /**
     * 下拉数据：exam_question
     */
    List<SelectVO> listExamQuestion();

}
