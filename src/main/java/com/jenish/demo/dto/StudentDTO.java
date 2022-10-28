package com.jenish.demo.dto;

import java.sql.Time;

import lombok.Data;

@Data
public class StudentDTO {
	
	private int id;
	private String name;
	private String email;
	private boolean isCheckIn;
	private Time loginTime;

}
