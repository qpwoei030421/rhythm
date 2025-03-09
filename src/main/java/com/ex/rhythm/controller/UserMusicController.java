package com.ex.rhythm.controller;

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
@RequestMapping("/music")
public class UserMusicController {

    private final UserMusicService UserMusicService;
    private final UserService userService;

    // 음악 관리 리스트
    @GetMapping("/myMusic")
    public String myMusic(Principal principal, Model model) {

        List<UserMusicDTO> userMusicDTOs = UserMusicService
                .findMusicByUserId(userService.findIdByEmail(principal.getName()));

        model.addAttribute("musics", userMusicDTOs);

        return "audio/myMusic";
    }

    // 음악 삭제
    @GetMapping("/deleteMusic")
    public String deleteMusic(@RequestParam("id") int id) {

        UserMusicService.deleteMusicById(id);

        return "redirect:/music/myMusic";
    }
}
