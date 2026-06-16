package com.law.train.module.exam_student_paper_answer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.law.train.module.exam_student_paper_answer.entity.ExamStudentPaperAnswer;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 学生试卷答案Service接口
 */
public interface ExamStudentPaperAnswerService extends IService<ExamStudentPaperAnswer> {

    /**
     * 分页查询
     */
    IPage<ExamStudentPaperAnswer> pageQuery(Integer current, Integer size, ExamStudentPaperAnswer entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamStudentPaperAnswer> listQuery(ExamStudentPaperAnswer entity);

    /**
     * 根据ID查询
     */
    ExamStudentPaperAnswer getById(Long id);

    /**
     * 下拉数据：exam_student
     */
    List<SelectVO> listExamStudent();

    /**
     * 下拉数据：exam_paper
     */
    List<SelectVO> listExamPaper();

    /**
     * 下拉数据：exam_question
     */
    List<SelectVO> listExamQuestion();

}
