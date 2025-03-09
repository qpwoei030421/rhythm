package com.ex.rhythm.mapper;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.ex.rhythm.dto.UserDTO;

@Mapper
public interface UserMapper {

    // 계정 등록
    void insertUser(UserDTO userDTO);

    // id로 계정 조회
    UserDTO selectUserById(int id);

    // 이메일로 계정 조회
    Optional<UserDTO> selectUserByEmail(String userEmail);

}
