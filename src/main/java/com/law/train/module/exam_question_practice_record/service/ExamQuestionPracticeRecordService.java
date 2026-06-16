package com.law.train.module.exam_question_practice_record.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.law.train.module.exam_question_practice_record.entity.ExamQuestionPracticeRecord;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 题目作答记录Service接口
 */
public interface ExamQuestionPracticeRecordService extends IService<ExamQuestionPracticeRecord> {

    /**
     * 分页查询
     */
    IPage<ExamQuestionPracticeRecord> pageQuery(Integer current, Integer size, ExamQuestionPracticeRecord entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamQuestionPracticeRecord> listQuery(ExamQuestionPracticeRecord entity);

    /**
     * 根据ID查询
     */
    ExamQuestionPracticeRecord getById(Long id);

    /**
     * 下拉数据：exam_course
     */
    List<SelectVO> listExamCourse();

    /**
     * 下拉数据：exam_student
     */
    List<SelectVO> listExamStudent();

    /**
     * 下拉数据：exam_review_plan
     */
    List<SelectVO> listExamReviewPlan();

    /**
     * 下拉数据：exam_paper
     */
    List<SelectVO> listExamPaper();

    /**
     * 下拉数据：exam_question
     */
    List<SelectVO> listExamQuestion();

}
