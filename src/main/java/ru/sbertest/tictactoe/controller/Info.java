package ru.sbertest.tictactoe.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/info")
public class Info {

    @GetMapping
    public String mainInfo() {
        return "v.1.0.0";
    }
}
