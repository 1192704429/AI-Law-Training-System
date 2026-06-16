package com.law.train.module.exam_question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.law.train.module.exam_question.entity.ExamQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 题目Mapper接口
 */
@Mapper
public interface ExamQuestionMapper extends BaseMapper<ExamQuestion> {

    /**
     * 分页查询
     */
    List<ExamQuestion> pageQuery(@Param("entity") ExamQuestion entity,
                             @Param("offset") Long offset,
                             @Param("limit") Long limit);

    /**
     * 查询总数
     */
    Long countQuery(@Param("entity") ExamQuestion entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamQuestion> listQuery(@Param("entity") ExamQuestion entity);

    /**
     * 随机取一道题（相同 entity 条件）
     */
    ExamQuestion getRandomOne(@Param("entity") ExamQuestion entity);

    /**
     * 根据ID查询
     */
    ExamQuestion getById(@Param("id") Long id);

    /**
     * 下拉选项查询
     */
    /**
     * 下拉数据：exam_course
     */
    @Select("select course_name as label, id as value from exam_course")
    List<SelectVO> listExamCourse();

}
