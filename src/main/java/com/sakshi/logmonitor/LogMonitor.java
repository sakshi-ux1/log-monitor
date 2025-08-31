package com.sakshi.logmonitor;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.Optional;
import java.util.Properties;

public class LogMonitor {

    public static void main(String[] args) throws Exception {
        boolean readStdin = false;
        String logFile = null;
        String configPath = "config/monitor.properties";

        // Parse arguments
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--stdin" -> readStdin = true;
                case "--log" -> logFile = args[++i];
                case "--config" -> configPath = args[++i];
                default -> {}
            }
        }

        // Load configuration
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(Path.of(configPath))) {
            p.load(in);
        }
        String level = p.getProperty("level", "ERROR");
        int threshold = Integer.parseInt(p.getProperty("threshold", "10"));
        long windowMinutes = Long.parseLong(p.getProperty("windowMinutes", "5"));
        long cooldownSeconds = Long.parseLong(p.getProperty("cooldownSeconds", "60"));
        Path alertsFile = Path.of(p.getProperty("alertsFile", "alerts/alerts.log"));
        Files.createDirectories(alertsFile.getParent());

        AlertEngine engine = new AlertEngine(level, threshold, windowMinutes, cooldownSeconds);

        try (BufferedWriter alertOut = Files.newBufferedWriter(alertsFile,
                java.nio.charset.StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

            if (readStdin) {
                process(new BufferedReader(new InputStreamReader(System.in)), engine, alertOut);
            } else if (logFile != null) {
                try (BufferedReader br = Files.newBufferedReader(Path.of(logFile))) {
                    process(br, engine, alertOut);
                }
            } else {
                System.err.println("Usage: --stdin OR --log <file> [--config <file>]");
                System.exit(2);
            }
        }
    }

    private static void process(BufferedReader br, AlertEngine engine, BufferedWriter alertOut) throws IOException {
        String line;
        long lineNo = 0L;
        while ((line = br.readLine()) != null) {
            lineNo++;
            LogEntry e = LogParser.parse(line, lineNo);
            if (e == null) continue; // skip unparseable

            Optional<String> maybeAlert = engine.onEvent(e);
            maybeAlert.ifPresent(msg -> writeAlert(msg, alertOut));

            // check recovery on every advance in time
            engine.checkRecovery(Instant.now()).ifPresent(msg -> writeAlert(msg, alertOut));
        }
    }

    private static void writeAlert(String msg, BufferedWriter out) {
        try {
            String line = Instant.now() + " ALERT: " + msg;
            out.write(line);
            out.newLine();
            out.flush();
            System.out.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
