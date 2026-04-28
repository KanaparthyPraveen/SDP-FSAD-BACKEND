package com.placeiq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    private String userId;

    /** STATUS_UPDATE | ROUND_UPDATE | NEW_COMPANY | DEADLINE_ALERT */
    private String type;

    private String title;
    private String message;
    private boolean read;
    private String link;
    private String createdAt;

    public static Notification create(String userId, String type, String title, String message, String link) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);
        n.setRead(false);
        n.setLink(link);
        n.setCreatedAt(Instant.now().toString());
        return n;
    }
}
