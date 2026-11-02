package com.jorgeroberto.study.process_tracker.municipalprocess.service;

import com.jorgeroberto.study.process_tracker.municipalprocess.dto.response.ProcessingResultResponseDTO;
import com.jorgeroberto.study.process_tracker.municipalprocess.model.MunicipalProcess;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public interface IMunicipalProcessingService {

    ProcessingResultResponseDTO process(final List<? extends MunicipalProcess> processes);

    <T extends MunicipalProcess> List<T> filter(
            Collection<? extends T> processes,
            Predicate <? super T> criterion
    );
}
