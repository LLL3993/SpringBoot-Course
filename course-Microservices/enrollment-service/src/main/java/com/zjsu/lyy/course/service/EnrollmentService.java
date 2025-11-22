package com.zjsu.lyy.course.service;

import com.zjsu.lyy.course.model.Enrollment;
import com.zjsu.lyy.course.repository.EnrollmentRepository;
import com.zjsu.lyy.course.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepo;
    private final StudentRepository studentRepo;
    private final RestTemplate restTemplate;

    @Value("${catalog-service.url:http://localhost:8081}")
    private String catalogUrl;

    public EnrollmentService(EnrollmentRepository enrollmentRepo,
                             StudentRepository studentRepo,
                             RestTemplate restTemplate) {
        this.enrollmentRepo = enrollmentRepo;
        this.studentRepo = studentRepo;
        this.restTemplate = restTemplate;
    }

    /* ========== 查询 ========== */
    public List<Enrollment> getAll() { return enrollmentRepo.findAll(); }
    public List<Enrollment> getByCourse(String courseCode) { return enrollmentRepo.findByCourseId(courseCode); }
    public List<Enrollment> getByStudent(String studentId) { return enrollmentRepo.findByStudentId(studentId); }

    /* ========== 选课 ========== */
    public Enrollment enroll(String courseCode, String studentId) {
        // 1. 学生存在性
        if (!studentRepo.existsByStudentId(studentId)) {
            throw new IllegalArgumentException("学生不存在");
        }
        // 2. 课程存在性 + 拿容量/已选（远程）
        Map<String, Object> resp;
        try {
            resp = restTemplate.getForObject(
                    catalogUrl + "/api/courses/code/" + courseCode,
                    Map.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("课程不存在");
        }
        Map<String, Object> courseData = (Map<String, Object>) resp.get("data");
        Integer capacity = (Integer) courseData.get("capacity");
        Integer enrolled = (Integer) courseData.get("enrolled");

        // 3. 重复选课
        if (enrollmentRepo.existsByCourseIdAndStudentId(courseCode, studentId)) {
            throw new IllegalArgumentException("重复选课");
        }
        // 4. 容量
        if (enrolled >= capacity) {
            throw new IllegalArgumentException("课程容量已满");
        }
        // 5. 新建选课
        Enrollment e = new Enrollment();
        e.setCourseId(courseCode);
        e.setStudentId(studentId);
        e.setStatus(Enrollment.Status.ACTIVE);
        Enrollment saved = enrollmentRepo.save(e);

        // 6. 远程+1（异步，失败也不管）
        restTemplate.put(
                catalogUrl + "/api/courses/code/" + courseCode,
                Map.of("enrolled", enrolled + 1));
        return saved;
    }

    /* ========== 退课 ========== */
    public void drop(String enrollmentId) {
        Enrollment e = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("选课记录不存在"));
        // 人数-1
        Map<String, Object> resp = restTemplate.getForObject(
                catalogUrl + "/api/courses/code/" + e.getCourseId(),
                Map.class);
        Map<String, Object> courseData = (Map<String, Object>) resp.get("data");
        Integer enrolled = (Integer) courseData.get("enrolled");
        restTemplate.put(
                catalogUrl + "/api/courses/code/" + e.getCourseId(),
                Map.of("enrolled", enrolled - 1));
        enrollmentRepo.delete(e);
    }
}