package com.placeiq.repository;

import com.placeiq.model.Application;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {
    List<Application> findByApplicantUserId(String userId);
    List<Application> findByCompanyId(String companyId);
    List<Application> findByStatus(String status);
    boolean existsByApplicantUserIdAndCompanyId(String userId, String companyId);
    List<Application> findByApplicantUserIdOrderByAppliedAtDesc(String userId);
}
