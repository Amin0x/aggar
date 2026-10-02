package com.amin.aggar.service;

import com.amin.aggar.api.dto.MessageDto;
import com.amin.aggar.domain.entity.Message;
import com.amin.aggar.domain.entity.Property;
import com.amin.aggar.domain.entity.User;
import com.amin.aggar.repository.MessageRepository;
import com.amin.aggar.repository.PropertyRepository;
import com.amin.aggar.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository,
                         PropertyRepository propertyRepository,
                         UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public MessageDto sendMessage(Long propertyId, Long senderId, String subject, String content) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found: " + propertyId));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + senderId));

        Message message = new Message();
        message.setProperty(property);
        message.setSender(sender);
        message.setSubject(subject);
        message.setContent(content);
        message.setIsRead(false);
        message.setCreatedAt(LocalDateTime.now());

        Message saved = messageRepository.save(message);

        return toDto(saved);
    }

    public Page<MessageDto> getMessagesByProperty(Long propertyId, Pageable pageable) {
        return messageRepository.findByPropertyId(propertyId, pageable)
                .map(this::toDto);
    }

    public Page<MessageDto> getMessagesBySender(Integer senderId, Pageable pageable) {
        return messageRepository.findBySenderId(senderId, pageable)
                .map(this::toDto);
    }

    public Page<MessageDto> getUnreadMessages(Pageable pageable) {
        return messageRepository.findByIsReadFalse(pageable)
                .map(this::toDto);
    }

    @Transactional
    public void markAsRead(Long messageId) {
        messageRepository.findById(messageId).ifPresent(msg -> {
            msg.setIsRead(true);
            messageRepository.save(msg);
        });
    }

    public long countUnreadByProperty(Long propertyId) {
        return messageRepository.countByPropertyIdAndIsReadFalse(propertyId);
    }

    private MessageDto toDto(Message message) {
        return new MessageDto(
                message.getId(),
                message.getProperty().getId(),
                message.getSender().getId(),
                message.getSender().getName(),
                message.getSubject(),
                message.getContent(),
                message.getIsRead(),
                message.getCreatedAt()
        );
    }
}
