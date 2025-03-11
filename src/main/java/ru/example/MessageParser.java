package ru.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageParser {
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("^(\\w+)\\|(\\d+)\\|(\\w+)\\|(\\w+)$");

    public static class Message {
        public String type;
        public long timestamp;
        public String sender;
        public String content;

        public Message(String type, long timestamp, String sender, String content) {
            this.type = type;
            this.timestamp = timestamp;
            this.sender = sender;
            this.content = content;
        }

        @Override
        public String toString() {
            return "Message{" +
                    "type='" + type + '\'' +
                    ", timestamp=" + timestamp +
                    ", sender='" + sender + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    public static Message parse(String message) {
        Matcher matcher = MESSAGE_PATTERN.matcher(message);
        if (matcher.matches()) {
            return new Message(
                    matcher.group(1),
                    Long.parseLong(matcher.group(2)),
                    matcher.group(3),
                    matcher.group(4)
            );
        } else {
            throw new IllegalArgumentException("Invalid message format");
        }
    }
}
