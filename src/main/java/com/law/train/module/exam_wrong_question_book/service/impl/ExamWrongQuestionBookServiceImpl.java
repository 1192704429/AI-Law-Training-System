package com.law.train.module.exam_wrong_question_book.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.law.train.module.exam_wrong_question_book.entity.ExamWrongQuestionBook;
import com.law.train.module.exam_wrong_question_book.mapper.ExamWrongQuestionBookMapper;
import com.law.train.module.exam_wrong_question_book.service.ExamWrongQuestionBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 学生错题本Service实现类
 */
@Service
public class ExamWrongQuestionBookServiceImpl extends ServiceImpl<ExamWrongQuestionBookMapper, ExamWrongQuestionBook> implements ExamWrongQuestionBookService {

    @Autowired
    private ExamWrongQuestionBookMapper examWrongQuestionBookMapper;

    @Override
    public IPage<ExamWrongQuestionBook> pageQuery(Integer current, Integer size, ExamWrongQuestionBook entity) {
        Long offset = (long) (current - 1) * size;
        Long limit = (long) size;
        List<ExamWrongQuestionBook> records = examWrongQuestionBookMapper.pageQuery(entity, offset, limit);
        Long total = examWrongQuestionBookMapper.countQuery(entity);

        Page<ExamWrongQuestionBook> page = new Page<>(current, size);
        page.setRecords(records);
        page.setTotal(total);
        return page;
    }

    @Override
    public List<ExamWrongQuestionBook> listQuery(ExamWrongQuestionBook entity) {
        return examWrongQuestionBookMapper.listQuery(entity);
    }

    @Override
    public ExamWrongQuestionBook getById(Long id) {
        return examWrongQuestionBookMapper.getById(id);
    }

    /**
     * 下拉数据：exam_course
     */
    @Override
    public List<SelectVO> listExamCourse() {
        return examWrongQuestionBookMapper.listExamCourse();
    }

    /**
     * 下拉数据：exam_question_practice_record
     */
    @Override
    public List<SelectVO> listExamQuestionPracticeRecord() {
        return examWrongQuestionBookMapper.listExamQuestionPracticeRecord();
    }

    /**
     * 下拉数据：exam_student
     */
    @Override
    public List<SelectVO> listExamStudent() {
        return examWrongQuestionBookMapper.listExamStudent();
    }

    /**
     * 下拉数据：exam_question
     */
    @Override
    public List<SelectVO> listExamQuestion() {
        return examWrongQuestionBookMapper.listExamQuestion();
    }

}
