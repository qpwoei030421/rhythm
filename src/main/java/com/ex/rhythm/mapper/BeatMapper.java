package com.ex.rhythm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ex.rhythm.dto.BeatDTO;

@Mapper
public interface BeatMapper {

    // 비트 저장
    void insertBeat(BeatDTO beatDto);

    // 한 곡의 비트 조회
    List<BeatDTO> selectMusic(String musicTitle);

}
