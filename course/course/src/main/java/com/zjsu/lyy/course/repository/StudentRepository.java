package com.zjsu.lyy.course.repository;

import com.zjsu.lyy.course.model.Student;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class StudentRepository {
    private final Map<String, Student> map = new ConcurrentHashMap<>();

    public List<Student> findAll() { return new ArrayList<>(map.values()); }
    public Optional<Student> findById(String id) { return Optional.ofNullable(map.get(id)); }
    public Optional<Student> findByStudentId(String studentId) {
        return Optional.ofNullable(map.get(studentId));
    }
    public void deleteById(String id) { map.remove(id); }
    public boolean existsByStudentId(String studentId) {
        return map.containsKey(studentId);
    }

    public Student save(Student s) {
        if (s.getId() == null) { 
            s.setId(UUID.randomUUID().toString());
        }
        map.put(s.getStudentId(), s);   // 关键行
        return s;
    }
    public void deleteByStudentId(String studentId) {
        map.remove(studentId);
    }
}