package com.zjsu.lyy.course.service;

import com.zjsu.lyy.course.model.Course;
import com.zjsu.lyy.course.model.Enrollment;
import com.zjsu.lyy.course.repository.CourseRepository;
import com.zjsu.lyy.course.repository.EnrollmentRepository;
import com.zjsu.lyy.course.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepo;
    private final CourseRepository courseRepo;
    private final StudentRepository studentRepo;

    public EnrollmentService(EnrollmentRepository enrollmentRepo,
                             CourseRepository courseRepo,
                             StudentRepository studentRepo) {
        this.enrollmentRepo = enrollmentRepo;
        this.courseRepo = courseRepo;
        this.studentRepo = studentRepo;
    }

    public List<Enrollment> getAll() { return enrollmentRepo.findAll(); }
    public List<Enrollment> getByCourse(String courseCode) { return enrollmentRepo.findByCourseCode(courseCode); }
    public List<Enrollment> getByStudent(String studentId) { return enrollmentRepo.findByStudentId(studentId); }

    public Enrollment enroll(String courseCode, String studentId) {
        Course course = courseRepo.findByCode(courseCode)
                .orElseThrow(() -> new IllegalArgumentException("课程不存在"));
        if (!studentRepo.existsByStudentId(studentId))
            throw new IllegalArgumentException("学生不存在");
        if (enrollmentRepo.existsByCourseAndStudent(courseCode, studentId))
            throw new IllegalArgumentException("重复选课");
        int enrolled = course.getEnrolled();
        int capacity = course.getCapacity();

        if (enrolled >= capacity) {
            throw new IllegalArgumentException("课程容量已满");
        }

        // 创建选课记录
        Enrollment e = new Enrollment();
        e.setId(UUID.randomUUID().toString());
        e.setCourseId(courseCode);
        e.setStudentId(studentId);
        Enrollment saved = enrollmentRepo.save(e);

        course.setEnrolled(enrolled + 1);
        courseRepo.save(course);

        return saved;
    }

    public void drop(String id) {
        Enrollment enrollment = enrollmentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("选课记录不存在"));

        Course course = courseRepo.findByCode(enrollment.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("课程不存在"));
        course.setEnrolled(course.getEnrolled() - 1);
        courseRepo.save(course);

        enrollmentRepo.deleteById(id);
    }
}