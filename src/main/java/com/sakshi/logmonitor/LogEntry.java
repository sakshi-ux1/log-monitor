package com.sakshi.logmonitor;


import java.time.Instant;


public class LogEntry {
public final long lineNumber;
public final Instant timestamp;
public final String level;
public final String message;


public LogEntry(long lineNumber, Instant timestamp, String level, String message) {
this.lineNumber = lineNumber;
this.timestamp = timestamp;
this.level = level;
this.message = message;
}


@Override
public String toString() {
return "#" + lineNumber + " " + timestamp + " [" + level + "] " + message;
}
}