package com.zjsu.lyy.course.controller;

import com.zjsu.lyy.course.model.Course;
import com.zjsu.lyy.course.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService service;

    public CourseController(CourseService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> all() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "Success");
        resp.put("data", service.getAll());
        return resp;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)   // 201
    public Map<String, Object> create(@Valid @RequestBody Course course) {
        Course saved = service.create(course);   // service 里已设置 UUID
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 201);
        resp.put("message", "Created");
        resp.put("data", saved);
        return resp;
    }
}