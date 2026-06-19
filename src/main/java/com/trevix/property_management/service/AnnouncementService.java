package com.trevix.property_management.service;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.trevix.property_management.dto.request.AnnouncementCreateRequest;
import com.trevix.property_management.dto.response.AnnouncementResponse;
import com.trevix.property_management.entity.Announcement;
import com.trevix.property_management.entity.Property;
import com.trevix.property_management.entity.User;
import com.trevix.property_management.enums.ErrorCode;
import com.trevix.property_management.exception.AppException;
import com.trevix.property_management.repository.AnnouncementRepository;
import com.trevix.property_management.repository.PropertyRepository;
import com.trevix.property_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @Transactional
    public AnnouncementResponse create(UUID ownerId, AnnouncementCreateRequest request) {
        Property property = propertyRepository.findById(request.getPropertyId())
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Property not found: " + request.getPropertyId()));

        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found: " + ownerId));

        Announcement announcement = new Announcement();
        announcement.setProperty(property);
        announcement.setPostedBy(owner);
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());

        Announcement saved = announcementRepository.save(announcement);
        log.info("Announcement created: {} for property: {}", saved.getId(), property.getId());
        return toResponse(saved);
    }

    public List<AnnouncementResponse> getByOwner(UUID ownerId) {
        return announcementRepository.findByOwnerId(ownerId)
            .stream().map(this::toResponse).toList();
    }

    public List<AnnouncementResponse> getByProperty(UUID propertyId) {
        return announcementRepository.findByProperty_IdOrderByCreatedAtDesc(propertyId)
            .stream().map(this::toResponse).toList();
    }

    public AnnouncementResponse getById(UUID id) {
        return toResponse(findById(id));
    }

    @Transactional
    public AnnouncementResponse update(UUID id, AnnouncementCreateRequest request) {
        Announcement announcement = findById(id);
        if (request.getTitle() != null) announcement.setTitle(request.getTitle());
        if (request.getContent() != null) announcement.setContent(request.getContent());
        return toResponse(announcementRepository.save(announcement));
    }

    @Transactional
    public void delete(UUID id) {
        if (!announcementRepository.existsById(id))
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Announcement not found: " + id);
        announcementRepository.deleteById(id);
        log.info("Announcement deleted: {}", id);
    }

    private Announcement findById(UUID id) {
        return announcementRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Announcement not found: " + id));
    }

    private AnnouncementResponse toResponse(Announcement a) {
        return AnnouncementResponse.builder()
            .id(a.getId())
            .propertyId(a.getProperty() != null ? a.getProperty().getId() : null)
            .propertyName(a.getProperty() != null ? a.getProperty().getName() : null)
            .postedById(a.getPostedBy() != null ? a.getPostedBy().getId() : null)
            .postedByName(a.getPostedBy() != null ? a.getPostedBy().getFullName() : null)
            .title(a.getTitle())
            .content(a.getContent())
            .createdAt(a.getCreatedAt())
            .updatedAt(a.getUpdatedAt())
            .build();
    }
}