package com.jorgeroberto.study.process_tracker.municipalprocess.service;

import com.jorgeroberto.study.process_tracker.municipalprocess.dto.response.ProcessingResultResponseDTO;
import com.jorgeroberto.study.process_tracker.municipalprocess.model.MunicipalProcess;

import java.util.List;

public interface IMunicipalProcessingService {
    ProcessingResultResponseDTO process(final List<MunicipalProcess> processes);
}
