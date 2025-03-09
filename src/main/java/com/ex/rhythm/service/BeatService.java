package com.ex.rhythm.service;

import com.ex.rhythm.dto.BeatDTO;
import com.ex.rhythm.mapper.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BeatService {

    private final BeatMapper beatMapper;

    // 비트 저장
    public void saveBeat(BeatDTO beat) {
        beatMapper.insertBeat(beat);
    }

    // 한 곡의 비트 조회
    public List<BeatDTO> getMusic(String musicTitle) {
        return beatMapper.selectMusic(musicTitle);
    }
}
