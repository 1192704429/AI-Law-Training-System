package com.law.train.module.exam_admin_user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.law.train.module.exam_admin_user.entity.ExamAdminUser;

import java.util.List;

/**
 * @author wuming
 * @description: 管理员Service接口
 */
public interface ExamAdminUserService extends IService<ExamAdminUser> {

    /**
     * 分页查询
     */
    IPage<ExamAdminUser> pageQuery(Integer current, Integer size, ExamAdminUser entity);

    /**
     * 列表查询（不分页）
     */
    List<ExamAdminUser> listQuery(ExamAdminUser entity);

    /**
     * 根据ID查询
     */
    ExamAdminUser getById(Long id);

}
