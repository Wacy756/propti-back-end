package com.propti.auth.repository;

import com.propti.auth.entity.Property;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, String> {
    List<Property> findByLandlordId(String landlordId);
}
