package com.shiyi.controller;

import com.shiyi.scheduled.DemoScheduled;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DemoController {

    private final DemoScheduled demoScheduled;

    @GetMapping("/test")
    public String demo() {
        demoScheduled.weather();
        return "发送成功";
    }
}
