package com.law.train.module.exam_student_paper.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.law.train.module.exam_student_paper.entity.ExamStudentPaper;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 学生试卷作答Service接口
 */
public interface ExamStudentPaperService extends IService<ExamStudentPaper> {

    /**
     * 分页查询
     */
    IPage<ExamStudentPaper> pageQuery(Integer current, Integer size, ExamStudentPaper entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamStudentPaper> listQuery(ExamStudentPaper entity);

    /**
     * 根据ID查询
     */
    ExamStudentPaper getById(Long id);

    /**
     * 下拉数据：exam_student
     */
    List<SelectVO> listExamStudent();

    /**
     * 下拉数据：exam_paper
     */
    List<SelectVO> listExamPaper();

}
