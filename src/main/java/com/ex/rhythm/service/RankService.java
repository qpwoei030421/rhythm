package com.ex.rhythm.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ex.rhythm.dto.RankDTO;
import com.ex.rhythm.mapper.RankMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RankService {

    private final RankMapper rankMapper;

    // 점수 저장
    public void saveRank(RankDTO rankDTO) {
        rankMapper.insertRank(rankDTO);
    }

    // musicId로 점수 조회
    public List<RankDTO> findRank() {

        return rankMapper.selectRank();
    }
}
