package com.amin.aggar.api.controller;

import com.amin.aggar.api.dto.PriceHistoryDto;
import com.amin.aggar.service.PriceHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/price-history")
public class PriceHistoryController {

    private final PriceHistoryService service;

    public PriceHistoryController(PriceHistoryService service) {
        this.service = service;
    }

    @GetMapping
    public Page<PriceHistoryDto> list(Pageable pageable) { return service.list(pageable); }

    @GetMapping("/{id}")
    public ResponseEntity<PriceHistoryDto> get(@PathVariable Long id) { return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }

    @PostMapping
    public ResponseEntity<PriceHistoryDto> create(@RequestBody PriceHistoryDto dto) { PriceHistoryDto created = service.create(dto); return ResponseEntity.created(URI.create("/api/price-history/" + created.getId())).body(created); }

    @PutMapping("/{id}")
    public ResponseEntity<PriceHistoryDto> update(@PathVariable Long id, @RequestBody PriceHistoryDto dto) { return service.update(id, dto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { boolean removed = service.delete(id); return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build(); }
}

