package com.jorgeroberto.study.process_tracker.sanitarylicense.model;

import com.jorgeroberto.study.process_tracker.municipalprocess.model.MunicipalProcess;

import java.math.BigDecimal;

public class SanitaryLicense extends MunicipalProcess {

    @Override
    public BigDecimal calculateIssuanceFee() {
        return new BigDecimal("35.00");
    }
}
