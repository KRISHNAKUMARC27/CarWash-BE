package com.sas.carwash.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import com.sas.carwash.entity.JobSpares;
import com.sas.carwash.entity.JobSparesInfo;
import com.sas.carwash.model.InfoWrapper;
import com.sas.carwash.repository.JobSparesRepository;
import com.sas.carwash.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobStatsService {

    private final JobSparesRepository jobSparesRepository;

    // Service REPORT START
    private Map<String, Object> processJobSparesServiceInfo(List<JobSpares> records) {
        Map<String, Object> result = new HashMap<>();

        List<JobSpares> estimateJobSpares = records.stream().filter(job -> job.getEstimateObjId() != null)
                .collect(Collectors.toList());
        List<JobSpares> invoiceJobSpares = records.stream().filter(job -> job.getInvoiceObjId() != null)
                .collect(Collectors.toList());

        // Flatten all JobSparesInfo from all JobSpares
        List<JobSparesInfo> allEstimateServiceInfos = estimateJobSpares.stream()
                .flatMap(job -> {
                    List<JobSparesInfo> combined = new ArrayList<>();
                    // if (job.getJobSparesInfo() != null) combined.addAll(job.getJobSparesInfo());
                    if (job.getJobServiceInfo() != null)
                        combined.addAll(job.getJobServiceInfo());
                    return combined.stream();
                })
                .collect(Collectors.toList());

        // 1. category -> sum(amount)
        Map<String, BigDecimal> categoryToAmountEstimate = allEstimateServiceInfos.stream()
                .collect(Collectors.groupingBy(
                        JobSparesInfo::getCategory,
                        Collectors.mapping(
                                JobSparesInfo::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        // 2. category -> sum(qty)
        Map<String, BigDecimal> categoryToQtyEstimate = allEstimateServiceInfos.stream()
                .collect(Collectors.groupingBy(
                        JobSparesInfo::getCategory,
                        Collectors.mapping(
                                JobSparesInfo::getQty,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        // 3. sparesAndLabour -> sum(amount)
        Map<String, BigDecimal> sparesLabourToAmountEstimate = allEstimateServiceInfos.stream()
                .collect(Collectors.groupingBy(
                        JobSparesInfo::getSparesAndLabour,
                        Collectors.mapping(
                                JobSparesInfo::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        // 4. sparesAndLabour -> sum(qty)
        Map<String, BigDecimal> sparesLabourToQtyEstimate = allEstimateServiceInfos.stream()
                .collect(Collectors.groupingBy(
                        JobSparesInfo::getSparesAndLabour,
                        Collectors.mapping(
                                JobSparesInfo::getQty,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        // Flatten all JobSparesInfo from all JobSpares
        List<JobSparesInfo> allInvoiceServiceInfos = invoiceJobSpares.stream()
                .flatMap(job -> {
                    List<JobSparesInfo> combined = new ArrayList<>();
                    // if (job.getJobSparesInfo() != null) combined.addAll(job.getJobSparesInfo());
                    if (job.getJobServiceInfo() != null)
                        combined.addAll(job.getJobServiceInfo());
                    return combined.stream();
                })
                .collect(Collectors.toList());

        // 1. category -> sum(amount)
        Map<String, BigDecimal> categoryToAmountInvoice = allInvoiceServiceInfos.stream()
                .collect(Collectors.groupingBy(
                        JobSparesInfo::getCategory,
                        Collectors.mapping(
                                JobSparesInfo::getGstAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        // 2. category -> sum(qty)
        Map<String, BigDecimal> categoryToQtyInvoice = allInvoiceServiceInfos.stream()
                .collect(Collectors.groupingBy(
                        JobSparesInfo::getCategory,
                        Collectors.mapping(
                                JobSparesInfo::getQty,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        // 3. sparesAndLabour -> sum(amount)
        Map<String, BigDecimal> sparesLabourToAmountInvoice = allInvoiceServiceInfos.stream()
                .collect(Collectors.groupingBy(
                        JobSparesInfo::getSparesAndLabour,
                        Collectors.mapping(
                                JobSparesInfo::getGstAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        // 4. sparesAndLabour -> sum(qty)
        Map<String, BigDecimal> sparesLabourToQtyInvoice = allInvoiceServiceInfos.stream()
                .collect(Collectors.groupingBy(
                        JobSparesInfo::getSparesAndLabour,
                        Collectors.mapping(
                                JobSparesInfo::getQty,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        Map<String, BigDecimal> sortedCombinedCategoryToAmount = Utils.sortMapByValueDesc(Utils.mergeAndSum(
                categoryToAmountInvoice,
                categoryToAmountEstimate));
        result.put("categoryToAmount", sortedCombinedCategoryToAmount);

        Map<String, BigDecimal> sortedCombinedCategoryToQty = Utils.sortMapByValueDesc(Utils.mergeAndSum(
                categoryToQtyInvoice,
                categoryToQtyEstimate));
        result.put("categoryToQty", sortedCombinedCategoryToQty);

        Map<String, BigDecimal> sortedCombinedSparesLabourToAmount = Utils.sortMapByValueDesc(Utils.mergeAndSum(
                sparesLabourToAmountInvoice,
                sparesLabourToAmountEstimate));
        result.put("sparesLabourToAmount", sortedCombinedSparesLabourToAmount);

        Map<String, BigDecimal> sortedCombinedSparesLabourToQty = Utils.sortMapByValueDesc(Utils.mergeAndSum(
                sparesLabourToQtyInvoice,
                sparesLabourToQtyEstimate));
        result.put("sparesLabourToQty", sortedCombinedSparesLabourToQty);

        List<InfoWrapper> allWrappedInfos = new ArrayList<>();

        // Wrap estimate entries
        for (JobSpares job : estimateJobSpares) {
            if (job.getJobServiceInfo() != null) {
                for (JobSparesInfo info : job.getJobServiceInfo()) {
                    allWrappedInfos.add(new InfoWrapper(
                            info.getCategory(),
                            info.getSparesAndLabour(),
                            Optional.ofNullable(info.getAmount()).orElse(BigDecimal.ZERO),
                            Optional.ofNullable(info.getQty()).orElse(BigDecimal.ZERO)));
                }
            }
        }

        // Wrap invoice entries (use getGstAmount)
        for (JobSpares job : invoiceJobSpares) {
            if (job.getJobServiceInfo() != null) {
                for (JobSparesInfo info : job.getJobServiceInfo()) {
                    allWrappedInfos.add(new InfoWrapper(
                            info.getCategory(),
                            info.getSparesAndLabour(),
                            Optional.ofNullable(info.getGstAmount()).orElse(BigDecimal.ZERO),
                            Optional.ofNullable(info.getQty()).orElse(BigDecimal.ZERO)));
                }
            }
        }

        Map<String, List<Map<String, Object>>> maxQtyByCategory = new HashMap<>();
        Map<String, List<Map<String, Object>>> maxAmountByCategory = new HashMap<>();

        Map<String, Map<String, List<InfoWrapper>>> grouped = allWrappedInfos.stream()
                .collect(Collectors.groupingBy(
                        InfoWrapper::category,
                        Collectors.groupingBy(InfoWrapper::sparesAndLabour)));

        for (var categoryEntry : grouped.entrySet()) {
            String category = categoryEntry.getKey();
            Map<String, List<InfoWrapper>> sparesMap = categoryEntry.getValue();

            // Prepare lists to sort
            List<Map<String, Object>> qtyList = new ArrayList<>();
            List<Map<String, Object>> amountList = new ArrayList<>();

            for (var sparesEntry : sparesMap.entrySet()) {
                String spares = sparesEntry.getKey();
                List<InfoWrapper> entries = sparesEntry.getValue();

                BigDecimal totalAmount = entries.stream()
                        .map(InfoWrapper::amount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalQty = entries.stream()
                        .map(InfoWrapper::qty)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                qtyList.add(Map.of(
                        "sparesAndLabour", spares,
                        "qty", totalQty));

                amountList.add(Map.of(
                        "sparesAndLabour", spares,
                        "amount", totalAmount));
            }

            // Sort descending
            qtyList.sort((a, b) -> ((BigDecimal) b.get("qty")).compareTo((BigDecimal) a.get("qty")));
            amountList.sort((a, b) -> ((BigDecimal) b.get("amount")).compareTo((BigDecimal) a.get("amount")));

            maxQtyByCategory.put(category, qtyList);
            maxAmountByCategory.put(category, amountList);
        }

        result.put("maxQtyByCategory", maxQtyByCategory);
        result.put("maxAmountByCategory", maxAmountByCategory);

        return result;
    }

    public Map<String, Object> getDailyJobServiceStats(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<JobSpares> records = jobSparesRepository.findByJobCloseDateBetween(start, end);
        return processJobSparesServiceInfo(records);
    }

    public Map<String, Object> getWeeklyJobServiceStats(int year, int week) {
        List<JobSpares> records = jobSparesRepository.findAll().stream().filter(e -> {
            LocalDateTime dateTime = e.getJobCloseDate();
            return dateTime.getYear() == year && dateTime.get(WeekFields.ISO.weekOfYear()) == week;
        }).collect(Collectors.toList());
        return processJobSparesServiceInfo(records);
    }

    public Map<String, Object> getMonthlyJobServiceStats(int year, int month) {
        List<JobSpares> records = jobSparesRepository.findAll().stream()
                .filter(e -> e.getJobCloseDate().getYear() == year && e.getJobCloseDate().getMonthValue() == month)
                .collect(Collectors.toList());
        return processJobSparesServiceInfo(records);
    }

    public Map<String, Object> getYearlyJobServiceStats(int year) {
        List<JobSpares> records = jobSparesRepository.findAll().stream()
                .filter(e -> e.getJobCloseDate().getYear() == year)
                .collect(Collectors.toList());
        return processJobSparesServiceInfo(records);
    }

    public Map<String, Object> getJobServiceStatsByDateRange(LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();
        List<JobSpares> records = jobSparesRepository.findByJobCloseDateBetween(startDateTime, endDateTime);
        return processJobSparesServiceInfo(records);
    }
    // Service REPORT END

    // Spares REPORT START
    private Map<String, Object> processJobSparesInfo(List<JobSpares> records) {
            Map<String, Object> result = new HashMap<>();

            List<JobSpares> estimateJobSpares = records.stream().filter(job -> job.getEstimateObjId() != null)
                            .collect(Collectors.toList());
            List<JobSpares> invoiceJobSpares = records.stream().filter(job -> job.getInvoiceObjId() != null)
                            .collect(Collectors.toList());

            // Flatten all JobSparesInfo from all JobSpares
            List<JobSparesInfo> allEstimateSparesInfos = estimateJobSpares.stream()
                            .flatMap(job -> {
                                    List<JobSparesInfo> combined = new ArrayList<>();
                                    if (job.getJobSparesInfo() != null) combined.addAll(job.getJobSparesInfo());
                                //     if (job.getJobServiceInfo() != null)
                                //             combined.addAll(job.getJobServiceInfo());
                                    return combined.stream();
                            })
                            .collect(Collectors.toList());

            // 1. category -> sum(amount)
            Map<String, BigDecimal> categoryToAmountEstimate = allEstimateSparesInfos.stream()
                            .collect(Collectors.groupingBy(
                                            JobSparesInfo::getCategory,
                                            Collectors.mapping(
                                                            JobSparesInfo::getAmount,
                                                            Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

            // 2. category -> sum(qty)
            Map<String, BigDecimal> categoryToQtyEstimate = allEstimateSparesInfos.stream()
                            .collect(Collectors.groupingBy(
                                            JobSparesInfo::getCategory,
                                            Collectors.mapping(
                                                            JobSparesInfo::getQty,
                                                            Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

            // 3. sparesAndLabour -> sum(amount)
            Map<String, BigDecimal> sparesLabourToAmountEstimate = allEstimateSparesInfos.stream()
                            .collect(Collectors.groupingBy(
                                            JobSparesInfo::getSparesAndLabour,
                                            Collectors.mapping(
                                                            JobSparesInfo::getAmount,
                                                            Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

            // 4. sparesAndLabour -> sum(qty)
            Map<String, BigDecimal> sparesLabourToQtyEstimate = allEstimateSparesInfos.stream()
                            .collect(Collectors.groupingBy(
                                            JobSparesInfo::getSparesAndLabour,
                                            Collectors.mapping(
                                                            JobSparesInfo::getQty,
                                                            Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

            // Flatten all JobSparesInfo from all JobSpares
            List<JobSparesInfo> allInvoiceSparesInfos = invoiceJobSpares.stream()
                            .flatMap(job -> {
                                    List<JobSparesInfo> combined = new ArrayList<>();
                                    if (job.getJobSparesInfo() != null) combined.addAll(job.getJobSparesInfo());
                                //     if (job.getJobServiceInfo() != null)
                                //             combined.addAll(job.getJobServiceInfo());
                                    return combined.stream();
                            })
                            .collect(Collectors.toList());

            // 1. category -> sum(amount)
            Map<String, BigDecimal> categoryToAmountInvoice = allInvoiceSparesInfos.stream()
                            .collect(Collectors.groupingBy(
                                            JobSparesInfo::getCategory,
                                            Collectors.mapping(
                                                            JobSparesInfo::getGstAmount,
                                                            Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

            // 2. category -> sum(qty)
            Map<String, BigDecimal> categoryToQtyInvoice = allInvoiceSparesInfos.stream()
                            .collect(Collectors.groupingBy(
                                            JobSparesInfo::getCategory,
                                            Collectors.mapping(
                                                            JobSparesInfo::getQty,
                                                            Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

            // 3. sparesAndLabour -> sum(amount)
            Map<String, BigDecimal> sparesLabourToAmountInvoice = allInvoiceSparesInfos.stream()
                            .collect(Collectors.groupingBy(
                                            JobSparesInfo::getSparesAndLabour,
                                            Collectors.mapping(
                                                            JobSparesInfo::getGstAmount,
                                                            Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

            // 4. sparesAndLabour -> sum(qty)
            Map<String, BigDecimal> sparesLabourToQtyInvoice = allInvoiceSparesInfos.stream()
                            .collect(Collectors.groupingBy(
                                            JobSparesInfo::getSparesAndLabour,
                                            Collectors.mapping(
                                                            JobSparesInfo::getQty,
                                                            Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

            Map<String, BigDecimal> sortedCombinedCategoryToAmount = Utils.sortMapByValueDesc(Utils.mergeAndSum(
                            categoryToAmountInvoice,
                            categoryToAmountEstimate));
            result.put("categoryToAmount", sortedCombinedCategoryToAmount);

            Map<String, BigDecimal> sortedCombinedCategoryToQty = Utils.sortMapByValueDesc(Utils.mergeAndSum(
                            categoryToQtyInvoice,
                            categoryToQtyEstimate));
            result.put("categoryToQty", sortedCombinedCategoryToQty);

            Map<String, BigDecimal> sortedCombinedSparesLabourToAmount = Utils.sortMapByValueDesc(Utils.mergeAndSum(
                            sparesLabourToAmountInvoice,
                            sparesLabourToAmountEstimate));
            result.put("sparesLabourToAmount", sortedCombinedSparesLabourToAmount);

            Map<String, BigDecimal> sortedCombinedSparesLabourToQty = Utils.sortMapByValueDesc(Utils.mergeAndSum(
                            sparesLabourToQtyInvoice,
                            sparesLabourToQtyEstimate));
            result.put("sparesLabourToQty", sortedCombinedSparesLabourToQty);

            List<InfoWrapper> allWrappedInfos = new ArrayList<>();

            // Wrap estimate entries
            for (JobSpares job : estimateJobSpares) {
                    if (job.getJobSparesInfo() != null) {
                            for (JobSparesInfo info : job.getJobSparesInfo()) {
                                    allWrappedInfos.add(new InfoWrapper(
                                                    info.getCategory(),
                                                    info.getSparesAndLabour(),
                                                    Optional.ofNullable(info.getAmount()).orElse(BigDecimal.ZERO),
                                                    Optional.ofNullable(info.getQty()).orElse(BigDecimal.ZERO)));
                            }
                    }
            }

            // Wrap invoice entries (use getGstAmount)
            for (JobSpares job : invoiceJobSpares) {
                    if (job.getJobSparesInfo() != null) {
                            for (JobSparesInfo info : job.getJobSparesInfo()) {
                                    allWrappedInfos.add(new InfoWrapper(
                                                    info.getCategory(),
                                                    info.getSparesAndLabour(),
                                                    Optional.ofNullable(info.getGstAmount()).orElse(BigDecimal.ZERO),
                                                    Optional.ofNullable(info.getQty()).orElse(BigDecimal.ZERO)));
                            }
                    }
            }

            Map<String, List<Map<String, Object>>> maxQtyByCategory = new HashMap<>();
            Map<String, List<Map<String, Object>>> maxAmountByCategory = new HashMap<>();

            Map<String, Map<String, List<InfoWrapper>>> grouped = allWrappedInfos.stream()
                            .collect(Collectors.groupingBy(
                                            InfoWrapper::category,
                                            Collectors.groupingBy(InfoWrapper::sparesAndLabour)));

            for (var categoryEntry : grouped.entrySet()) {
                    String category = categoryEntry.getKey();
                    Map<String, List<InfoWrapper>> sparesMap = categoryEntry.getValue();

                    // Prepare lists to sort
                    List<Map<String, Object>> qtyList = new ArrayList<>();
                    List<Map<String, Object>> amountList = new ArrayList<>();

                    for (var sparesEntry : sparesMap.entrySet()) {
                            String spares = sparesEntry.getKey();
                            List<InfoWrapper> entries = sparesEntry.getValue();

                            BigDecimal totalAmount = entries.stream()
                                            .map(InfoWrapper::amount)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                            BigDecimal totalQty = entries.stream()
                                            .map(InfoWrapper::qty)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                            qtyList.add(Map.of(
                                            "sparesAndLabour", spares,
                                            "qty", totalQty));

                            amountList.add(Map.of(
                                            "sparesAndLabour", spares,
                                            "amount", totalAmount));
                    }

                    // Sort descending
                    qtyList.sort((a, b) -> ((BigDecimal) b.get("qty")).compareTo((BigDecimal) a.get("qty")));
                    amountList.sort((a, b) -> ((BigDecimal) b.get("amount")).compareTo((BigDecimal) a.get("amount")));

                    maxQtyByCategory.put(category, qtyList);
                    maxAmountByCategory.put(category, amountList);
            }

            result.put("maxQtyByCategory", maxQtyByCategory);
            result.put("maxAmountByCategory", maxAmountByCategory);

            return result;
    }

    public Map<String, Object> getDailyJobSparesStats(LocalDate date) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = start.plusDays(1);
            List<JobSpares> records = jobSparesRepository.findByJobCloseDateBetween(start, end);
            return processJobSparesInfo(records);
    }

    public Map<String, Object> getWeeklyJobSparesStats(int year, int week) {
            List<JobSpares> records = jobSparesRepository.findAll().stream().filter(e -> {
                    LocalDateTime dateTime = e.getJobCloseDate();
                    return dateTime.getYear() == year && dateTime.get(WeekFields.ISO.weekOfYear()) == week;
            }).collect(Collectors.toList());
            return processJobSparesInfo(records);
    }

    public Map<String, Object> getMonthlyJobSparesStats(int year, int month) {
            List<JobSpares> records = jobSparesRepository.findAll().stream()
                            .filter(e -> e.getJobCloseDate().getYear() == year
                                            && e.getJobCloseDate().getMonthValue() == month)
                            .collect(Collectors.toList());
            return processJobSparesInfo(records);
    }

    public Map<String, Object> getYearlyJobSparesStats(int year) {
            List<JobSpares> records = jobSparesRepository.findAll().stream()
                            .filter(e -> e.getJobCloseDate().getYear() == year)
                            .collect(Collectors.toList());
            return processJobSparesInfo(records);
    }

    public Map<String, Object> getJobSparesStatsByDateRange(LocalDate start, LocalDate end) {
            LocalDateTime startDateTime = start.atStartOfDay();
            LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();
            List<JobSpares> records = jobSparesRepository.findByJobCloseDateBetween(startDateTime, endDateTime);
            return processJobSparesInfo(records);
    }
    // Spares REPORT END

}
