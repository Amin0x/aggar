package com.amin.aggar.api.dto;

import java.time.LocalDateTime;

public class MessageDto {
    private Long id;
    private Long propertyId;
    private Long senderId;
    private String senderName;
    private String subject;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public MessageDto() {}

    public MessageDto(Long id, Long propertyId, Long senderId, String senderName,
                       String subject, String content, Boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.propertyId = propertyId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.subject = subject;
        this.content = content;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean read) { isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
