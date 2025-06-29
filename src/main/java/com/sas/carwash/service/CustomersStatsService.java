package com.sas.carwash.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sas.carwash.entity.Estimate;
import com.sas.carwash.entity.Invoice;
import com.sas.carwash.repository.EstimateRepository;
import com.sas.carwash.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomersStatsService {

        private final EstimateRepository estimateRepository;
        private final InvoiceRepository inoviceRepository;

        // REPORT START
        private Map<String, Object> processCustomerServiceInfo(List<Estimate> estimates, List<Invoice> invoices) {
                Map<String, Object> result = new HashMap<>();

                record CustomerRecord(String ownerPhoneNumber, String ownerName, BigDecimal grandTotal) {
                }

                List<CustomerRecord> allRecords = new ArrayList<>();

                // Collect all records from Estimate
                for (Estimate est : estimates) {
                        if (est.getOwnerPhoneNumber() != null) {
                                allRecords.add(new CustomerRecord(
                                                est.getOwnerPhoneNumber(),
                                                est.getOwnerName(),
                                                Optional.ofNullable(est.getGrandTotal()).orElse(BigDecimal.ZERO)));
                        }
                }

                // Collect all records from Invoice
                for (Invoice inv : invoices) {
                        if (inv.getOwnerPhoneNumber() != null) {
                                allRecords.add(new CustomerRecord(
                                                inv.getOwnerPhoneNumber(),
                                                inv.getOwnerName(),
                                                Optional.ofNullable(inv.getGrandTotal()).orElse(BigDecimal.ZERO)));
                        }
                }

                // Group by phone number
                Map<String, List<CustomerRecord>> groupedByPhone = allRecords.stream()
                                .collect(Collectors.groupingBy(CustomerRecord::ownerPhoneNumber));

                // maxGrandTotalByCustomer
                List<Map<String, Object>> maxGrandTotalList = groupedByPhone.values().stream()
                                .map(list -> {
                                        CustomerRecord max = list.stream()
                                                        .max(Comparator.comparing(CustomerRecord::grandTotal))
                                                        .orElse(null);
                                        if (max != null) {
                                                return Map.<String, Object>of(max.ownerName() + " " + max.ownerPhoneNumber(), max.grandTotal());
                                        }
                                        return null;
                                })
                                .filter(Objects::nonNull)
                                .sorted((a, b) -> {
                                        BigDecimal v1 = (BigDecimal) a.values().iterator().next();
                                        BigDecimal v2 = (BigDecimal) b.values().iterator().next();
                                        return v2.compareTo(v1); // descending
                                })
                                .collect(Collectors.toList());

                // visitCountByCustomer
                List<Map<String, Object>> visitCountList = groupedByPhone.values().stream()
                                .map(list -> {
                                        String name = list.get(0).ownerName() + " " + list.get(0).ownerPhoneNumber(); // pick any name
                                        long count = list.size();
                                        return Map.<String, Object>of(name, count);
                                })
                                .sorted((a, b) -> {
                                        Long v1 = (Long) a.values().iterator().next();
                                        Long v2 = (Long) b.values().iterator().next();
                                        return v2.compareTo(v1); // descending
                                })
                                .collect(Collectors.toList());

                List<Map<String, Object>> totalContributionList = groupedByPhone.values().stream()
                                .map(list -> {
                                        String name = list.get(0).ownerName() + " " + list.get(0).ownerPhoneNumber(); // pick any one name
                                        BigDecimal total = list.stream()
                                                        .map(CustomerRecord::grandTotal)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        return Map.<String, Object>of(name, total);
                                })
                                .sorted((a, b) -> {
                                        BigDecimal v1 = (BigDecimal) a.values().iterator().next();
                                        BigDecimal v2 = (BigDecimal) b.values().iterator().next();
                                        return v2.compareTo(v1); // descending
                                })
                                .collect(Collectors.toList());

                result.put("totalContributionByCustomer", totalContributionList);                            
                result.put("maxGrandTotalByCustomer", maxGrandTotalList);
                result.put("visitCountByCustomer", visitCountList);

                return result;
        }

        public Map<String, Object> getDailyCustomerStats(LocalDate date) {
                LocalDateTime start = date.atStartOfDay();
                LocalDateTime end = start.plusDays(1);
                List<Estimate> records = estimateRepository.findByBillCloseDateBetween(start, end);
                List<Invoice> invoiceRecords = inoviceRepository.findByBillCloseDateBetween(start, end);
                return processCustomerServiceInfo(records, invoiceRecords);
        }

        public Map<String, Object> getWeeklyCustomerStats(int year, int week) {
                List<Estimate> records = estimateRepository.findAll().stream().filter(e -> {
                        LocalDateTime dateTime = e.getBillCloseDate();
                        return dateTime.getYear() == year && dateTime.get(WeekFields.ISO.weekOfYear()) == week;
                }).collect(Collectors.toList());
                List<Invoice> invoiceRecords = inoviceRepository.findAll().stream().filter(e -> {
                        LocalDateTime dateTime = e.getBillCloseDate();
                        return dateTime.getYear() == year && dateTime.get(WeekFields.ISO.weekOfYear()) == week;
                }).collect(Collectors.toList());
                return processCustomerServiceInfo(records, invoiceRecords);
        }

        public Map<String, Object> getMonthlyCustomerStats(int year, int month) {
                List<Estimate> records = estimateRepository.findAll().stream()
                                .filter(e -> e.getBillCloseDate().getYear() == year
                                                && e.getBillCloseDate().getMonthValue() == month)
                                .collect(Collectors.toList());
                List<Invoice> invoiceRecords = inoviceRepository.findAll().stream()
                                .filter(e -> e.getBillCloseDate().getYear() == year
                                                && e.getBillCloseDate().getMonthValue() == month)
                                .collect(Collectors.toList());
                return processCustomerServiceInfo(records, invoiceRecords);
        }

        public Map<String, Object> getYearlyCustomerStats(int year) {
                List<Estimate> records = estimateRepository.findAll().stream()
                                .filter(e -> e.getBillCloseDate().getYear() == year)
                                .collect(Collectors.toList());
                List<Invoice> invoiceRecords = inoviceRepository.findAll().stream()
                                .filter(e -> e.getBillCloseDate().getYear() == year)
                                .collect(Collectors.toList());
                return processCustomerServiceInfo(records, invoiceRecords);
        }

        public Map<String, Object> getCustomerStatsByDateRange(LocalDate start, LocalDate end) {
                LocalDateTime startDateTime = start.atStartOfDay();
                LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();
                List<Estimate> records = estimateRepository.findByBillCloseDateBetween(startDateTime, endDateTime);
                List<Invoice> invoiceRecords = inoviceRepository.findByBillCloseDateBetween(startDateTime, endDateTime);
                return processCustomerServiceInfo(records, invoiceRecords);
        }

}
