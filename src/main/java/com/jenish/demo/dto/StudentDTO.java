package com.jenish.demo.dto;

import lombok.Data;

@Data
public class StudentDTO {
	
	private int id;
	private String name;
	private String email;
	private boolean isCheckIn;
	private String loginTime;

}
