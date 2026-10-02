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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    public MessageDto sendMessage(Long propertyId, String senderUsername, String subject, String content) {
        if (subject == null || subject.isBlank() || subject.length() > 255
                || content == null || content.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A subject and message are required");
        }
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));

        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        Message message = new Message();
        message.setProperty(property);
        message.setSender(sender);
        message.setSubject(subject.trim());
        message.setContent(content.trim());
        message.setIsRead(false);
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
