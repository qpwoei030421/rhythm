package com.ex.rhythm.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ex.rhythm.audio.AudioAnalyzer;
import com.ex.rhythm.service.UserMusicService;
import com.ex.rhythm.service.UserService;

@RestController
public class FileUploadConroller {

    // 음악 업로드 처리
    @Value("${file.upload-dir}") // application.properties에서 설정한 업로드 폴더 경로
    private String uploadDir;

    @Autowired
    private AudioAnalyzer audioAnalyzer;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMusicService userMusicService;

    @PostMapping("audioUpload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, Principal principal) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어있습니다.");
        }

        try {

            // 저장할 폴더 경로 생성
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            // UUID를 통한 고유한 파일 이름 생성
            String userFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String filePath = uploadDir + "/" + userFileName;

            // user_musics에 저장
            String username = principal.getName();
            userMusicService.saveMusic(userService.findIdByEmail(username), userFileName);

            // 파일 저장
            file.transferTo(new File(filePath));

            try {

                // FLAC 또는 다른 형식이 파일이면 WAV로 변환
                String fileExtension = getFileExtension(file.getOriginalFilename());

                if (!"wav".equalsIgnoreCase(fileExtension)) {
                    System.out.println(filePath);
                    // 변환 후 WAV 파일로 저장
                    String convertedFilePath = convertToWav(filePath);

                    // 음악 분석 실행
                    audioAnalyzer.analyzeAudio(convertedFilePath, userFileName);

                } else {
                    // 이미 WAV 파일이면 바로 분석
                    audioAnalyzer.analyzeAudio(filePath, userFileName);
                }
            } catch (Exception e) {
                return ResponseEntity.status(500).body("파일 분석 실패 : " + e.getMessage());
            }
            return ResponseEntity.ok("파일 업로드 성공: " + file.getOriginalFilename());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("파일 업로드 실패: " + e.getMessage());
        }
    }

    // 파일 확장자 추출
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    // FFmpeg로 FLAC이나 다른 파일 형식을 WAV로 변환하는 메서드
    private String convertToWav(String inputFilePath) throws IOException {
        String outputFilePath = inputFilePath.substring(0, inputFilePath.lastIndexOf('.')) + ".wav";

        ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-i", inputFilePath, outputFilePath);

        processBuilder.inheritIO();

        Process process = processBuilder.start();

        try {
            process.waitFor(); // ffmpeg가 종료될 때까지 기다림
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 변환된 WAV 파일 경로 리턴
        return outputFilePath;
    }
}
