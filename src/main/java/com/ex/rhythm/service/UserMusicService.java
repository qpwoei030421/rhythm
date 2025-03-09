package com.ex.rhythm.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ex.rhythm.dto.UserMusicDTO;
import com.ex.rhythm.mapper.UserMusicMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserMusicService {

    private final UserMusicMapper userMusicMapper;

    // 음악 저장
    public void saveMusic(int userId, String musicTitle) {
        UserMusicDTO userMusicDTO = new UserMusicDTO();

        userMusicDTO.setUserId(userId);
        userMusicDTO.setMusicTitle(musicTitle);

        userMusicMapper.insertMusic(userMusicDTO);

    }

    // id로 음악 불러오기
    public List<UserMusicDTO> findMusicByUserId(int userId) {

        return userMusicMapper.selectMusicByUserId(userId);
    }

    // id로 음악 삭제하기
    public void deleteMusicById(int id) {
        userMusicMapper.deleteMusicById(id);
    }

    // musicTitle로 musicId 찾기
    public int findMusicIdByMusicTitle(String musicTitle) {
        return userMusicMapper.selectMusicIdByMusicTitle(musicTitle);
    }

    // musicId로 음악 조회
    public UserMusicDTO findMusicById(int id) {
        return userMusicMapper.selectMusicById(id);
    }

}