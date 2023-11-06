package com.parkingmanager.parkingmanager;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {
    private final Map<String, String> emailVerificationMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> codeAttemptsMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService;
    private final long codeTimeoutMillis = 3000000; // 5 minutes

    public VerificationCodeService(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public void storeVerificationCode(String email, String code) {
        emailVerificationMap.put(email, code);
    }

    public String getStoredVerificationCode(String email) {
        return emailVerificationMap.get(email);
    }

    public void incrementCodeAttempts(String email) {
        codeAttemptsMap.put(email, codeAttemptsMap.getOrDefault(email, 0) + 1);
    }

    public void resetCodeAttempts(String email) {
        codeAttemptsMap.remove(email);
    }

    public boolean isUserTimedOut(String email) {
        int codeAttempts = codeAttemptsMap.getOrDefault(email, 0);
        return codeAttempts >= 5; // Adjust as needed
    }

    public void scheduleCodeTimeout(String email) {
        ScheduledFuture<?> future = scheduledExecutorService.schedule(() -> {
            resetCodeAttempts(email);
        }, codeTimeoutMillis, TimeUnit.MILLISECONDS);
    }
}
