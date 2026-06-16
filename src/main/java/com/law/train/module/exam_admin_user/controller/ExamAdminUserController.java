package com.law.train.module.exam_admin_user.controller;

import com.law.train.common.Result;
import com.law.train.module.exam_admin_user.entity.ExamAdminUser;
import com.law.train.module.exam_admin_user.service.ExamAdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wuming
 * @description: 管理员Controller
 */
@RestController
@RequestMapping("/exam_admin_user")
public class ExamAdminUserController {

    @Autowired
    private ExamAdminUserService examAdminUserService;

    /**
     * 分页查询管理员列表
     */
    @PostMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestBody(required = false) ExamAdminUser entity) {
        if (entity == null) {
            entity = new ExamAdminUser();
        }
        return Result.success(examAdminUserService.pageQuery(current, size, entity));
    }

    /**
     * 查询所有管理员列表
     */
    @PostMapping("/list")
    public Result list(@RequestBody(required = false) ExamAdminUser entity) {
        if (entity == null) {
            entity = new ExamAdminUser();
        }
        List<ExamAdminUser> list = examAdminUserService.listQuery(entity);
        return Result.success(list);
    }

    /**
     * 根据ID查询管理员
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        ExamAdminUser examAdminUser = examAdminUserService.getById(id);
        if (examAdminUser == null) {
            return Result.error("管理员不存在");
        }
        return Result.success(examAdminUser);
    }

    /**
     * 新增管理员
     */
    @PostMapping
    public Result save(@RequestBody ExamAdminUser examAdminUser) {
        examAdminUser.setCreateTime(LocalDateTime.now());
        boolean success = examAdminUserService.save(examAdminUser);
        if (success) {
            return Result.success("新增成功", examAdminUser);
        }
        return Result.error("新增失败");
    }

    /**
     * 更新管理员
     */
    @PutMapping
    public Result update(@RequestBody ExamAdminUser examAdminUser) {
        boolean success = examAdminUserService.updateById(examAdminUser);
        if (success) {
            return Result.success("更新成功", examAdminUser);
        }
        return Result.error("更新失败");
    }

    /**
     * 根据ID删除管理员
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        boolean success = examAdminUserService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除管理员
     */
    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        boolean success = examAdminUserService.removeByIds(ids);
        if (success) {
            return Result.success("批量删除成功");
        }
        return Result.error("批量删除失败");
    }

}
