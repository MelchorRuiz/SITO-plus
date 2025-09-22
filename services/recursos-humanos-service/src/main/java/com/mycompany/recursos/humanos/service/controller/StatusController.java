package com.mycompany.recursos.humanos.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/status")
public class StatusController {
    @GetMapping
    public Map<String, String> status() {
        return Map.of("status", "ok");
    }
}
