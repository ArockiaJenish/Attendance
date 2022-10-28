package com.jenish.demo.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class LogDto {
	
	private String checkIn;
	private String checkOut;
	private Date date;
	private String totTime;
	
}
