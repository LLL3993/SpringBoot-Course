package com.zjsu.lyy.course.repository;

import com.zjsu.lyy.course.model.Course;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CourseRepository {
    private final Map<String, Course> map = new ConcurrentHashMap<>();

    public List<Course> findAll() { return new ArrayList<>(map.values()); }
    public Optional<Course> findByCode(String code) {
        return map.values().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(code))
                .findFirst();
    }
    public boolean existsById(String id) { return map.containsKey(id); }
    public Course save(Course course) { map.put(course.getCode(), course); return course; }
    public void deleteByCode(String code) { map.remove(code); }
}