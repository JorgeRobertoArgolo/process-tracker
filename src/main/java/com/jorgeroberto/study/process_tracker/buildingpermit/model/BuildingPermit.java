package com.jorgeroberto.study.process_tracker.buildingpermit.model;

import com.jorgeroberto.study.process_tracker.municipalprocess.model.MunicipalProcess;

import java.math.BigDecimal;

public class BuildingPermit extends MunicipalProcess {

    @Override
    public BigDecimal calculateIssuanceFee() {
        return new BigDecimal("50.00");
    }
}
