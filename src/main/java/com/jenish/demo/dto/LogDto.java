package com.jenish.demo.dto;

import java.sql.Date;
import java.sql.Time;

import lombok.Data;

@Data
public class LogDto {
	
	private Time checkIn;
	private Time checkOut;
	private Date date;
	private Time totTime;
	
}
