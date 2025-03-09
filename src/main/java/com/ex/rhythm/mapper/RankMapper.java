package com.ex.rhythm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ex.rhythm.dto.RankDTO;

@Mapper
public interface RankMapper {

    // 점수 저장
    void insertRank(RankDTO rankDTO);

    // musicId로 점수 조회
    List<RankDTO> selectRank();
}
