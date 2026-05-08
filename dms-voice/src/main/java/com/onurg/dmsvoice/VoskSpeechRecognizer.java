package com.onurg.dmsvoice;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.vosk.Model;
import org.vosk.Recognizer;

public class VoskSpeechRecognizer implements AutoCloseable {

    private static final float DEFAULT_SAMPLE_RATE = 16000f;
    private final Model model;
    private final float sampleRate;

    public VoskSpeechRecognizer(Path modelPath) throws IOException {
        this(modelPath, DEFAULT_SAMPLE_RATE);
    }

    public VoskSpeechRecognizer(Path modelPath, float sampleRate) throws IOException {
        this.model = new Model(modelPath.toString());
        this.sampleRate = sampleRate;
    }

    public Optional<String> recognizeFromMicrophone(int maxSeconds) throws IOException, LineUnavailableException {
        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            throw new LineUnavailableException("Microphone does not support required format: " + format);
        }

        try (TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info)) {
            line.open(format);
            line.start();
            try (Recognizer recognizer = new Recognizer(model, sampleRate)) {
                byte[] buffer = new byte[4096];
                long stopTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(maxSeconds);
                while (System.currentTimeMillis() < stopTime) {
                    int read = line.read(buffer, 0, buffer.length);
                    if (read < 0) {
                        break;
                    }
                    recognizer.acceptWaveForm(buffer, read);
                }
                String result = recognizer.getFinalResult();
                if (result == null || result.isBlank()) {
                    return Optional.empty();
                }
                return Optional.of(result);
            }
        }
    }

    @Override
    public void close() {
        model.close();
    }
}
