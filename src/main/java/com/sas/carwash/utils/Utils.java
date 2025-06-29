package com.sas.carwash.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {

    public static Map<String, BigDecimal> sortMapByValueDesc(Map<String, BigDecimal> inputMap) {
        return inputMap.entrySet()
            .stream()
            .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1, // merge function, not needed here
                LinkedHashMap::new
            ));
    }

    public static Map<String, BigDecimal> mergeAndSum(Map<String, BigDecimal> map1, Map<String, BigDecimal> map2) {
        Map<String, BigDecimal> result = new HashMap<>(map1);
        map2.forEach((key, value) -> 
            result.merge(key, value, BigDecimal::add)
        );
        return result;
    }
    
}
