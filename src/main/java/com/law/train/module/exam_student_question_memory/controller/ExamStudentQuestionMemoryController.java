package com.law.train.module.exam_student_question_memory.controller;

import com.law.train.common.Result;
import com.law.train.module.exam_student_question_memory.entity.ExamStudentQuestionMemory;
import com.law.train.module.exam_student_question_memory.service.ExamStudentQuestionMemoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import com.law.train.vo.SelectVO;

/**
 * @author wuming
 * @description: 学生题目记忆状态Controller
 */
@RestController
@RequestMapping("/exam_student_question_memory")
public class ExamStudentQuestionMemoryController {

    @Autowired
    private ExamStudentQuestionMemoryService examStudentQuestionMemoryService;

    /**
     * 分页查询学生题目记忆状态列表
     */
    @PostMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestBody(required = false) ExamStudentQuestionMemory entity) {
        if (entity == null) {
            entity = new ExamStudentQuestionMemory();
        }
        return Result.success(examStudentQuestionMemoryService.pageQuery(current, size, entity));
    }

    /**
     * 查询所有学生题目记忆状态列表
     */
    @PostMapping("/list")
    public Result list(@RequestBody(required = false) ExamStudentQuestionMemory entity) {
        if (entity == null) {
            entity = new ExamStudentQuestionMemory();
        }
        List<ExamStudentQuestionMemory> list = examStudentQuestionMemoryService.listQuery(entity);
        return Result.success(list);
    }

    /**
     * 根据ID查询学生题目记忆状态
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        ExamStudentQuestionMemory examStudentQuestionMemory = examStudentQuestionMemoryService.getById(id);
        if (examStudentQuestionMemory == null) {
            return Result.error("学生题目记忆状态不存在");
        }
        return Result.success(examStudentQuestionMemory);
    }

    /**
     * 新增学生题目记忆状态
     */
    @PostMapping
    public Result save(@RequestBody ExamStudentQuestionMemory examStudentQuestionMemory) {
        examStudentQuestionMemory.setCreateTime(LocalDateTime.now());
        boolean success = examStudentQuestionMemoryService.save(examStudentQuestionMemory);
        if (success) {
            return Result.success("新增成功", examStudentQuestionMemory);
        }
        return Result.error("新增失败");
    }

    /**
     * 更新学生题目记忆状态
     */
    @PutMapping
    public Result update(@RequestBody ExamStudentQuestionMemory examStudentQuestionMemory) {
        boolean success = examStudentQuestionMemoryService.updateById(examStudentQuestionMemory);
        if (success) {
            return Result.success("更新成功", examStudentQuestionMemory);
        }
        return Result.error("更新失败");
    }

    /**
     * 根据ID删除学生题目记忆状态
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        boolean success = examStudentQuestionMemoryService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除学生题目记忆状态
     */
    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        boolean success = examStudentQuestionMemoryService.removeByIds(ids);
        if (success) {
            return Result.success("批量删除成功");
        }
        return Result.error("批量删除失败");
    }

    /**
     * 下拉数据接口：exam_course
     */
    @GetMapping("/select/listExamCourse")
    public Result listExamCourse() {
        List<SelectVO> list = examStudentQuestionMemoryService.listExamCourse();
        return Result.success(list);
    }

    /**
     * 下拉数据接口：exam_student
     */
    @GetMapping("/select/listExamStudent")
    public Result listExamStudent() {
        List<SelectVO> list = examStudentQuestionMemoryService.listExamStudent();
        return Result.success(list);
    }

    /**
     * 下拉数据接口：exam_question
     */
    @GetMapping("/select/listExamQuestion")
    public Result listExamQuestion() {
        List<SelectVO> list = examStudentQuestionMemoryService.listExamQuestion();
        return Result.success(list);
    }

}
