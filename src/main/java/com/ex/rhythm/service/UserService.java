package com.ex.rhythm.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ex.rhythm.dto.UserDTO;
import com.ex.rhythm.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    // 계정 등록
    public void signUp(UserDTO userDTO) {
        userMapper.insertUser(userDTO);
    }

    // 계정 중복 확인
    public boolean overlap(String userEmail) {

        boolean result;

        if (!userMapper.selectUserByEmail(userEmail).isEmpty()) {
            result = false;
        } else {
            result = true;
        }

        return result;
    }

    // 이메일로 id 찾기
    public int findIdByEmail(String username) {
        int result;

        Optional<UserDTO> userDTO = userMapper.selectUserByEmail(username);

        if (!userDTO.isEmpty()) {
            result = userDTO.get().getId();
        } else {
            result = 0;
        }

        return result;
    }

    // id로 회원 정보 조회
    public UserDTO findUserById(int id) {
        return userMapper.selectUserById(id);
    }

}