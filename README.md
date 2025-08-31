Log Monitoring System



This project is a simple Log Monitoring Tool built in Core Java that helps detect error patterns in application logs and generate alerts. It was created to practice concepts of Java, Data Structures, Algorithms, File Handling, Regex, and Configuration Management.



📌 Features



Monitors application logs for a specific log level (e.g., ERROR).



Reads log entries line by line and parses them.



Uses a sliding time window to count error occurrences.



Triggers an ALERT if errors exceed a defined threshold.



Supports cooldown period to avoid duplicate alerts.



Configurations are externalized in a properties file (no hardcoding).



Alerts are written into a separate file for review.



🛠️ Tech Used



Java (Core Java, Collections, Regex, DateTime API)



Properties File for external configuration



File I/O for reading logs and writing alerts



Command-line execution for flexibility



📂 Project Structure



LogMonitor.java → Main class that controls log monitoring and alerting



LogParser.java → Parses each log line using Regex and extracts timestamp, level, and message



LogEntry.java → A simple data model (POJO) that holds log details



monitor.properties → Configuration file with threshold, log level, window, etc.



app.log → Sample log file with entries



alerts.log → Stores generated alerts



⚙️ Configuration (monitor.properties)

level=ERROR

threshold=1

windowMinutes=5

cooldownSeconds=10

alertsFile=alerts/alerts.log





This makes the project flexible – you can change the monitoring rules without modifying the code.



🚀 How to Run



Compile and package the project:



mvn clean package





Run with your log file and configuration:



java -cp target/log-monitor-1.0-SNAPSHOT.jar com.sakshi.logmonitor.LogMonitor --log data/app.log --config config/monitor.properties



🌍 Real-World Use Case



In real systems, logs continuously record system activities. Monitoring them is crucial for:



Detecting application failures quickly



Identifying unusual patterns (e.g., too many login failures)



Triggering automated alerts for support teams



This project demonstrates the core idea of how log monitoring tools (like Splunk, ELK, Datadog) work at a basic level.



📖 Learning Outcome



Core Java programming



File handling and regex



Parsing and data modeling with classes



Time window–based monitoring logic



Externalizing configuration



Simulating alerting systems

