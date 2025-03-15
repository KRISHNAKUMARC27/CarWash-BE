package com.sas.carwash.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseCategory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private String category;
}
