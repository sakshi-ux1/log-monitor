package com.sakshi.logmonitor;


import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;


public class AlertEngine {
private final String targetLevel;
private final int threshold;
private final long windowMillis;
private final long cooldownMillis;


private final Deque<Long> window = new ArrayDeque<>();
private boolean alertActive = false;
private long lastAlertMillis = 0L;


public AlertEngine(String targetLevel, int threshold, long windowMinutes, long cooldownSeconds) {
this.targetLevel = targetLevel;
this.threshold = threshold;
this.windowMillis = Duration.ofMinutes(windowMinutes).toMillis();
this.cooldownMillis = Duration.ofSeconds(cooldownSeconds).toMillis();
}


public Optional<String> onEvent(LogEntry e) {
if (!e.level.equalsIgnoreCase(targetLevel)) {
evictOld(e.timestamp.toEpochMilli());
return Optional.empty();
}


long now = e.timestamp.toEpochMilli();
evictOld(now);
window.addLast(now);


if (!alertActive && window.size() >= threshold) {
if (now - lastAlertMillis >= cooldownMillis) {
alertActive = true;
lastAlertMillis = now;
return Optional.of("ALERT: High " + targetLevel + " rate — " + window.size() +
" in last " + (windowMillis / 60000) + " min at line #" + e.lineNumber);
}
}
return Optional.empty();
}


public Optional<String> checkRecovery(Instant currentTime) {
long now = currentTime.toEpochMilli();
evictOld(now);
if (alertActive && window.size() < threshold) {
alertActive = false;
return Optional.of("RECOVERY: Error rate back to normal.");
}
return Optional.empty();
}


private void evictOld(long nowMillis) {
while (!window.isEmpty() && nowMillis - window.peekFirst() > windowMillis) {
window.removeFirst();
}
}
}