package com.ex.rhythm.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ex.rhythm.dto.UserMusicDTO;
import com.ex.rhythm.service.UserMusicService;
import com.ex.rhythm.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/rhythm")
public class gameBoardController {

    private final UserService userService;
    private final UserMusicService userMusicService;

    @GetMapping("/gameBoard")
    public String gameBoard(@RequestParam("musicTitle") String musicTitle, Model model) {
        model.addAttribute("musicTitle", musicTitle);
        model.addAttribute("encodedMusicTitle", URLEncoder.encode(musicTitle, StandardCharsets.UTF_8));
        return "gameBoard";
    }

    @GetMapping("/selectMusic")
    public String selectMusic(Principal principal, Model model) {

        List<UserMusicDTO> userMusics = userMusicService
                .findMusicByUserId(userService.findIdByEmail(principal.getName()));
        model.addAttribute("musics", userMusics);

        return "audio/selectMusic";
    }

    // 게임 종료 후 처리
    @GetMapping("/gameOver")
    public String gameOver(@RequestParam("score") int score, @RequestParam("userMaxCombo") int maxCombo,
            @RequestParam("musicTitle") String musicTitle, Model model) {

        model.addAttribute("score", score);
        model.addAttribute("maxCombo", maxCombo);
        model.addAttribute("musicTitle", musicTitle);
        model.addAttribute("encodedMusicTitle", URLEncoder.encode(musicTitle, StandardCharsets.UTF_8));

        return "gameOver";
    }
}
