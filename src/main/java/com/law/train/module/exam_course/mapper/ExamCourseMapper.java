package com.law.train.module.exam_course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.law.train.module.exam_course.entity.ExamCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wuming
 * @description: 课程Mapper接口
 */
@Mapper
public interface ExamCourseMapper extends BaseMapper<ExamCourse> {

    /**
     * 分页查询
     */
    List<ExamCourse> pageQuery(@Param("entity") ExamCourse entity,
                             @Param("offset") Long offset,
                             @Param("limit") Long limit);

    /**
     * 查询总数
     */
    Long countQuery(@Param("entity") ExamCourse entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamCourse> listQuery(@Param("entity") ExamCourse entity);

    /**
     * 根据ID查询
     */
    ExamCourse getById(@Param("id") Long id);

}
