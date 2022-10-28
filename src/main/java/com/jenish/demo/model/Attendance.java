package com.jenish.demo.model;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Attendance {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String status;
	private String logIn;
	private String logOut;
	private String workedTime;
	private Date date;
	private int stuId;
}
