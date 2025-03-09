package com.ex.rhythm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ex.rhythm.dto.UserMusicDTO;

@Mapper
public interface UserMusicMapper {

    // 음악 등록
    void insertMusic(UserMusicDTO userMusicDTO);

    // userId로 음악 불러오기
    List<UserMusicDTO> selectMusicByUserId(int userId);

    // id로 음악 삭제
    void deleteMusicById(int id);

    // musicTitle로 musicId 찾기
    int selectMusicIdByMusicTitle(String musicTitle);

    // musicId로 음악 조회
    UserMusicDTO selectMusicById(int id);
}
