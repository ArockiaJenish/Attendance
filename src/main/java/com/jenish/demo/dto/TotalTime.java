package com.jenish.demo.dto;

import java.sql.Date;
import java.util.List;

import com.jenish.demo.model.TimeLogs;

import lombok.Data;

@Data
public class TotalTime {

	private String status;
	private String loginTime;
	private String logoutTime;
	private Date date;
	private String workedTime;
	private String loggedInTime;
	//private String overAllTime;
	private List<TimeLogs> timeLog;
	
}
