package com.zjsu.lyy.course.service;

import com.zjsu.lyy.course.model.Student;
import com.zjsu.lyy.course.repository.EnrollmentRepository;
import com.zjsu.lyy.course.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentService {
    private final StudentRepository repo;
    private final EnrollmentRepository enrollmentRepo;

    public StudentService(StudentRepository repo, EnrollmentRepository enrollmentRepo) {
        this.repo = repo;
        this.enrollmentRepo = enrollmentRepo;
    }

    public List<Student> getAll() { return repo.findAll(); }
    public Student getByStudentId(String studentId) {
        return repo.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("学生不存在"));
    }
    public Student create(Student s) {
        if (s.getStudentId() == null || s.getStudentId().isBlank())
            throw new IllegalArgumentException("学号不能为空");
        if (repo.existsByStudentId(s.getStudentId()))
            throw new IllegalArgumentException("学号已存在");
        if (!s.getEmail().contains("@"))
            throw new IllegalArgumentException("邮箱格式错误");
        // 不再生成 UUID，直接把学号当主键
        s.setCreatedAt(LocalDateTime.now());
        return repo.save(s);
    }
    public Student update(String studentId, Student newStu) {
        Student old = getByStudentId(studentId);
        // 允许改学号，但要保证新学号不冲突
        if (newStu.getStudentId() != null &&
            !newStu.getStudentId().equals(old.getStudentId())) {
            if (repo.existsByStudentId(newStu.getStudentId()))
                throw new IllegalArgumentException("新学号已存在");
            // 先删旧 key，再换新 key
            repo.deleteByStudentId(studentId);
            old.setStudentId(newStu.getStudentId());
        }
        // 其余字段正常合并
        if (newStu.getName() != null) old.setName(newStu.getName());
        if (newStu.getMajor() != null) old.setMajor(newStu.getMajor());
        if (newStu.getGrade() != null) old.setGrade(newStu.getGrade());
        if (newStu.getEmail() != null) {
            if (!newStu.getEmail().contains("@"))
                throw new IllegalArgumentException("邮箱格式错误");
            old.setEmail(newStu.getEmail());
        }
        return repo.save(old);
    }
    public void delete(String studentId) {
        if (!enrollmentRepo.findByStudentId(studentId).isEmpty())
            throw new IllegalArgumentException("无法删除：该学生存在选课记录");
        repo.deleteByStudentId(studentId);
    }
}