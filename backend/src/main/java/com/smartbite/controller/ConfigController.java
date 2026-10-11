package com.smartbite.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Value("${smartbite.demo-mode:true}")
    private boolean demoMode;

    @Value("${smartbite.canteen.tables:T01,T02,T03,T04,T05,T06,T07,T08,T09,T10}")
    private String configuredTables;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getConfig() {
        List<String> tableList = Arrays.stream(
                (configuredTables != null ? configuredTables : "T01,T02,T03,T04,T05,T06,T07,T08,T09,T10").split(",")
        )
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "demoMode", demoMode,
                "tables", tableList,
                "canteenName", "SmartBite Campus Canteen"
        ));
    }
}
