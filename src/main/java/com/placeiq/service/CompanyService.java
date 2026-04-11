package com.placeiq.service;

import com.placeiq.model.Company;
import com.placeiq.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public List<Company> getActiveCompanies() {
        return companyRepository.findByStatus("active");
    }

    public Company getCompanyById(String id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with id: " + id));
    }

    public Company createCompany(Company company) {
        if (company.getStatus() == null) company.setStatus("active");
        if (company.getRegistrations() == null) company.setRegistrations(0);
        return companyRepository.save(company);
    }

    public Company updateCompany(String id, Company updates) {
        Company existing = getCompanyById(id);
        if (updates.getName() != null)           existing.setName(updates.getName());
        if (updates.getLogo() != null)           existing.setLogo(updates.getLogo());
        if (updates.getRole() != null)           existing.setRole(updates.getRole());
        if (updates.getCtc() != null)            existing.setCtc(updates.getCtc());
        if (updates.getLocation() != null)       existing.setLocation(updates.getLocation());
        if (updates.getOpenings() != null)       existing.setOpenings(updates.getOpenings());
        if (updates.getMinCgpa() != null)        existing.setMinCgpa(updates.getMinCgpa());
        if (updates.getMaxBacklogs() != null)    existing.setMaxBacklogs(updates.getMaxBacklogs());
        if (updates.getDeadline() != null)       existing.setDeadline(updates.getDeadline());
        if (updates.getDescription() != null)    existing.setDescription(updates.getDescription());
        if (updates.getSkills() != null)         existing.setSkills(updates.getSkills());
        if (updates.getAllowedBranches() != null) existing.setAllowedBranches(updates.getAllowedBranches());
        if (updates.getStatus() != null)         existing.setStatus(updates.getStatus());
        if (updates.getRounds() != null)         existing.setRounds(updates.getRounds());
        if (updates.getCtcBreakdown() != null)   existing.setCtcBreakdown(updates.getCtcBreakdown());
        return companyRepository.save(existing);
    }

    public void deleteCompany(String id) {
        if (!companyRepository.existsById(id)) {
            throw new IllegalArgumentException("Company not found with id: " + id);
        }
        companyRepository.deleteById(id);
    }

    public List<Company> searchCompanies(String query) {
        return companyRepository.findByNameContainingIgnoreCase(query);
    }
}
