package com.law.train.basic.controller;

import cn.hutool.core.util.StrUtil;
import com.law.train.common.Result;
import com.law.train.basic.controller.ExamStudentLoginController.StudentLoginUser;
import com.law.train.module.exam_question_practice_record.mapper.ExamQuestionPracticeRecordMapper;
import com.law.train.module.exam_wrong_question_book.mapper.ExamWrongQuestionBookMapper;
import com.law.train.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生端学习数据统计（错题分布、刷题趋势、课程正确率）
 */
@RestController
@RequestMapping("/student")
public class StudentStatsController {

    @Autowired
    private ExamQuestionPracticeRecordMapper practiceRecordMapper;
    @Autowired
    private ExamWrongQuestionBookMapper wrongBookMapper;
    @Autowired
    private JwtUtil<StudentLoginUser> jwtUtil;

    private Long getStudentId(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StrUtil.isBlank(authorization)) return null;
        if (ExamStudentLoginController.STUDENT_TOKEN_CACHE_MAP.get(authorization) == null) return null;
        if (!jwtUtil.verifyToken(authorization)) return null;
        StudentLoginUser user = jwtUtil.parseToken(authorization, StudentLoginUser.class);
        return user != null ? user.getId() : null;
    }

    private static long toLong(Object o) {
        if (o == null) return 0L;
        if (o instanceof Number) return ((Number) o).longValue();
        try {
            return Long.parseLong(o.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private static int toInt(Object o) {
        if (o == null) return 0;
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            return Integer.parseInt(o.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String questionTypeLabel(int t) {
        return switch (t) {
            case 1 -> "单选题";
            case 2 -> "多选题";
            case 3 -> "填空题";
            case 4 -> "判断题";
            case 5 -> "主观题";
            default -> t == 0 ? "未知题型" : ("题型" + t);
        };
    }

    /**
     * 学习统计汇总：错题总数与分布、近 30 日每日刷题量、各课程正确率
     */
    @GetMapping("/stats/dashboard")
    public Result dashboard(HttpServletRequest request) {
        Long studentId = getStudentId(request);
        if (studentId == null) return Result.error(401, "请先登录");

        Long wrongTotal = wrongBookMapper.countActiveWrongByStudent(studentId);
        if (wrongTotal == null) wrongTotal = 0L;

        List<Map<String, Object>> wrongCourses = wrongBookMapper.selectWrongCourseDistribution(studentId);
        List<Map<String, Object>> wrongTypes = wrongBookMapper.selectWrongTypeDistribution(studentId);
        List<Map<String, Object>> wrongByQuestionType = new ArrayList<>();
        for (Map<String, Object> row : wrongTypes) {
            int qt = toInt(row.get("questionType"));
            Map<String, Object> item = new HashMap<>();
            item.put("name", questionTypeLabel(qt));
            item.put("value", toLong(row.get("wrongCount")));
            wrongByQuestionType.add(item);
        }

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(29);
        LocalDateTime startDt = start.atStartOfDay();
        List<Map<String, Object>> dailyRows = practiceRecordMapper.selectStudentDailyPracticeCount(studentId, startDt);
        Map<String, Long> byDay = new HashMap<>();
        for (Map<String, Object> row : dailyRows) {
            Object d = row.get("statDate");
            if (d == null) continue;
            String key = d.toString();
            if (key.length() >= 10) key = key.substring(0, 10);
            byDay.put(key, toLong(row.get("cnt")));
        }
        List<Map<String, Object>> dailyTrend = new ArrayList<>();
        long sum30 = 0;
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            String ds = d.toString();
            long c = byDay.getOrDefault(ds, 0L);
            sum30 += c;
            Map<String, Object> point = new HashMap<>();
            point.put("date", ds);
            point.put("count", c);
            dailyTrend.add(point);
        }

        List<Map<String, Object>> accRows = practiceRecordMapper.selectStudentCourseAccuracy(studentId);
        List<Map<String, Object>> courseAccuracy = new ArrayList<>();
        for (Map<String, Object> row : accRows) {
            long total = toLong(row.get("totalCount"));
            long correct = toLong(row.get("correctCount"));
            double rate = total > 0 ? (correct * 100.0 / total) : 0;
            BigDecimal rateBd = BigDecimal.valueOf(rate).setScale(1, RoundingMode.HALF_UP);
            Map<String, Object> item = new HashMap<>();
            item.put("courseId", row.get("courseId"));
            item.put("courseName", row.get("courseName") != null ? row.get("courseName").toString() : "");
            item.put("totalCount", total);
            item.put("correctCount", correct);
            item.put("correctRate", rateBd.doubleValue());
            courseAccuracy.add(item);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("wrongTotal", wrongTotal);
        data.put("wrongByCourse", wrongCourses);
        data.put("wrongByQuestionType", wrongByQuestionType);
        data.put("dailyTrend", dailyTrend);
        data.put("practiceCountLast30Days", sum30);
        data.put("courseAccuracy", courseAccuracy);
        return Result.success(data);
    }
}
