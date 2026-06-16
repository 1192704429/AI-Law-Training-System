package com.law.train.module.exam_student_paper_answer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.law.train.module.exam_student_paper_answer.entity.ExamStudentPaperAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 学生试卷答案Mapper接口
 */
@Mapper
public interface ExamStudentPaperAnswerMapper extends BaseMapper<ExamStudentPaperAnswer> {

    /**
     * 分页查询
     */
    List<ExamStudentPaperAnswer> pageQuery(@Param("entity") ExamStudentPaperAnswer entity,
                             @Param("offset") Long offset,
                             @Param("limit") Long limit);

    /**
     * 查询总数
     */
    Long countQuery(@Param("entity") ExamStudentPaperAnswer entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamStudentPaperAnswer> listQuery(@Param("entity") ExamStudentPaperAnswer entity);

    /**
     * 根据ID查询
     */
    ExamStudentPaperAnswer getById(@Param("id") Long id);

    /**
     * 下拉选项查询
     */
    /**
     * 下拉数据：exam_student
     */
    @Select("select real_name as label, id as value from exam_student")
    List<SelectVO> listExamStudent();

    /**
     * 下拉数据：exam_paper
     */
    @Select("select paper_name as label, id as value from exam_paper")
    List<SelectVO> listExamPaper();

    /**
     * 下拉数据：exam_question
     */
    @Select("select question_title as label, id as value from exam_question")
    List<SelectVO> listExamQuestion();

}
