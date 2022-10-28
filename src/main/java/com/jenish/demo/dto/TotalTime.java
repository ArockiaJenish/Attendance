package com.jenish.demo.dto;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import com.jenish.demo.model.TimeLogs;

import lombok.Data;

@Data
public class TotalTime {

	private String status;
	private Time loginTime;
	private Time logoutTime;
	private Date date;
	private Time workedTime;
	private Time loggedInTime;
	//private String overAllTime;
	private List<TimeLogs> timeLog;
	
}
