package com.zjsu.lyy.course.service;

import com.zjsu.lyy.course.model.Course;
import com.zjsu.lyy.course.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    private final CourseRepository repo;

    public CourseService(CourseRepository repo) { this.repo = repo; }

    public List<Course> getAll() {
        return repo.findAll();
    }

    public Course create(Course course) {
        if (course.getId() == null || course.getId().isBlank()) {
            throw new IllegalArgumentException("id 不能为空");
        }
        if (repo.existsById(course.getId())) {
            throw new IllegalArgumentException("课程 id 已存在");
        }
        return repo.save(course);
    }
}