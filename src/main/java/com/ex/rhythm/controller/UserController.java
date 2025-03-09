package com.ex.rhythm.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ex.rhythm.dto.UserDTO;
import com.ex.rhythm.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    // 로그인 페이지
    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(name = "rememberMe", required = false) boolean rememberMe,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model, @RequestParam(value = "error", required = false) String error) {
        try {
            System.out.println(1);
            if (error != null) {
                model.addAttribute("errorMessage", "아이디 또는 비밀번호가 잘못되었습니다.");
            }

            // 인증 객체 생성 (Spring Security 방식)
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                    password);

            // 인증 진행 (AuthenticationManager 사용)
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 인증 성공 시, SecurityContext에 저장
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            // 세션 생성 및 저장
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

            // Remember-Me 기능 (Spring Security 방식)
            if (rememberMe) {
                Cookie rememberMeCookie = new Cookie("remember-me", username);
                rememberMeCookie.setMaxAge(60 * 60 * 24 * 30); // 30일 유지
                rememberMeCookie.setHttpOnly(true);
                response.addCookie(rememberMeCookie);
            }

            return "redirect:/main";

        } catch (BadCredentialsException e) {
            System.out.println(e.getMessage());
            model.addAttribute("status", "failed");
            model.addAttribute("message", "잘못된 사용자명 또는 비밀번호입니다.");
            return "user/login";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("status", "failed");
            model.addAttribute("message", "로그인 중 오류가 발생했습니다.");
            return "user/login";
        }
    }

    // 계정 등록 페이지
    @GetMapping("/signUp")
    public String signUp() {
        return "user/register";
    }

    // 계정 중복 확인
    @PostMapping("/emailOverlappingCheck")
    public ResponseEntity<Map<String, Object>> overlappingCheck(@RequestBody Map<String, String> payload) {

        Map<String, Object> response = new HashMap<>();
        String username = payload.get("username");

        try {
            boolean overlapResult = this.userService.overlap(username);
            response.put("status", "success");

            if (!overlapResult) {
                response.put("authCode", "failed");
            } else {
                response.put("authCode", "success");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    private final BCryptPasswordEncoder passwordEncoder;

    // 계정 등록 처리
    @PostMapping("/signup")
    public String signup(@ModelAttribute UserDTO userDTO, Model model) {

        try {
            userDTO.setUserPw(passwordEncoder.encode(userDTO.getUserPw()));
            this.userService.signUp(userDTO);

            model.addAttribute("result", "success");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("result", "failed");
            model.addAttribute("error", "회원가입중 오류가 발생함");
        }
        return "user/signupPro";
    }
}
