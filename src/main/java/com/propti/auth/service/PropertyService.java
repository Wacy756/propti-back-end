package com.propti.auth.service;

import com.propti.auth.dto.PropertyDto;
import com.propti.auth.entity.Property;
import com.propti.auth.entity.Tenancy;
import com.propti.auth.repository.PropertyRepository;
import com.propti.auth.repository.TenancyRepository;
import com.propti.auth.dto.CreatePropertyRequest;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final TenancyRepository tenancyRepository;

    @Transactional(readOnly = true)
    public List<PropertyDto> listForLandlord(String landlordId) {
        return propertyRepository.findByLandlordId(landlordId).stream()
                .map(this::toDtoWithTenancy)
                .toList();
    }

    @Transactional
    public PropertyDto createProperty(String landlordId, CreatePropertyRequest request) {
        Property property = new Property();
        property.setId(request.getName() != null ? request.getName().replaceAll("\\s+", "-").toLowerCase() + "-" + System.currentTimeMillis() : java.util.UUID.randomUUID().toString());
        property.setLandlordId(landlordId);
        property.setName(request.getName());
        property.setAddress(request.getAddress());
        property.setPostcode(request.getPostcode());
        property.setRent(request.getRent());
        property.setStatus("Vacant");
        property.setPaid(Boolean.TRUE.equals(request.getPaid()));
        Property saved = propertyRepository.save(property);
        return toDtoWithTenancy(saved);
    }

    @Transactional
    public void markOccupied(String propertyId, UUID tenancyId) {
        propertyRepository.findById(propertyId).ifPresent(prop -> {
            prop.setStatus("Occupied");
            propertyRepository.save(prop);
        });
    }

    @Transactional(readOnly = true)
    public PropertyDto getById(String id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found: " + id));
        return toDtoWithTenancy(property);
    }

    @Transactional
    public PropertyDto updateProperty(String id, CreatePropertyRequest request) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found: " + id));
        final var tenancy = tenancyRepository.findFirstByPropertyId(id).orElse(null);
        if (tenancy != null) {
            // lock edits once tenancy started
            throw new IllegalStateException("Property details are locked once a tenancy is started.");
        }
        if (request.getName() != null) property.setName(request.getName());
        if (request.getAddress() != null) property.setAddress(request.getAddress());
        if (request.getPostcode() != null) property.setPostcode(request.getPostcode());
        if (request.getRent() != null) property.setRent(request.getRent());
        if (request.getPaid() != null) property.setPaid(request.getPaid());
        Property saved = propertyRepository.save(property);
        return toDtoWithTenancy(saved);
    }

    @Transactional
    public void deleteProperty(String id) {
        final var tenancyOpt = tenancyRepository.findFirstByPropertyId(id);
        if (tenancyOpt.isPresent()) {
            final var tenancy = tenancyOpt.get();
            if (tenancy.getTenantStatus() == com.propti.auth.entity.Tenancy.TenantStatus.ACTIVE) {
                throw new IllegalStateException("Cannot delete a property with an active tenancy.");
            }
            tenancyRepository.delete(tenancy);
        }
        propertyRepository.deleteById(id);
    }

    private PropertyDto toDtoWithTenancy(Property property) {
        final var tenancy = tenancyRepository.findFirstByPropertyId(property.getId()).orElse(null);
        return new PropertyDto(
                property.getId(),
                property.getLandlordId(),
                property.getName(),
                property.getAddress(),
                property.getPostcode(),
                property.getRent(),
                property.getStatus(),
                tenancy != null ? tenancy.getId() : null,
                tenancy != null ? tenancy.getTenantName() : null,
                tenancy != null ? tenancy.getTenantEmail() : null,
                property.getPaid(),
                tenancy != null ? tenancy.getTenantStatus() : null,
                tenancy != null ? tenancy.getReferenceStatus() : null,
                tenancy != null ? tenancy.getAgreementStatus() : null,
                property.getCreatedAt()
        );
    }
}
