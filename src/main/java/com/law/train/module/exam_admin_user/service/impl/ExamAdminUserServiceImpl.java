package com.law.train.module.exam_admin_user.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.law.train.module.exam_admin_user.entity.ExamAdminUser;
import com.law.train.module.exam_admin_user.mapper.ExamAdminUserMapper;
import com.law.train.module.exam_admin_user.service.ExamAdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wuming
 * @description: 管理员Service实现类
 */
@Service
public class ExamAdminUserServiceImpl extends ServiceImpl<ExamAdminUserMapper, ExamAdminUser> implements ExamAdminUserService {

    @Autowired
    private ExamAdminUserMapper examAdminUserMapper;

    @Override
    public IPage<ExamAdminUser> pageQuery(Integer current, Integer size, ExamAdminUser entity) {
        Long offset = (long) (current - 1) * size;
        Long limit = (long) size;
        List<ExamAdminUser> records = examAdminUserMapper.pageQuery(entity, offset, limit);
        Long total = examAdminUserMapper.countQuery(entity);

        Page<ExamAdminUser> page = new Page<>(current, size);
        page.setRecords(records);
        page.setTotal(total);
        return page;
    }

    @Override
    public List<ExamAdminUser> listQuery(ExamAdminUser entity) {
        return examAdminUserMapper.listQuery(entity);
    }

    @Override
    public ExamAdminUser getById(Long id) {
        return examAdminUserMapper.getById(id);
    }

}
