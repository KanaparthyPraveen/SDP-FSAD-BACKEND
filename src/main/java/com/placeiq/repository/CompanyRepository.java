package com.placeiq.repository;

import com.placeiq.model.Company;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends MongoRepository<Company, String> {
    List<Company> findByStatus(String status);
    List<Company> findByNameContainingIgnoreCase(String name);
}
