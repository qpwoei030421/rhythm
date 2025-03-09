package com.ex.rhythm.audio;

import java.io.*;

public class AudioConverter {

    public static void convertFlacToWav(String inputFilePath, String outputFilePath) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-i", inputFilePath, outputFilePath);
        processBuilder.inheritIO();
        Process process = processBuilder.start();

        try {
            process.waitFor(); // ffmpeg가 종료될 때까지 기다림
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            String inputFilePath = "yourfile.flac";
            String outputFilePath = "yourfile.wav";
            convertFlacToWav(inputFilePath, outputFilePath);
            System.out.println("파일 변환 완료: " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}