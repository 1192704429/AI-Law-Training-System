package com.law.train.module.exam_wrong_question_book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.law.train.module.exam_wrong_question_book.entity.ExamWrongQuestionBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.law.train.vo.SelectVO;

import java.util.List;
import java.util.Map;

/**
 * @author wuming
 * @description: 学生错题本Mapper接口
 */
@Mapper
public interface ExamWrongQuestionBookMapper extends BaseMapper<ExamWrongQuestionBook> {

    /**
     * 分页查询
     */
    List<ExamWrongQuestionBook> pageQuery(@Param("entity") ExamWrongQuestionBook entity,
                             @Param("offset") Long offset,
                             @Param("limit") Long limit);

    /**
     * 查询总数
     */
    Long countQuery(@Param("entity") ExamWrongQuestionBook entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamWrongQuestionBook> listQuery(@Param("entity") ExamWrongQuestionBook entity);

    /**
     * 根据ID查询
     */
    ExamWrongQuestionBook getById(@Param("id") Long id);

    /**
     * 下拉选项查询
     */
    /**
     * 下拉数据：exam_course
     */
    @Select("select course_name as label, id as value from exam_course")
    List<SelectVO> listExamCourse();

    /**
     * 下拉数据：exam_question_practice_record
     */
    @Select("select id as label, id as value from exam_question_practice_record")
    List<SelectVO> listExamQuestionPracticeRecord();

    /**
     * 下拉数据：exam_student
     */
    @Select("select real_name as label, id as value from exam_student")
    List<SelectVO> listExamStudent();

    /**
     * 下拉数据：exam_question
     */
    @Select("select question_title as label, id as value from exam_question")
    List<SelectVO> listExamQuestion();

    Long countActiveWrongByStudent(@Param("studentId") Long studentId);

    List<Map<String, Object>> selectWrongCourseDistribution(@Param("studentId") Long studentId);

    List<Map<String, Object>> selectWrongTypeDistribution(@Param("studentId") Long studentId);

}
