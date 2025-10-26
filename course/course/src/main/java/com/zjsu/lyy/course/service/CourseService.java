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

    public Course getById(String id) {
        return repo.findById(id)
                   .orElseThrow(() -> new IllegalArgumentException("课程不存在"));
    }

    public Course update(String id, Course newCourse) {
        Course old = getById(id);          // 不存在会抛异常
        if (newCourse.getCode() != null) old.setCode(newCourse.getCode());
        if (newCourse.getTitle() != null) old.setTitle(newCourse.getTitle());
        return repo.save(old);
    }

    public void delete(String id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("课程不存在");
        repo.deleteById(id);
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