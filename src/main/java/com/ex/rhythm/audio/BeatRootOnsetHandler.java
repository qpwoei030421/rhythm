package com.ex.rhythm.audio;

import java.util.ArrayList;
import java.util.List;

public class BeatRootOnsetHandler extends MyBeatHandler {
    private List<List<Double>> beats = new ArrayList<>();

    // 비트 감지 시 호출되는 메서드
    @Override
    public void handleOnset(double time, double salience) {
        // 각 비트의 정보
        List<Double> beatInfo = new ArrayList<Double>();

        // 감지된 비트 시간 리스트에 추가
        beatInfo.add(time);

        // 감지된 세기 리스트에 추가
        beatInfo.add(salience);

        // 감지된 비트 추가
        beats.add(beatInfo);
        // System.out.println("비트 감지 시점: " + time);
    }

    // 감지된 비트를 반환하는 메서드
    public List<List<Double>> getBeats() {
        return beats;
    }

    // 비트 리스트 초기화 메서드
    public void clearBeats() {
        beats.clear();
    }
}
