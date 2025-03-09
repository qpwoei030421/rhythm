package com.ex.rhythm.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ex.rhythm.dto.RankDTO;
import com.ex.rhythm.dto.UserDTO;
import com.ex.rhythm.dto.UserMusicDTO;
import com.ex.rhythm.service.RankService;
import com.ex.rhythm.service.UserMusicService;
import com.ex.rhythm.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/rank")
public class RankController {

    private final RankService rankService;
    private final UserMusicService userMusicService;
    private final UserService userService;

    // 점수 저장 처리
    @GetMapping("/save")
    public String saveRank(@RequestParam("score") int score, @RequestParam("maxCombo") int maxCombo,
            @RequestParam("musicTitle") String musicTitle, Principal principal) {

        RankDTO rankDTO = new RankDTO();

        rankDTO.setMusicId(
                userMusicService.findMusicIdByMusicTitle(URLDecoder.decode(musicTitle, StandardCharsets.UTF_8)));
        rankDTO.setScore(score);
        rankDTO.setMaxCombo(maxCombo);

        rankService.saveRank(rankDTO);

        return "redirect:/rank/list";
    }

    // 점수 리스트
    @GetMapping("/list")
    public String list(Model model) {

        List<RankDTO> rankDTOs = rankService.findRank();
        List<UserMusicDTO> userMusicDTOs = new ArrayList<UserMusicDTO>();
        List<UserDTO> userDTOs = new ArrayList<UserDTO>();

        for (int i = 0; i < rankDTOs.size(); i++) {
            userMusicDTOs.add(userMusicService.findMusicById(rankDTOs.get(i).getMusicId()));
        }

        for (int i = 0; i < userMusicDTOs.size(); i++) {
            userDTOs.add(userService.findUserById(userMusicDTOs.get(i).getUserId()));
        }

        model.addAttribute("ranks", rankDTOs);
        model.addAttribute("musics", userMusicDTOs);
        model.addAttribute("users", userDTOs);

        return "rank";
    }

}
