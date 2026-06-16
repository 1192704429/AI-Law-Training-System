package com.law.train.basic.controller;

import cn.hutool.core.util.StrUtil;
import com.law.train.common.Result;
import com.law.train.basic.controller.ExamStudentLoginController.StudentLoginUser;
import com.law.train.module.exam_review_plan.entity.ExamReviewPlan;
import com.law.train.module.exam_review_plan.service.ExamReviewPlanService;
import com.law.train.module.exam_review_plan_detail.entity.ExamReviewPlanDetail;
import com.law.train.module.exam_review_plan_detail.service.ExamReviewPlanDetailService;
import com.law.train.module.exam_wrong_question_book.entity.ExamWrongQuestionBook;
import com.law.train.module.exam_wrong_question_book.service.ExamWrongQuestionBookService;
import com.law.train.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学生端我的计划：列表、新增（计划天数后端计算）、计划详情（按天选错题课程）、保存详情
 */
@RestController
@RequestMapping("/student")
public class StudentPlanController {

    @Autowired
    private ExamReviewPlanService examReviewPlanService;
    @Autowired
    private ExamReviewPlanDetailService examReviewPlanDetailService;
    @Autowired
    private ExamWrongQuestionBookService wrongQuestionBookService;
    @Autowired
    private JwtUtil<StudentLoginUser> jwtUtil;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long getStudentId(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StrUtil.isBlank(authorization)) return null;
        if (ExamStudentLoginController.STUDENT_TOKEN_CACHE_MAP.get(authorization) == null) return null;
        if (!jwtUtil.verifyToken(authorization)) return null;
        StudentLoginUser user = jwtUtil.parseToken(authorization, StudentLoginUser.class);
        return user != null ? user.getId() : null;
    }

    /**
     * 学生端：我的计划列表（支持计划名称搜索）
     */
    @GetMapping("/plan/list")
    public Result list(HttpServletRequest request,
                       @RequestParam(required = false) String planName) {
        Long studentId = getStudentId(request);
        if (studentId == null) return Result.error(401, "请先登录");
        ExamReviewPlan query = new ExamReviewPlan();
        query.setStudentId(studentId);
        if (StrUtil.isNotBlank(planName)) query.setPlanName(planName.trim());
        List<ExamReviewPlan> list = examReviewPlanService.listQuery(query);
        return Result.success(list);
    }

    /**
     * 学生端：新增计划（不传计划天数，后端根据开始/结束日期自动计算）
     */
    @PostMapping("/plan")
    public Result save(HttpServletRequest request, @RequestBody SavePlanRequest body) {
        Long studentId = getStudentId(request);
        if (studentId == null) return Result.error(401, "请先登录");
        if (StrUtil.isBlank(body.getPlanName())) return Result.error("请输入计划名称");
        LocalDateTime startDate = parseDate(body.getStartDate());
        LocalDateTime endDate = parseDate(body.getEndDate());
        if (startDate == null) return Result.error("请选择开始日期");
        if (endDate == null) return Result.error("请选择结束日期");
        if (!endDate.toLocalDate().isAfter(startDate.toLocalDate()) && !endDate.toLocalDate().equals(startDate.toLocalDate())) {
            return Result.error("结束日期不能早于开始日期");
        }
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
        if (days > 365) return Result.error("计划天数不能超过365天");
        ExamReviewPlan plan = new ExamReviewPlan();
        plan.setStudentId(studentId);
        plan.setPlanName(body.getPlanName().trim());
        plan.setStartDate(startDate);
        plan.setEndDate(endDate);
        plan.setPlanDays((int) days);
        plan.setTargetDesc(StrUtil.isNotBlank(body.getTargetDesc()) ? body.getTargetDesc().trim() : null);
        plan.setStatus(1);
        plan.setCreateTime(LocalDateTime.now());
        examReviewPlanService.save(plan);
        return Result.success("创建成功", plan);
    }

    /**
     * 学生端：计划详情（单条计划 + 其明细列表）
     */
    @GetMapping("/plan/{planId}")
    public Result getPlan(HttpServletRequest request, @PathVariable Long planId) {
        Long studentId = getStudentId(request);
        if (studentId == null) return Result.error(401, "请先登录");
        ExamReviewPlan plan = examReviewPlanService.getById(planId);
        if (plan == null) return Result.error("计划不存在");
        if (!plan.getStudentId().equals(studentId)) return Result.error("无权限");
        ExamReviewPlanDetail detailQuery = new ExamReviewPlanDetail();
        detailQuery.setPlanId(planId);
        List<ExamReviewPlanDetail> details = examReviewPlanDetailService.listQuery(detailQuery);
        details.sort(Comparator.comparing(ExamReviewPlanDetail::getPlanDate));
        Map<String, Object> data = new HashMap<>();
        data.put("plan", plan);
        data.put("details", details);
        return Result.success(data);
    }

    /**
     * 学生端：有错题的课程列表（用于详情里按天选择课程）
     */
    @GetMapping("/plan/wrong-courses")
    public Result wrongCourses(HttpServletRequest request) {
        Long studentId = getStudentId(request);
        if (studentId == null) return Result.error(401, "请先登录");
        ExamWrongQuestionBook query = new ExamWrongQuestionBook();
        query.setStudentId(studentId);
        query.setStatus(1);
        List<ExamWrongQuestionBook> list = wrongQuestionBookService.listQuery(query);
        List<Map<String, Object>> courses = list.stream()
                .filter(w -> w.getCourseId() != null && w.getCourseName() != null)
                .collect(Collectors.groupingBy(ExamWrongQuestionBook::getCourseId))
                .entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("courseId", e.getKey());
                    m.put("courseName", e.getValue().get(0).getCourseName());
                    m.put("wrongCount", e.getValue().size());
                    return m;
                })
                .sorted(Comparator.comparing(m -> (String) m.get("courseName")))
                .collect(Collectors.toList());
        return Result.success(courses);
    }

    /**
     * 学生端：保存计划详情（按天选择课程，仅限有错题的课程）
     */
    @PostMapping("/plan/{planId}/detail")
    public Result saveDetail(HttpServletRequest request, @PathVariable Long planId,
                             @RequestBody SavePlanDetailRequest body) {
        Long studentId = getStudentId(request);
        if (studentId == null) return Result.error(401, "请先登录");
        ExamReviewPlan plan = examReviewPlanService.getById(planId);
        if (plan == null) return Result.error("计划不存在");
        if (!plan.getStudentId().equals(studentId)) return Result.error("无权限");
        if (plan.getPlanDays() == null || plan.getPlanDays() < 1) return Result.error("计划天数异常");
        List<SavePlanDetailItem> items = body.getDetails();
        if (items == null) items = new ArrayList<>();
        Set<Long> allowedCourseIds = getAllowedCourseIds(studentId);
        if (allowedCourseIds.isEmpty()) return Result.error("暂无错题课程，无法添加详情");
        ExamReviewPlanDetail delQuery = new ExamReviewPlanDetail();
        delQuery.setPlanId(planId);
        List<ExamReviewPlanDetail> existing = examReviewPlanDetailService.listQuery(delQuery);
        for (ExamReviewPlanDetail d : existing) {
            examReviewPlanDetailService.removeById(d.getId());
        }
        LocalDate start = plan.getStartDate().toLocalDate();
        for (SavePlanDetailItem item : items) {
            if (item.getDayIndex() == null || item.getDayIndex() < 1 || item.getDayIndex() > plan.getPlanDays()) continue;
            if (item.getCourseId() == null || !allowedCourseIds.contains(item.getCourseId())) continue;
            ExamReviewPlanDetail detail = new ExamReviewPlanDetail();
            detail.setPlanId(planId);
            detail.setStudentId(studentId);
            detail.setPlanDate(LocalDateTime.of(start.plusDays(item.getDayIndex() - 1), java.time.LocalTime.MIN));
            detail.setCourseId(item.getCourseId());
            detail.setCompleteStatus(0);
            detail.setCreateTime(LocalDateTime.now());
            examReviewPlanDetailService.save(detail);
        }
        return Result.success("保存成功");
    }

    private Set<Long> getAllowedCourseIds(Long studentId) {
        ExamWrongQuestionBook q = new ExamWrongQuestionBook();
        q.setStudentId(studentId);
        q.setStatus(1);
        return wrongQuestionBookService.listQuery(q).stream()
                .map(ExamWrongQuestionBook::getCourseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private LocalDateTime parseDate(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        s = s.trim();
        try {
            if (s.length() <= 10) {
                return LocalDate.parse(s, DATE_FORMAT).atStartOfDay();
            }
            return LocalDateTime.parse(s, DATETIME_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }

    @Data
    public static class SavePlanRequest {
        private String planName;
        private String startDate;
        private String endDate;
        private String targetDesc;
    }

    @Data
    public static class SavePlanDetailRequest {
        private List<SavePlanDetailItem> details;
    }

    @Data
    public static class SavePlanDetailItem {
        private Integer dayIndex;
        private Long courseId;
    }
}
