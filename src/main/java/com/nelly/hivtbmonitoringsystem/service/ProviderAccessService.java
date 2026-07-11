package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.entity.FacilityProvider;
import com.nelly.hivtbmonitoringsystem.entity.Patient;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.enums.UserRole;
import com.nelly.hivtbmonitoringsystem.repository.FacilityProviderRepository;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Doctor-level (treating-provider) patient scoping — one shared guard used by
 * the clinical dashboard, patient confirmation, and treatment-plan services.
 *
 * <p>Rules:
 * <ul>
 *   <li>ADMIN / SYSTEM_ADMIN bypass all provider scoping.</li>
 *   <li>A provider only sees/manages patients whose {@code managingProvider}
 *       is themselves — or patients with no managing provider yet
 *       (legacy/admin-registered rows and PROVISIONAL screening vouchers,
 *       which are unowned until a provider confirms them).</li>
 *   <li>Facility scoping still applies first: a provider never reaches
 *       another facility's patients at all.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class ProviderAccessService {

    private final SystemUserRepository userRepository;
    private final FacilityProviderRepository facilityProviderRepository;

    /** The calling user's provider profile, or empty for admins/other roles. */
    public Optional<FacilityProvider> currentProviderProfile() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .flatMap(u -> facilityProviderRepository.findByUserId(u.getId()));
    }

    public boolean isAdmin() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .map(SystemUser::getRole)
                .map(r -> r == UserRole.ADMIN || r == UserRole.SYSTEM_ADMIN)
                .orElse(false);
    }

    /** True if the given provider may see/manage this patient (provider layer only). */
    public boolean canManage(Patient patient, FacilityProvider provider) {
        if (patient.getManagingProvider() == null) return true; // unowned — facility-visible
        return provider != null
                && patient.getManagingProvider().getId().equals(provider.getId());
    }

    /**
     * Throws 403 unless the current user may see/manage this patient.
     * Admins always pass; providers pass for their own or unowned patients.
     */
    public void ensureCanManage(Patient patient) {
        if (isAdmin()) return;
        FacilityProvider me = currentProviderProfile().orElse(null);
        if (me != null && patient.getFacility() != null
                && !me.getFacility().getId().equals(patient.getFacility().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Patient is not registered at your facility");
        }
        if (!canManage(patient, me)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "This patient is managed by another provider");
        }
    }
}
