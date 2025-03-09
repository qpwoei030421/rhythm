package com.ex.rhythm.audio;

import java.net.MalformedURLException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/audio/")
public class AudioController {

    // 음악 업로드 페이지
    @GetMapping("audioUpload")
    public String audioUpload() {
        return "audio/audioUpload";
    }

    // 음악 분석 페이지
    @GetMapping("audioAnalyze/{fileName}")
    public String audioAnalyze(@PathVariable("fileName") String fileName) throws MalformedURLException {
        try {

            audioAnalyze(fileName);

            return "redirect:/main";
        } catch (Exception e) {
            return "redirect:/audio/audioUpload";
        }
    }
}
