package com.ex.rhythm.audio;

import be.tarsos.dsp.beatroot.BeatRootOnsetEventHandler;
import be.tarsos.dsp.onsets.OnsetHandler;

public class MyBeatHandler implements OnsetHandler {

    @Override
    public void handleOnset(double time, double salience) {
        // 비트 감지 시간과 salience 출력
        System.out.println("감지된 비트 시간: " + time + ", salience: " + salience);
    }

    public static void main(String[] args) {
        MyBeatHandler myBeatHandler = new MyBeatHandler();

        // BeatRootOnsetEventHandler가 생성된 후 trackBeats 메서드를 호출하여 비트를 추적할 수 있습니다.
        BeatRootOnsetEventHandler beatHandler = new BeatRootOnsetEventHandler();
        beatHandler.trackBeats(myBeatHandler); // 비트 추적을 시작합니다.
    }
}
