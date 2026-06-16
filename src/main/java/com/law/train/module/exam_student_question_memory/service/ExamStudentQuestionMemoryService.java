package com.law.train.module.exam_student_question_memory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.law.train.module.exam_student_question_memory.entity.ExamStudentQuestionMemory;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 学生题目记忆状态Service接口
 */
public interface ExamStudentQuestionMemoryService extends IService<ExamStudentQuestionMemory> {

    /**
     * 分页查询
     */
    IPage<ExamStudentQuestionMemory> pageQuery(Integer current, Integer size, ExamStudentQuestionMemory entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamStudentQuestionMemory> listQuery(ExamStudentQuestionMemory entity);

    /**
     * 根据ID查询
     */
    ExamStudentQuestionMemory getById(Long id);

    /**
     * 下拉数据：exam_course
     */
    List<SelectVO> listExamCourse();

    /**
     * 下拉数据：exam_student
     */
    List<SelectVO> listExamStudent();

    /**
     * 下拉数据：exam_question
     */
    List<SelectVO> listExamQuestion();

    /**
     * 推荐题目：next_review_time <= now，按 recommend_priority desc, next_review_time asc
     * @param courseId 可选，指定则只返回该课程的推荐题
     */
    java.util.List<ExamStudentQuestionMemory> listRecommend(Long studentId, Long courseId, Integer limit);

}
