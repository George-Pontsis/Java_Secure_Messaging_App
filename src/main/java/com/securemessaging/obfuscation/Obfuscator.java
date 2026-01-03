package com.securemessaging.obfuscation;
import java.util.*;
import java.util.concurrent.*;

public class Obfuscator {
    private static final int FIXED_RATE_MS = 1000;
    private static final Random random = new Random();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private String anonymousId;

    public Obfuscator() {
        this.anonymousId = generateAnonymousId();
    }

    private String generateAnonymousId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public String getAnonymousId() {
        return anonymousId;
    }

    public void addTimingJitter(Runnable sendAction) {
        int delay = random.nextInt(500) + 100;
        scheduler.schedule(sendAction, delay, TimeUnit.MILLISECONDS);
    }

    public void startDummyTraffic(com.securemessaging.transport.SocketCommunicator comm) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                String dummy = "DUMMY:" + System.currentTimeMillis();
                comm.sendPacket(dummy);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, FIXED_RATE_MS, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
