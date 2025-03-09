package com.ex.rhythm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainMenuController {

    @GetMapping("/main")
    public String main() {
        return "main";
    }
}
