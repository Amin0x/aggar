package com.amin.aggar.service;

import com.amin.aggar.api.dto.PriceHistoryDto;
import com.amin.aggar.domain.entity.PriceHistory;
import com.amin.aggar.repository.PriceHistoryRepository;
import com.amin.aggar.repository.PropertyRepository;
import com.amin.aggar.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Optional;

@Service
public class PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public PriceHistoryService(PriceHistoryRepository priceHistoryRepository, PropertyRepository propertyRepository, UserRepository userRepository) {
        this.priceHistoryRepository = priceHistoryRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    private PriceHistoryDto toDto(PriceHistory p) {
        if (p == null) return null;
        PriceHistoryDto d = new PriceHistoryDto(
                p.getId(),
                p.getProperty() != null ? p.getProperty().getId() : null,
                p.getOldPrice(),
                p.getNewPrice(),
                p.getChangedBy() != null ? p.getChangedBy().getId() : null,
                p.getChangedAt()
        );
        return d;
    }

    private PriceHistory fromDto(PriceHistoryDto d) {
        if (d == null) return null;
        PriceHistory p = new PriceHistory();
        p.setId(d.id());
        if (d.propertyId() != null) p.setProperty(propertyRepository.findById(d.propertyId()).orElse(null));
        p.setOldPrice(d.oldPrice());
        p.setNewPrice(d.newPrice());
        if (d.changedById() != null) p.setChangedBy(userRepository.findById(d.changedById()).orElse(null));
        p.setChangedAt(d.changedAt());
        return p;
    }

    public Page<PriceHistoryDto> list(Pageable pageable) {
        return priceHistoryRepository.findAll(pageable).map(this::toDto);
    }

    public Optional<PriceHistoryDto> findById(Long id) { return priceHistoryRepository.findById(id).map(this::toDto); }

    @Transactional
    public PriceHistoryDto create(PriceHistoryDto dto) {
        PriceHistory p = fromDto(dto);
        p.setId(null);
        PriceHistory saved = priceHistoryRepository.save(p);
        return toDto(saved);
    }

    @Transactional
    public Optional<PriceHistoryDto> update(Long id, PriceHistoryDto dto) {
        return priceHistoryRepository.findById(id).map(existing -> {
            if (dto.propertyId() != null) existing.setProperty(propertyRepository.findById(dto.propertyId()).orElse(null));
            existing.setOldPrice(dto.oldPrice());
            existing.setNewPrice(dto.newPrice());
            if (dto.changedById() != null) existing.setChangedBy(userRepository.findById(dto.changedById()).orElse(null));
            existing.setChangedAt(dto.changedAt());
            return toDto(priceHistoryRepository.save(existing));
        });
    }

    @Transactional
    public boolean delete(Long id) { return priceHistoryRepository.findById(id).map(p -> { priceHistoryRepository.delete(p); return true; }).orElse(false); }
}

