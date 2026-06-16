package com.law.train.module.exam_student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.law.train.module.exam_student.entity.ExamStudent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wuming
 * @description: 学生Mapper接口
 */
@Mapper
public interface ExamStudentMapper extends BaseMapper<ExamStudent> {

    /**
     * 分页查询
     */
    List<ExamStudent> pageQuery(@Param("entity") ExamStudent entity,
                             @Param("offset") Long offset,
                             @Param("limit") Long limit);

    /**
     * 查询总数
     */
    Long countQuery(@Param("entity") ExamStudent entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamStudent> listQuery(@Param("entity") ExamStudent entity);

    /**
     * 根据ID查询
     */
    ExamStudent getById(@Param("id") Long id);

}
