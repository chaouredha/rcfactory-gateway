package org.rcfactory.rcfactorygateway.controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class FallBackController {

    @GetMapping("/defaultFinance")
    public Map<String, String> financeFallBack() {
        return Map.of("server", "available");
    }
}
