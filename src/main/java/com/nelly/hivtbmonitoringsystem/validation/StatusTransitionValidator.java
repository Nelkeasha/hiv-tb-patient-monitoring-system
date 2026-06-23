package com.nelly.hivtbmonitoringsystem.validation;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Centralizes the "this record must currently be in state X before you can
 * do Y" checks that used to be written ad hoc (with inconsistent messages)
 * inside {@code ReferralService} and {@code TracingTaskService}. Works for
 * either a real Java enum status or a plain String status field.
 */
@Component
public class StatusTransitionValidator {

    /** Throws unless {@code currentStatus} is one of the statuses {@code actionDescription} is allowed from. */
    public <T> void requireCurrentStatus(String entityLabel, T currentStatus, Set<T> allowed, String actionDescription) {
        if (!allowed.contains(currentStatus)) {
            String allowedList = allowed.stream().map(String::valueOf).collect(Collectors.joining(" or "));
            throw new BusinessRuleException("status",
                    entityLabel + " must currently be " + allowedList + " to " + actionDescription
                            + " (it is currently " + currentStatus + ")",
                    HttpStatus.CONFLICT);
        }
    }

    /** Throws if {@code currentStatus} is a terminal status that can no longer be changed. */
    public <T> void requireNotTerminal(String entityLabel, T currentStatus, Set<T> terminalStatuses) {
        if (terminalStatuses.contains(currentStatus)) {
            throw new BusinessRuleException("status",
                    entityLabel + " is already " + currentStatus + " and cannot be changed further",
                    HttpStatus.CONFLICT);
        }
    }
}
