package com.ex.rhythm.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ex.rhythm.dto.BeatDTO;
import com.ex.rhythm.service.BeatService;

@RestController
@RequestMapping("/api")
public class BeatConroller {

    private final BeatService beatService;

    public BeatConroller(BeatService beatService) {
        this.beatService = beatService;
    }

    @GetMapping("/beats")
    public List<BeatDTO> getBeats(@RequestParam("musicTitle") String musicTitle) {
        return beatService.getMusic(musicTitle);
    }

    // 음악 업로드 처리
    @Value("${file.upload-dir}") // application.properties에서 설정한 업로드 폴더 경로
    private String uploadDir;

    @GetMapping("/music")
    public ResponseEntity<Resource> getMusic(@RequestParam("musicTitle") String title) throws IOException {
        // 음악 파일 경로 설정
        String musicDirectory = uploadDir; // 음악 파일이 저장된 디렉터리 경로
        File musicFile = new File(musicDirectory + "/" + title);

        // 파일이 존재하는지 확인
        if (!musicFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 파일 스트림을 사용하여 응답으로 반환
        InputStreamResource resource = new InputStreamResource(new FileInputStream(musicFile));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/flac"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + musicFile.getName() + "\"")
                .body(resource);
    }

}
