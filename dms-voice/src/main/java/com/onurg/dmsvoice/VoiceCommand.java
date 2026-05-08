package com.onurg.dmsvoice;

import java.util.Objects;

public final class VoiceCommand {

    private final VoiceIntent intent;
    private final String payload;
    private final String target;

    public VoiceCommand(VoiceIntent intent, String target, String payload) {
        this.intent = Objects.requireNonNull(intent, "intent");
        this.target = target;
        this.payload = payload;
    }

    public VoiceIntent getIntent() {
        return intent;
    }

    public String getPayload() {
        return payload;
    }

    public String getTarget() {
        return target;
    }

    public boolean hasPayload() {
        return payload != null && !payload.isBlank();
    }

    public boolean hasTarget() {
        return target != null && !target.isBlank();
    }

    public static VoiceCommand unknown(String text) {
        return new VoiceCommand(VoiceIntent.UNKNOWN, null, text);
    }
}
