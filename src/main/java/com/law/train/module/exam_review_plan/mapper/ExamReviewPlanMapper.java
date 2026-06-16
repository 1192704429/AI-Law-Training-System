package com.law.train.module.exam_review_plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.law.train.module.exam_review_plan.entity.ExamReviewPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.law.train.vo.SelectVO;

import java.util.List;

/**
 * @author wuming
 * @description: 学生复习计划Mapper接口
 */
@Mapper
public interface ExamReviewPlanMapper extends BaseMapper<ExamReviewPlan> {

    /**
     * 分页查询
     */
    List<ExamReviewPlan> pageQuery(@Param("entity") ExamReviewPlan entity,
                             @Param("offset") Long offset,
                             @Param("limit") Long limit);

    /**
     * 查询总数
     */
    Long countQuery(@Param("entity") ExamReviewPlan entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamReviewPlan> listQuery(@Param("entity") ExamReviewPlan entity);

    /**
     * 根据ID查询
     */
    ExamReviewPlan getById(@Param("id") Long id);

    /**
     * 下拉选项查询
     */
    /**
     * 下拉数据：exam_student
     */
    @Select("select real_name as label, id as value from exam_student")
    List<SelectVO> listExamStudent();

}
