package com.sas.carwash.model;

import java.math.BigDecimal;

public record InfoWrapper(String category, String sparesAndLabour, BigDecimal amount, BigDecimal qty) {
}
