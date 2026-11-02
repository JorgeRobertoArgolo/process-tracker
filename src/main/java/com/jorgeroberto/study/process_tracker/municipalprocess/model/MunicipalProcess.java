package com.jorgeroberto.study.process_tracker.municipalprocess.model;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public abstract class MunicipalProcess implements Chargeable {
    private final UUID id;
    private final String ticketNumber;
    private final LocalDate creationDate;

    protected MunicipalProcess() {
        this.id = UUID.randomUUID();
        this.ticketNumber = "PROT-" + this.id;
        this.creationDate = LocalDate.now();
    }

    public boolean isDirectApprovalAllowed() {
        return false;
    }
}
