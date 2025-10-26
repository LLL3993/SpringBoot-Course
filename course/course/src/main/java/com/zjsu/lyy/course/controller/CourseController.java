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

    @GetMapping("/{id}")
    public Map<String, Object> getOne(@PathVariable String id) {
        Course c = service.getById(id);
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "Success");
        resp.put("data", c);
        return resp;
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable String id,
                                    @Valid @RequestBody Course course) {
        Course updated = service.update(id, course);
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "Updated");
        resp.put("data", updated);
        return resp;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable String id) {
        service.delete(id);
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "Deleted");
        resp.put("data", null);
        return resp;
    }
}