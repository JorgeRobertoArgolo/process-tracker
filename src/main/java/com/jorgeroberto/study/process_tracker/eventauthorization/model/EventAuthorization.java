package com.jorgeroberto.study.process_tracker.eventauthorization.model;

import com.jorgeroberto.study.process_tracker.municipalprocess.model.MunicipalProcess;

import java.math.BigDecimal;

public class EventAuthorization extends MunicipalProcess {

    private static final int MAX_ATTENDANCE_FOR_DIRECT_APPROVAL = 500;

    private final int estimatedAttendance;
    private final boolean requiresStreetClosure;

    public EventAuthorization(final int estimatedAttendance, final boolean requiresStreetClosure) {
        if (estimatedAttendance <= 0) {
            throw new IllegalArgumentException("O público estimado deve ser um número positivo maior que 0.");
        }
        this.estimatedAttendance = estimatedAttendance;
        this.requiresStreetClosure = requiresStreetClosure;
    }

    @Override
    public boolean isDirectApprovalAllowed() {
        return (this.estimatedAttendance <= MAX_ATTENDANCE_FOR_DIRECT_APPROVAL) && (!this.requiresStreetClosure);
    }

    @Override
    public BigDecimal calculateIssuanceFee() {
        return new BigDecimal("20.00");
    }
}
