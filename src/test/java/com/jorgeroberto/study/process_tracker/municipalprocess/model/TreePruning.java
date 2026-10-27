package com.jorgeroberto.study.process_tracker.municipalprocess.model;

import java.math.BigDecimal;

public class TreePruning extends MunicipalProcess{

    @Override
    public BigDecimal calculateIssuanceFee() {
        return new BigDecimal("10.00");
    }
}
