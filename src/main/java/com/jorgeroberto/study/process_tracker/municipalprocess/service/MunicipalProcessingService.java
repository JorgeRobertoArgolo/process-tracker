package com.jorgeroberto.study.process_tracker.municipalprocess.service;

import com.jorgeroberto.study.process_tracker.municipalprocess.dto.response.ProcessingResultResponseDTO;
import com.jorgeroberto.study.process_tracker.municipalprocess.model.MunicipalProcess;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MunicipalProcessingService implements IMunicipalProcessingService {

    @Override
    public ProcessingResultResponseDTO process(final List<MunicipalProcess> processes) {
        BigDecimal totalFee = BigDecimal.ZERO;
        final List<MunicipalProcess> directlyApproved = new ArrayList<>();
        final List<MunicipalProcess> pendingReview = new ArrayList<>();

        for (final MunicipalProcess process : processes) {
            totalFee = totalFee.add(process.calculateIssuanceFee());
            if (process.isDirectApprovalAllowed()) {
                directlyApproved.add(process);
            } else {
                pendingReview.add(process);
            }
        }

        return new ProcessingResultResponseDTO(totalFee, List.copyOf(directlyApproved), List.copyOf(pendingReview));
    }
}
