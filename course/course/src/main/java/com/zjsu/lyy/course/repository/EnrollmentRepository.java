package com.zjsu.lyy.course.repository;

import com.zjsu.lyy.course.model.Enrollment;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EnrollmentRepository {
    private final Map<String, Enrollment> map = new ConcurrentHashMap<>();

    public List<Enrollment> findAll() { return new ArrayList<>(map.values()); }
    public List<Enrollment> findByCourseCode(String courseCode) {
        return map.values().stream()
                .filter(e -> e.getCourseId().equals(courseCode))
                .toList();
    }
    public List<Enrollment> findByStudentId(String studentId) {
        return map.values().stream()
                .filter(e -> e.getStudentId().equals(studentId))
                .toList();
    }
    public Optional<Enrollment> findById(String id) { return Optional.ofNullable(map.get(id)); }
    public boolean existsByCourseAndStudent(String courseId, String studentId) {
        return map.values().stream()
                .anyMatch(e -> e.getCourseId().equals(courseId)
                            && e.getStudentId().equals(studentId));
    }
    public Enrollment save(Enrollment e) { map.put(e.getId(), e); return e; }
    public void deleteById(String id) { map.remove(id); }
}