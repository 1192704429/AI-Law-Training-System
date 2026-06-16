package com.law.train.module.exam_student_paper_answer.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.law.train.module.exam_student_paper_answer.entity.ExamStudentPaperAnswer;
import com.law.train.module.exam_student_paper_answer.mapper.ExamStudentPaperAnswerMapper;
import com.law.train.module.exam_student_paper_answer.service.ExamStudentPaperAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 学生试卷答案Service实现类
 */
@Service
public class ExamStudentPaperAnswerServiceImpl extends ServiceImpl<ExamStudentPaperAnswerMapper, ExamStudentPaperAnswer> implements ExamStudentPaperAnswerService {

    @Autowired
    private ExamStudentPaperAnswerMapper examStudentPaperAnswerMapper;

    @Override
    public IPage<ExamStudentPaperAnswer> pageQuery(Integer current, Integer size, ExamStudentPaperAnswer entity) {
        Long offset = (long) (current - 1) * size;
        Long limit = (long) size;
        List<ExamStudentPaperAnswer> records = examStudentPaperAnswerMapper.pageQuery(entity, offset, limit);
        Long total = examStudentPaperAnswerMapper.countQuery(entity);

        Page<ExamStudentPaperAnswer> page = new Page<>(current, size);
        page.setRecords(records);
        page.setTotal(total);
        return page;
    }

    @Override
    public List<ExamStudentPaperAnswer> listQuery(ExamStudentPaperAnswer entity) {
        return examStudentPaperAnswerMapper.listQuery(entity);
    }

    @Override
    public ExamStudentPaperAnswer getById(Long id) {
        return examStudentPaperAnswerMapper.getById(id);
    }

    /**
     * 下拉数据：exam_student
     */
    @Override
    public List<SelectVO> listExamStudent() {
        return examStudentPaperAnswerMapper.listExamStudent();
    }

    /**
     * 下拉数据：exam_paper
     */
    @Override
    public List<SelectVO> listExamPaper() {
        return examStudentPaperAnswerMapper.listExamPaper();
    }

    /**
     * 下拉数据：exam_question
     */
    @Override
    public List<SelectVO> listExamQuestion() {
        return examStudentPaperAnswerMapper.listExamQuestion();
    }

}
