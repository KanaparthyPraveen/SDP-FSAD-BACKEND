package com.placeiq.config;

import com.placeiq.dto.RegisterRequest;
import com.placeiq.model.Company;
import com.placeiq.repository.CompanyRepository;
import com.placeiq.repository.UserRepository;
import com.placeiq.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Arrays;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final CompanyRepository companyRepository;
    private final com.placeiq.repository.ApplicationRepository applicationRepository;

    @Override
    public void run(String... args) {
        try {
            log.info("Checking for default accounts (seed data)...");

            // Seed Admin User
            if (!userRepository.existsByEmail("admin@pis.com")) {
                RegisterRequest admin = new RegisterRequest();
                admin.setName("Admin User");
                admin.setEmail("admin@pis.com");
                admin.setPassword("Admin@1234");
                admin.setRole("admin");
                admin.setDepartment("Placement Cell");
                
                authService.register(admin);
                log.info("Created default Admin user: admin@pis.com");
            } else {
                log.info("Default Admin already exists.");
            }

            // Seed Student User
            if (!userRepository.existsByEmail("praveen@student.com")) {
                RegisterRequest student = new RegisterRequest();
                student.setName("Praveen Kumar");
                student.setEmail("praveen@student.com");
                student.setPassword("Admin@1234");
                student.setRole("student");
                student.setDepartment("Computer Science");
                student.setRollNo("CS21B001");
                student.setYear(4);

                authService.register(student);
                log.info("Created default Student user: praveen@student.com");
            } else {
                log.info("Default Student already exists.");
            }

            // Seed Companies
            if (companyRepository.count() == 0) {
                Company google = new Company();
                google.setName("Google");
                google.setLogo("🌐");
                google.setRole("Software Engineer");
                google.setCtc("45 LPA");
                google.setLocation("Bangalore");
                google.setType("Product Based");
                google.setMinCgpa(8.5);
                google.setMaxBacklogs(0);
                google.setSkills(Arrays.asList("Java", "C++", "System Design"));
                google.setStatus("active");
                google.setOpenings(10);
                google.setDeadline("2026-05-01");
                google.setDescription("Join Google and build for the next billion users.");
                google.setAboutCompany("Google specializes in Internet-related services and products.");
                
                Company amazon = new Company();
                amazon.setName("Amazon");
                amazon.setLogo("🛒");
                amazon.setRole("SDE 1");
                amazon.setCtc("32 LPA");
                amazon.setLocation("Hyderabad");
                amazon.setType("Product Based");
                amazon.setMinCgpa(7.5);
                amazon.setMaxBacklogs(1);
                amazon.setSkills(Arrays.asList("Java", "AWS", "DSA"));
                amazon.setStatus("active");
                amazon.setOpenings(25);
                amazon.setDeadline("2026-04-20");
                amazon.setDescription("Work at Earth's most customer-centric company.");
                amazon.setAboutCompany("Amazon is an American multinational technology company.");

                Company microsoft = new Company();
                microsoft.setName("Microsoft");
                microsoft.setLogo("💻");
                microsoft.setRole("Software Developer");
                microsoft.setCtc("40 LPA");
                microsoft.setLocation("Noida");
                microsoft.setType("Product Based");
                microsoft.setMinCgpa(8.0);
                microsoft.setMaxBacklogs(0);
                microsoft.setSkills(Arrays.asList("C#", ".NET", "Azure"));
                microsoft.setStatus("upcoming");
                microsoft.setOpenings(15);
                microsoft.setDeadline("2026-06-15");
                microsoft.setDescription("Empowering every person and organization on the planet.");
                microsoft.setAboutCompany("Microsoft produces computer software, consumer electronics, PCs.");

                Company tcs = new Company();
                tcs.setName("TCS");
                tcs.setLogo("🏢");
                tcs.setRole("Systems Engineer");
                tcs.setCtc("7.5 LPA");
                tcs.setLocation("Pune");
                tcs.setType("Service Based");
                tcs.setMinCgpa(6.5);
                tcs.setMaxBacklogs(2);
                tcs.setSkills(Arrays.asList("Java", "SQL", "Python"));
                tcs.setStatus("closed");
                tcs.setOpenings(100);
                tcs.setDeadline("2026-03-01");
                tcs.setDescription("Join India's largest IT services brand.");
                tcs.setAboutCompany("Tata Consultancy Services is an IT services and consulting company.");

                companyRepository.saveAll(Arrays.asList(google, amazon, microsoft, tcs));
                log.info("Created test companies: Google, Amazon, Microsoft, TCS");
            } else {
                log.info("Companies already seeded.");
            }

            // Seed Applications
            if (applicationRepository.count() == 0) {
                userRepository.findByEmail("praveen@student.com").ifPresent(student -> {
                    List<Company> companies = companyRepository.findAll();
                    if (companies.size() >= 3) {
                        Company google = companies.stream().filter(c -> c.getName().equals("Google")).findFirst().orElse(companies.get(0));
                        Company amazon = companies.stream().filter(c -> c.getName().equals("Amazon")).findFirst().orElse(companies.get(1));
                        Company microsoft = companies.stream().filter(c -> c.getName().equals("Microsoft")).findFirst().orElse(companies.get(2));

                        try {
                            com.placeiq.model.Application.ApplicantSnapshot snapshot = new com.placeiq.model.Application.ApplicantSnapshot(
                                    student.getId(), student.getName(), student.getEmail(), student.getPhone(),
                                    student.getDepartment(), student.getRollNo(), student.getYear(),
                                    8.5, 0, Arrays.asList("Java", "React", "Spring Boot"),
                                    null, null, null, null
                            );                    com.placeiq.model.Application app1 = new com.placeiq.model.Application();
                        app1.setCompanyId(google.getId());
                        app1.setCompanyName(google.getName());
                        app1.setCompanyLogo(google.getLogo());
                        app1.setRole(google.getRole());
                        app1.setPackageOffered(google.getCtc());
                        app1.setApplicant(snapshot);
                        app1.setStatus("interview");
                        app1.setCurrentRound("Technical Round - 1");
                        app1.setAppliedDate("2026-03-01");
                        app1.setAppliedAt(java.time.Instant.now().minusSeconds(86400 * 10).toString());
                        app1.setProbability(85.0);
                        app1.setRounds(Arrays.asList(
                                new com.placeiq.model.Application.ApplicationRound("Online Assessment", "CLEARED", java.time.Instant.now().minusSeconds(86400 * 5).toString()),
                                new com.placeiq.model.Application.ApplicationRound("Technical Round - 1", "ONGOING", null),
                                new com.placeiq.model.Application.ApplicationRound("Technical Round - 2", "PENDING", null),
                                new com.placeiq.model.Application.ApplicationRound("HR Round", "PENDING", null)
                        ));

                        com.placeiq.model.Application app2 = new com.placeiq.model.Application();
                        app2.setCompanyId(amazon.getId());
                        app2.setCompanyName(amazon.getName());
                        app2.setCompanyLogo(amazon.getLogo());
                        app2.setRole(amazon.getRole());
                        app2.setPackageOffered(amazon.getCtc());
                        app2.setApplicant(snapshot);
                        app2.setStatus("applied");
                        app2.setCurrentRound("Online Assessment");
                        app2.setAppliedDate("2026-04-05");
                        app2.setAppliedAt(java.time.Instant.now().minusSeconds(86400 * 2).toString());
                        app2.setProbability(70.0);
                        app2.setRounds(Arrays.asList(
                                new com.placeiq.model.Application.ApplicationRound("Online Assessment", "PENDING", null),
                                new com.placeiq.model.Application.ApplicationRound("Technical Round - 1", "PENDING", null),
                                new com.placeiq.model.Application.ApplicationRound("Technical Round - 2", "PENDING", null),
                                new com.placeiq.model.Application.ApplicationRound("HR Round", "PENDING", null)
                        ));

                        com.placeiq.model.Application app3 = new com.placeiq.model.Application();
                        app3.setCompanyId(microsoft.getId());
                        app3.setCompanyName(microsoft.getName());
                        app3.setCompanyLogo(microsoft.getLogo());
                        app3.setRole(microsoft.getRole());
                        app3.setPackageOffered(microsoft.getCtc());
                        app3.setApplicant(snapshot);
                        app3.setStatus("rejected");
                        app3.setCurrentRound("Technical Round - 1");
                        app3.setAppliedDate("2026-02-15");
                        app3.setAppliedAt(java.time.Instant.now().minusSeconds(86400 * 25).toString());
                        app3.setProbability(20.0);
                        app3.setRounds(Arrays.asList(
                                new com.placeiq.model.Application.ApplicationRound("Online Assessment", "CLEARED", java.time.Instant.now().minusSeconds(86400 * 20).toString()),
                                new com.placeiq.model.Application.ApplicationRound("Technical Round - 1", "REJECTED", java.time.Instant.now().minusSeconds(86400 * 15).toString()),
                                new com.placeiq.model.Application.ApplicationRound("Technical Round - 2", "PENDING", null),
                                new com.placeiq.model.Application.ApplicationRound("HR Round", "PENDING", null)
                        ));

                            applicationRepository.saveAll(Arrays.asList(app1, app2, app3));
                            log.info("Created test applications with multi-stage rounds for praveen@student.com");
                        } catch (Exception e) {
                            log.error("Failed to seed applications: ", e);
                        }
                    }
                });
            } else {
                log.info("Applications already seeded.");
            }
        } catch (Exception e) {
            log.error("========================================");
            log.error("DATABASE CONNECTION OR SEEDING FAILED!");
            log.error("Ensure MONGO_URI is set properly. Application will continue without seeding.");
            log.error("Error: {}", e.getMessage());
            log.error("========================================");
        }
    }
}
