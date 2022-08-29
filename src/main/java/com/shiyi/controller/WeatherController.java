package com.shiyi.controller;

import com.shiyi.scheduled.WeatherScheduled;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherScheduled weatherScheduled;

    @GetMapping("/")
    public String demo() {
        weatherScheduled.weather();
        return "发送成功";
    }
}
