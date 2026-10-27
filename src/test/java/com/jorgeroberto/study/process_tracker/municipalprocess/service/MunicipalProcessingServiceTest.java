package com.jorgeroberto.study.process_tracker.municipalprocess.service;

import com.jorgeroberto.study.process_tracker.buildingpermit.model.BuildingPermit;
import com.jorgeroberto.study.process_tracker.eventauthorization.model.EventAuthorization;
import com.jorgeroberto.study.process_tracker.municipalprocess.dto.response.ProcessingResultResponseDTO;
import com.jorgeroberto.study.process_tracker.municipalprocess.model.TreePruning;
import com.jorgeroberto.study.process_tracker.sanitarylicense.model.SanitaryLicense;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MunicipalProcessingServiceTest {

    final private IMunicipalProcessingService municipalProcessingService = new MunicipalProcessingService();

    @Test
    @DisplayName("O isDirectApprovalAllowed deve ser false, pois não foi sobrescrito pela classe filha.")
    void isDirectApprovalAllowed_shouldReturnFalseWhenSubclassDoesNotOverride() {
        final TreePruning treePruning = new TreePruning();
        assertFalse(treePruning.isDirectApprovalAllowed());
    }

    @Test
    @DisplayName("O Serviço deve ser categorizado pelo service como Review Pendente(pendingReview).")
    void process_shouldPutInPendingReviewWhenSubclassDoesNotOverride() {
        final TreePruning treePruning = new TreePruning();

        final ProcessingResultResponseDTO processingResultResponseDTO = municipalProcessingService
                .process(List.of(treePruning));

        assertThat(processingResultResponseDTO).isNotNull();
        assertThat(processingResultResponseDTO.pendingReview()).containsExactly(treePruning);
        assertThat(processingResultResponseDTO.directlyApproved()).isEmpty();
    }

    @Test
    @DisplayName("Deve somar as taxas e separar aprovados direto de pendentes em um lote misto")
    void process_shouldSumFeesAndSplitByDirectApproval() {
        final BuildingPermit buildingPermit = new BuildingPermit();
        final SanitaryLicense sanitaryLicense = new SanitaryLicense();
        final EventAuthorization eventAuthorization = new EventAuthorization(450, false);

        final ProcessingResultResponseDTO result = municipalProcessingService.process(List.of(buildingPermit, sanitaryLicense, eventAuthorization));

        assertThat(result).isNotNull();
        assertThat(result.pendingReview()).containsExactly(buildingPermit, sanitaryLicense);
        assertThat(result.directlyApproved()).containsExactly(eventAuthorization);

        assertThat(result.totalFee()).isEqualByComparingTo("105.00");
    }

    @Test
    @DisplayName("Deve aprovar o evento direto.")
    void process_shouldApproveDirectlyWhenAttendanceIsAtLimit() {
        final EventAuthorization eventAuthorization = new EventAuthorization(500, false);

        final ProcessingResultResponseDTO result = municipalProcessingService.process(List.of(eventAuthorization));

        assertThat(result).isNotNull();
        assertThat(result.pendingReview()).isEmpty();
        assertThat(result.directlyApproved()).containsExactly(eventAuthorization);
    }

    @Test
    @DisplayName("O evento deve ficar com revisão pendente.")
    void process_shouldPutInPendingReviewWhenAttendanceExceedsLimit() {
        final EventAuthorization eventAuthorization = new EventAuthorization(501, false);

        final ProcessingResultResponseDTO result = municipalProcessingService.process(List.of(eventAuthorization));

        assertThat(result).isNotNull();
        assertThat(result.pendingReview()).containsExactly(eventAuthorization);
        assertThat(result.directlyApproved()).isEmpty();
    }

    @Test
    @DisplayName("Se precisar que fechem a rua, deve ir direto para pendente de revisão")
    void process_shouldPutInPendingReviewWhenStreetClosureIsRequired() {
        final EventAuthorization eventAuthorization = new EventAuthorization(250, true);

        final ProcessingResultResponseDTO result = municipalProcessingService.process(List.of(eventAuthorization));

        assertThat(result).isNotNull();
        assertThat(result.pendingReview()).containsExactly(eventAuthorization);
        assertThat(result.directlyApproved()).isEmpty();
    }
}