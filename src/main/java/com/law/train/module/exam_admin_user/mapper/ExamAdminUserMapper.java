package com.law.train.module.exam_admin_user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.law.train.module.exam_admin_user.entity.ExamAdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wuming
 * @description: 管理员Mapper接口
 */
@Mapper
public interface ExamAdminUserMapper extends BaseMapper<ExamAdminUser> {

    /**
     * 分页查询
     */
    List<ExamAdminUser> pageQuery(@Param("entity") ExamAdminUser entity,
                             @Param("offset") Long offset,
                             @Param("limit") Long limit);

    /**
     * 查询总数
     */
    Long countQuery(@Param("entity") ExamAdminUser entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamAdminUser> listQuery(@Param("entity") ExamAdminUser entity);

    /**
     * 根据ID查询
     */
    ExamAdminUser getById(@Param("id") Long id);

}
