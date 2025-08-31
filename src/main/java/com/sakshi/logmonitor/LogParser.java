package com.sakshi.logmonitor;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {
    // Accepts:
    // 2025-08-30 22:55:01 ERROR First error
    // 2025-08-30 22:55:01 [ERROR] First error
    // 2025-08-30 22:55:01,123 ERROR First error
    // 2025-08-30 22:55:01.123 [ERROR] First error
    private static final Pattern P1 = Pattern.compile(
        "^(?<ts>\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(?:[.,]\\d{3})?)\\s+\\[?(?<lvl>TRACE|DEBUG|INFO|WARN|ERROR|FATAL)\\]?\\s+(?<msg>.*)$"
    );

    private static final DateTimeFormatter[] FMT = new DateTimeFormatter[]{
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    };

    public static LogEntry parse(String line, long lineNo) {
        Matcher m = P1.matcher(line);
        if (!m.matches()) return null;
        String ts = m.group("ts");
        String lvl = m.group("lvl");
        String msg = m.group("msg");

        Instant instant = parseTs(ts);
        if (instant == null) return null;
        return new LogEntry(lineNo, instant, lvl, msg);
    }

    private static Instant parseTs(String ts) {
        for (DateTimeFormatter f : FMT) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(ts.replace('.', ','), f);
                return ldt.atZone(ZoneId.systemDefault()).toInstant();
            } catch (DateTimeParseException ignored) {}
        }
        return null;
    }
}
