package com.zjsu.lyy.course.service;

import com.zjsu.lyy.course.model.Course;
import com.zjsu.lyy.course.model.Enrollment;
import com.zjsu.lyy.course.repository.CourseRepository;
import com.zjsu.lyy.course.repository.EnrollmentRepository;
import com.zjsu.lyy.course.repository.StudentRepository;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
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
    public List<Enrollment> getByCourse(String courseId) { return enrollmentRepo.findByCourseId(courseId); }
    public List<Enrollment> getByStudent(String studentId) { return enrollmentRepo.findByStudentId(studentId); }

    public Enrollment enroll(String courseCode, String studentId) {
        // 1. 拿到课程
        Course course = courseRepo.findByCode(courseCode)
                .orElseThrow(() -> new IllegalArgumentException("课程不存在"));

        // 2. 判学生是否存在
        if (!studentRepo.existsByStudentId(studentId)) {
            throw new IllegalArgumentException("学生不存在");
        }

        // 3. 判重复选课
        if (enrollmentRepo.existsByCourseIdAndStudentId(course.getCode(), studentId)) {
            throw new IllegalArgumentException("重复选课");
        }

        // 4. 判容量
        int enrolled = enrollmentRepo.countByCourseIdAndStatus(course.getCode(), Enrollment.Status.ACTIVE);
        if (enrolled >= course.getCapacity()) {
            throw new IllegalArgumentException("课程容量已满");
        }

        // 5. 新建选课记录
        Enrollment e = new Enrollment();
        e.setCourseId(course.getCode());
        e.setStudentId(studentId);
        e.setStatus(Enrollment.Status.ACTIVE);
        Enrollment saved = enrollmentRepo.save(e);

        // 6. 把课程已选人数+1
        course.setEnrolled(enrolled + 1);
        courseRepo.save(course);

        return saved;
    }

    public void drop(String enrollmentId) {
        // 1. 找到选课记录
        Enrollment e = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("选课记录不存在"));

        // 2. 找到对应课程
        Course course = courseRepo.findById(e.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("课程不存在"));

        // 3. 人数-1
        int now = course.getEnrolled();
        course.setEnrolled(now - 1);
        courseRepo.save(course);

        // 4. 把记录标为 DROPPED（或直接删除，作业要求直接删也行）
        enrollmentRepo.deleteById(enrollmentId);
    }
}