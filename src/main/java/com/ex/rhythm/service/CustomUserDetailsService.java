package com.ex.rhythm.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ex.rhythm.dto.UserDTO;
import com.ex.rhythm.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserDTO> user = userMapper.selectUserByEmail(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        return User.builder()
                .username(user.get().getUserEmail())
                .password(user.get().getUserPw()) // DB에서 가져온 비밀번호 사용
                .roles("USER") // 기본 역할 설정
                .build();
    }
}
