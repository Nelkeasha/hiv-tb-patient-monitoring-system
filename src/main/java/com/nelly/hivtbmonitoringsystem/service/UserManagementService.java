package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.request.CreateChwRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.CreateProviderRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.CreateSupervisorRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.FacilityResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.StaffResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.UserSummaryResponse;
import com.nelly.hivtbmonitoringsystem.entity.Chw;
import com.nelly.hivtbmonitoringsystem.entity.Facility;
import com.nelly.hivtbmonitoringsystem.entity.FacilityProvider;
import com.nelly.hivtbmonitoringsystem.entity.Supervisor;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.enums.UserRole;
import com.nelly.hivtbmonitoringsystem.repository.ChwRepository;
import com.nelly.hivtbmonitoringsystem.repository.FacilityProviderRepository;
import com.nelly.hivtbmonitoringsystem.repository.FacilityRepository;
import com.nelly.hivtbmonitoringsystem.repository.SupervisorRepository;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final SystemUserRepository userRepository;
    private final ChwRepository chwRepository;
    private final FacilityProviderRepository providerRepository;
    private final SupervisorRepository supervisorRepository;
    private final FacilityRepository facilityRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public StaffResponse createChw(CreateChwRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already in use: " + req.getEmail());
        }
        Facility facility = findFacility(req.getFacilityId());
        String tempPassword = generateTempPassword();

        SystemUser user = SystemUser.builder()
                .fullName(req.getFullName())
                .email(req.getEmail())
                .phoneNumber(req.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(tempPassword))
                .role(UserRole.CHW)
                .isActive(true)
                .mustChangePassword(true)
                .preferredLanguage("rw")
                .build();
        userRepository.save(user);

        Chw chw = Chw.builder()
                .user(user)
                .facility(facility)
                .assignedVillage(req.getAssignedVillage())
                .assignedSector(req.getAssignedSector())
                .employeeCode(req.getEmployeeCode())
                .isActive(true)
                .build();
        chwRepository.save(chw);

        return StaffResponse.builder()
                .userId(user.getId())
                .staffId(chw.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .facilityName(facility.getName())
                .temporaryPassword(tempPassword)
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public StaffResponse createProvider(CreateProviderRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already in use: " + req.getEmail());
        }
        Facility facility = findFacility(req.getFacilityId());
        String tempPassword = generateTempPassword();

        SystemUser user = SystemUser.builder()
                .fullName(req.getFullName())
                .email(req.getEmail())
                .phoneNumber(req.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(tempPassword))
                .role(UserRole.FACILITY_PROVIDER)
                .isActive(true)
                .mustChangePassword(true)
                .preferredLanguage("rw")
                .build();
        userRepository.save(user);

        FacilityProvider provider = FacilityProvider.builder()
                .user(user)
                .facility(facility)
                .specialization(req.getSpecialization())
                .licenseNumber(req.getLicenseNumber())
                .build();
        providerRepository.save(provider);

        return StaffResponse.builder()
                .userId(user.getId())
                .staffId(provider.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .facilityName(facility.getName())
                .temporaryPassword(tempPassword)
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public StaffResponse createSupervisor(CreateSupervisorRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already in use: " + req.getEmail());
        }
        Facility facility = findFacility(req.getFacilityId());
        String tempPassword = generateTempPassword();

        SystemUser user = SystemUser.builder()
                .fullName(req.getFullName())
                .email(req.getEmail())
                .phoneNumber(req.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(tempPassword))
                .role(UserRole.SUPERVISOR)
                .isActive(true)
                .mustChangePassword(true)
                .preferredLanguage("rw")
                .build();
        userRepository.save(user);

        Supervisor supervisor = Supervisor.builder()
                .user(user)
                .facility(facility)
                .district(req.getDistrict())
                .build();
        supervisorRepository.save(supervisor);

        return StaffResponse.builder()
                .userId(user.getId())
                .staffId(supervisor.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .facilityName(facility.getName())
                .temporaryPassword(tempPassword)
                .createdAt(user.getCreatedAt())
                .build();
    }

    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> UserSummaryResponse.builder()
                        .id(u.getId())
                        .fullName(u.getFullName())
                        .email(u.getEmail())
                        .phoneNumber(u.getPhoneNumber())
                        .role(u.getRole().name())
                        .isActive(u.getIsActive())
                        .createdAt(u.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public UserSummaryResponse toggleUserStatus(UUID userId) {
        SystemUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
        return UserSummaryResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public StaffResponse resetPassword(UUID userId) {
        SystemUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        String tempPassword = generateTempPassword();
        user.setPasswordHash(passwordEncoder.encode(tempPassword));
        user.setMustChangePassword(true);
        userRepository.save(user);
        return StaffResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .temporaryPassword(tempPassword)
                .build();
    }

    public List<FacilityResponse> getFacilities() {
        return facilityRepository.findAll().stream()
                .map(f -> FacilityResponse.builder()
                        .id(f.getId())
                        .name(f.getName())
                        .location(f.getLocation())
                        .district(f.getDistrict())
                        .isActive(f.getIsActive())
                        .build())
                .collect(Collectors.toList());
    }

    private Facility findFacility(UUID facilityId) {
        return facilityRepository.findById(facilityId)
                .orElseThrow(() -> new RuntimeException("Facility not found: " + facilityId));
    }

    private String generateTempPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789@#!";
        SecureRandom rng = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        sb.append(Character.toUpperCase(chars.charAt(rng.nextInt(26))));
        sb.append(chars.charAt(26 + rng.nextInt(26)));
        sb.append(chars.charAt(52 + rng.nextInt(5)));
        for (int i = 3; i < 10; i++) {
            sb.append(chars.charAt(rng.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
