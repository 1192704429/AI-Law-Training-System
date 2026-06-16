package com.law.train.basic.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.law.train.common.Result;
import com.law.train.module.exam_student.entity.ExamStudent;
import com.law.train.module.exam_student.service.ExamStudentService;
import com.law.train.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 学生端登录接口（用户端）
 * 与管理员登录完全隔离：独立 URL、独立 token 缓存、独立校验
 */
@RestController
@RequestMapping("/student")
public class ExamStudentLoginController {

    /** 学生端 token 缓存，与管理员 TOKEN_CACHE_MAP 分离 */
    static final Map<String, Long> STUDENT_TOKEN_CACHE_MAP = new HashMap<>();

    @Value("${spring.application.name}")
    private String secret;

    @Autowired
    private JwtUtil<StudentLoginUser> jwtUtil;

    @Autowired
    private ExamStudentService examStudentService;

    /**
     * 学生登录：账号密码来自 exam_student 表
     */
    @PostMapping("/login")
    public Result login(@RequestBody StudentLoginVO loginVO) {
        if (StrUtil.isBlank(loginVO.getUsername())) {
            return Result.error("请输入登录账号");
        }
        if (StrUtil.isBlank(loginVO.getPassword())) {
            return Result.error("请输入密码");
        }
        LambdaQueryWrapper<ExamStudent> q = new LambdaQueryWrapper<>();
        q.eq(ExamStudent::getUsername, loginVO.getUsername().trim());
        ExamStudent student = examStudentService.getOne(q);
        if (student == null) {
            return Result.error("账号不存在");
        }
        if (student.getStatus() != null && student.getStatus() != 1) {
            return Result.error("账号已停用");
        }
        String inputHash = sha256(student.getPassword());
        if (!inputHash.equals(loginVO.getPassword())) {
            return Result.error("密码错误");
        }
        StudentLoginUser loginUser = new StudentLoginUser();
        loginUser.setId(student.getId());
        loginUser.setName(StrUtil.isNotBlank(student.getRealName()) ? student.getRealName() : student.getUsername());
        loginUser.setAvatar(StrUtil.isNotBlank(student.getAvatar()) ? student.getAvatar() : "");
        loginUser.setRole("student");
        String token = jwtUtil.createToken(loginUser);
        STUDENT_TOKEN_CACHE_MAP.put(token, System.currentTimeMillis());
        return Result.success(token);
    }

    /**
     * 学生注册（无需登录，公开接口）
     */
    @PostMapping("/register")
    public Result register(@RequestBody StudentRegisterVO vo) {
        if (StrUtil.isBlank(vo.getUsername())) {
            return Result.error("请输入登录账号");
        }
        if (StrUtil.isBlank(vo.getPassword())) {
            return Result.error("请输入密码");
        }
        if (vo.getPassword().length() < 6) {
            return Result.error("密码长度不能小于6位");
        }
        LambdaQueryWrapper<ExamStudent> q = new LambdaQueryWrapper<>();
        q.eq(ExamStudent::getUsername, vo.getUsername().trim());
        if (examStudentService.count(q) > 0) {
            return Result.error("该账号已被注册");
        }
        ExamStudent student = new ExamStudent();
        student.setUsername(vo.getUsername().trim());
        student.setPassword(vo.getPassword()); // 存明文，与登录校验一致
        student.setRealName(StrUtil.isNotBlank(vo.getRealName()) ? vo.getRealName().trim() : null);
        student.setGender(vo.getGender());
        student.setPhone(StrUtil.isNotBlank(vo.getPhone()) ? vo.getPhone().trim() : null);
        student.setEmail(StrUtil.isNotBlank(vo.getEmail()) ? vo.getEmail().trim() : null);
        student.setGradeName(StrUtil.isNotBlank(vo.getGradeName()) ? vo.getGradeName().trim() : null);
        student.setSchoolName(StrUtil.isNotBlank(vo.getSchoolName()) ? vo.getSchoolName().trim() : null);
        student.setStatus(1);
        student.setCreateTime(LocalDateTime.now());
        examStudentService.save(student);
        return Result.success("注册成功，请登录");
    }

    /**
     * 学生端获取当前用户信息（需带学生 token）
     */
    @GetMapping("/info")
    public Result info(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StrUtil.isBlank(authorization)) {
            return Result.error(401, "请先登录");
        }
        Long startTime = STUDENT_TOKEN_CACHE_MAP.get(authorization);
        if (startTime == null) {
            return Result.error(401, "登录已过期，请重新登录");
        }
        if (System.currentTimeMillis() - startTime > (1000L * 60 * 60 * 24)) {
            STUDENT_TOKEN_CACHE_MAP.remove(authorization);
            return Result.error(401, "登录已过期，请重新登录");
        }
        if (!jwtUtil.verifyToken(authorization)) {
            return Result.error(401, "登录已过期，请重新登录");
        }
        StudentLoginUser loginUser = jwtUtil.parseToken(authorization, StudentLoginUser.class);
        ExamStudent student = examStudentService.getById(loginUser.getId());
        if (student == null) {
            return Result.error(401, "用户不存在");
        }
        StudentLoginUser info = new StudentLoginUser();
        info.setId(student.getId());
        info.setName(StrUtil.isNotBlank(student.getRealName()) ? student.getRealName() : student.getUsername());
        info.setAvatar(StrUtil.isNotBlank(student.getAvatar()) ? student.getAvatar() : "");
        info.setRole("student");
        return Result.success(info);
    }

    /**
     * 学生端退出登录
     */
    @GetMapping("/logout")
    public Result logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StrUtil.isNotBlank(token)) {
            STUDENT_TOKEN_CACHE_MAP.remove(token);
        }
        return Result.success("退出成功");
    }

    /**
     * 学生端：获取个人资料（完整信息，用于个人中心编辑，不含密码）
     */
    @GetMapping("/profile")
    public Result profile(HttpServletRequest request) {
        ExamStudent student = getStudentFromRequest(request);
        if (student == null) return Result.error(401, "请先登录");
        student.setPassword(null);
        return Result.success(student);
    }

    /**
     * 学生端：更新个人资料（不包含密码、username）
     */
    @PutMapping("/profile")
    public Result updateProfile(HttpServletRequest request, @RequestBody StudentProfileVO vo) {
        ExamStudent student = getStudentFromRequest(request);
        if (student == null) return Result.error(401, "请先登录");
        if (StrUtil.isNotBlank(vo.getRealName())) student.setRealName(vo.getRealName().trim());
        if (vo.getGender() != null) student.setGender(vo.getGender());
        if (vo.getPhone() != null) student.setPhone(vo.getPhone().trim().isEmpty() ? null : vo.getPhone().trim());
        if (vo.getEmail() != null) student.setEmail(vo.getEmail().trim().isEmpty() ? null : vo.getEmail().trim());
        if (vo.getAvatar() != null) student.setAvatar(vo.getAvatar().trim().isEmpty() ? null : vo.getAvatar().trim());
        if (vo.getGradeName() != null) student.setGradeName(vo.getGradeName().trim().isEmpty() ? null : vo.getGradeName().trim());
        if (vo.getSchoolName() != null) student.setSchoolName(vo.getSchoolName().trim().isEmpty() ? null : vo.getSchoolName().trim());
        examStudentService.updateById(student);
        return Result.success("保存成功");
    }

    /**
     * 学生端：修改密码
     * oldPassword 为前端 SHA256(secret+明文) 后的值，与登录时一致；newPassword 为明文，后端存明文
     */
    @PutMapping("/password")
    public Result changePassword(HttpServletRequest request, @RequestBody ChangePasswordVO vo) {
        ExamStudent student = getStudentFromRequest(request);
        if (student == null) return Result.error(401, "请先登录");
        if (StrUtil.isBlank(vo.getOldPassword())) return Result.error("请输入当前密码");
        if (StrUtil.isBlank(vo.getNewPassword())) return Result.error("请输入新密码");
        if (vo.getNewPassword().length() < 6) return Result.error("新密码长度不能小于6位");
        String oldHash = sha256(student.getPassword());
        if (!oldHash.equals(vo.getOldPassword().trim())) return Result.error("当前密码错误");
        student.setPassword(vo.getNewPassword().trim());
        examStudentService.updateById(student);
        return Result.success("密码修改成功，请重新登录");
    }

    private ExamStudent getStudentFromRequest(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StrUtil.isBlank(authorization)) return null;
        if (STUDENT_TOKEN_CACHE_MAP.get(authorization) == null) return null;
        if (!jwtUtil.verifyToken(authorization)) return null;
        StudentLoginUser loginUser = jwtUtil.parseToken(authorization, StudentLoginUser.class);
        return loginUser != null ? examStudentService.getById(loginUser.getId()) : null;
    }

    private String sha256(String input) {
        try {
            String s = secret + input;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(s.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) sb.append('0');
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    public static class StudentLoginVO {
        private String username;
        private String password;
    }

    @Data
    public static class StudentRegisterVO {
        private String username;
        private String password;
        private String realName;
        private Integer gender;
        private String phone;
        private String email;
        private String gradeName;
        private String schoolName;
    }

    @Data
    public static class StudentLoginUser {
        private Long id;
        private String name;
        private String avatar;
        private String role;
    }

    @Data
    public static class StudentProfileVO {
        private String realName;
        private Integer gender;
        private String phone;
        private String email;
        private String avatar;
        private String gradeName;
        private String schoolName;
    }

    @Data
    public static class ChangePasswordVO {
        private String oldPassword;
        private String newPassword;
    }
}
