package com.orsac.oiipcra.serviceImpl;

import com.orsac.oiipcra.repository.ReportQryRepository;
import com.orsac.oiipcra.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportQryRepository reportQryRepository;

    @Override
    public List<Map<String, Object>> getReport(int finYearId) {
        return reportQryRepository.getReportInfo(finYearId);
    }
}
