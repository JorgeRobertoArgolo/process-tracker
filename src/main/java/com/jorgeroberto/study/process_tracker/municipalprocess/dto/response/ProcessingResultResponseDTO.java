package com.jorgeroberto.study.process_tracker.municipalprocess.dto.response;

import com.jorgeroberto.study.process_tracker.municipalprocess.model.MunicipalProcess;

import java.math.BigDecimal;
import java.util.List;

public record ProcessingResultResponseDTO(
        BigDecimal totalFee,
        List<MunicipalProcess> directlyApproved,
        List<MunicipalProcess> pendingReview
) {}
