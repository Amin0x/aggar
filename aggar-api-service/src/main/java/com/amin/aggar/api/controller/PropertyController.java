package com.amin.aggar.api.controller;

import com.amin.aggar.api.dto.MessageDto;
import com.amin.aggar.api.dto.PropertyDto;
import com.amin.aggar.service.MessageService;
import com.amin.aggar.service.PropertyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService service;
    private final MessageService messageService;

    public PropertyController(PropertyService service, MessageService messageService) {
        this.service = service;
        this.messageService = messageService;
    }

    @GetMapping
    public Page<PropertyDto> list(
            @RequestParam(value = "listingType", required = false) String listingType,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "q", required = false) String q,
            Pageable pageable) { 
        return service.search(listingType, city, category, q, pageable); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDto> getById(@PathVariable("id") Long id) {
        return service.recordViewById(id)
                .map(p -> ResponseEntity.ok(p))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/view/{slug}")
    public ResponseEntity<PropertyDto> getBySlug(@PathVariable("slug") String slug) {
        return service.recordViewBySlug(slug)
                .map(p -> ResponseEntity.ok(p))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PropertyDto> create(@RequestBody PropertyDto dto) {
        PropertyDto created = service.create(dto); return ResponseEntity.created(URI.create("/api/properties/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyDto> update(@PathVariable("id") Long id, @RequestBody PropertyDto dto) {
        return service.update(id, dto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        boolean removed = service.delete(id); return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/admin/pending")
    public Page<PropertyDto> listPending(Pageable pageable) {
        return service.findByStatus("pending", pageable);
    }

    @PutMapping("/admin/{id}/approve")
    public ResponseEntity<PropertyDto> approve(@PathVariable("id") Long id) {
        return service.approve(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/admin/{id}/reject")
    public ResponseEntity<PropertyDto> reject(@PathVariable("id") Long id) {
        return service.reject(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<MessageDto> sendMessage(
            @PathVariable("id") Long id,
            @RequestParam("senderId") Long senderId,
            @RequestParam("subject") String subject,
            @RequestParam("content") String content) {
        MessageDto message = messageService.sendMessage(id, senderId, subject, content);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{id}/messages")
    public Page<MessageDto> getMessages(@PathVariable("id") Long id, Pageable pageable) {
        return messageService.getMessagesByProperty(id, pageable);
    }
}
