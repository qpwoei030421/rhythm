package com.ex.rhythm.audio;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.ComplexOnsetDetector;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ex.rhythm.dto.BeatDTO;
import com.ex.rhythm.service.BeatService;

@Component
public class AudioAnalyzer {

    @Autowired
    public BeatService beatService;

    public void analyzeAudio(String filePath, String originalFileName) {
        try {

            // 오디오 파일 로드
            File audioFile = new File(filePath);
            AudioDispatcher dispatcher = AudioDispatcherFactory.fromFile(audioFile, 1024, 512);

            // 비트 감지 (onset detection)
            ComplexOnsetDetector onsetDetector = new ComplexOnsetDetector(1024);
            BeatRootOnsetHandler beatHandler = new BeatRootOnsetHandler();
            onsetDetector.setHandler(beatHandler);
            dispatcher.addAudioProcessor(onsetDetector);

            // 오디오 처리 시작
            dispatcher.run();

            // 감지된 비트 출력
            System.out.println("감지된 비트 수: " + beatHandler.getBeats().size());
            for (int i = 0; i < beatHandler.getBeats().size(); i++) {
                System.out.println("비트 타이밍, 비트 세기: " + beatHandler.getBeats().get(i));
            }

            /*
             * 비트 기반 노트 생성 로직
             * - 노트는 총 4종류 : d, f, j, k
             * - 노트의 위치는 소수점의 두 자릿수로만 판별 (12.345 -> 34)
             * -> 00 ~ 24 = d, 25 ~ 49 -> f, 50 ~ 74 -> j, 75 ~ 99 -> k
             * 
             * - 전체 비트의 상위 10%보다 강한 비트는 두개의 노트를 생성
             * - 노트의 위치는 소수점의 세 자릿수로만 판별 (12.345 -> 345)
             * -> 000 ~ 166 = (d, f), 167 ~ 333 = (d, j), 334 ~ 500 = (d, k),
             * 501 ~ 666 =(f, j), 667 ~ 833 = (f, k), 834 ~ 999 = (j, k)
             */

            // 상위 10%의 비트 구분
            List<Double> tempList = new ArrayList<Double>();

            for (int i = 0; i < beatHandler.getBeats().size(); i++) {
                tempList.add(beatHandler.getBeats().get(i).get(1));
            }

            tempList.sort(Collections.reverseOrder());

            int topTenCnt = Math.max(1, (int) Math.ceil(beatHandler.getBeats().size() * 0.1));
            Double topTen = tempList.get(topTenCnt - 1);
            for (int i = 0; i < beatHandler.getBeats().size(); i++) {

                BeatDTO beatDTO = new BeatDTO();
                List<Double> thisList = new ArrayList<Double>();
                Double note;

                thisList = beatHandler.getBeats().get(i);

                beatDTO.setMusicTitle(originalFileName);
                beatDTO.setTimeStamp(thisList.get(0));

                if (thisList.get(1) < topTen) {

                    note = thisList.get(1) - Math.floor(thisList.get(1)); // 소수 부분만 남김
                    note = Math.floor(note * 100); // 소수 부분 두 자리만 남김 (정수)

                    if (note >= 0 && note < 25) {
                        beatDTO.setD((byte) 1);
                        beatDTO.setF((byte) 0);
                        beatDTO.setJ((byte) 0);
                        beatDTO.setK((byte) 0);
                    } else if (note >= 25 && note < 50) {
                        beatDTO.setD((byte) 0);
                        beatDTO.setF((byte) 1);
                        beatDTO.setJ((byte) 0);
                        beatDTO.setK((byte) 0);
                    } else if (note >= 50 && note < 75) {
                        beatDTO.setD((byte) 0);
                        beatDTO.setF((byte) 0);
                        beatDTO.setJ((byte) 1);
                        beatDTO.setK((byte) 0);
                    } else {
                        beatDTO.setD((byte) 0);
                        beatDTO.setF((byte) 0);
                        beatDTO.setJ((byte) 0);
                        beatDTO.setK((byte) 1);
                    }

                } else {

                    note = thisList.get(1) - Math.floor(thisList.get(1)); // 소수 부분만 남김
                    note = Math.floor(note * 1000); // 소수 부분 세 자리만 남김 (정수)
                    // 000 ~ 166 = (d, f), 167 ~ 333 = (d, j), 334 ~ 500 = (d, k),
                    // 501 ~ 666 =(f, j), 667 ~ 833 = (f, k), 834 ~ 999 = (j, k)
                    if (note >= 0 && note < 167) {
                        beatDTO.setD((byte) 1);
                        beatDTO.setF((byte) 1);
                        beatDTO.setJ((byte) 0);
                        beatDTO.setK((byte) 0);
                    } else if (note >= 167 && note < 334) {
                        beatDTO.setD((byte) 1);
                        beatDTO.setF((byte) 0);
                        beatDTO.setJ((byte) 1);
                        beatDTO.setK((byte) 0);
                    } else if (note >= 334 && note < 501) {
                        beatDTO.setD((byte) 1);
                        beatDTO.setF((byte) 0);
                        beatDTO.setJ((byte) 0);
                        beatDTO.setK((byte) 1);
                    } else if (note >= 501 && note < 667) {
                        beatDTO.setD((byte) 0);
                        beatDTO.setF((byte) 1);
                        beatDTO.setJ((byte) 1);
                        beatDTO.setK((byte) 0);
                    } else if (note >= 667 && note < 834) {
                        beatDTO.setD((byte) 0);
                        beatDTO.setF((byte) 1);
                        beatDTO.setJ((byte) 0);
                        beatDTO.setK((byte) 1);
                    } else {
                        beatDTO.setD((byte) 0);
                        beatDTO.setF((byte) 0);
                        beatDTO.setJ((byte) 1);
                        beatDTO.setK((byte) 1);
                    }
                }

                // System.out.println(beatDTO.toString());
                beatService.saveBeat(beatDTO);

            }
            System.out.println("비트 저장 완료!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
