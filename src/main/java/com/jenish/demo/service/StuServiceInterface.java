package com.jenish.demo.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.jenish.demo.dto.LogDto;
import com.jenish.demo.dto.StudentDTO;
import com.jenish.demo.dto.TotalTime;
import com.jenish.demo.model.Student;

public interface StuServiceInterface {
	
	public ResponseEntity<String> registerStudent(Student student);
	public StudentDTO login(Student stu);
	public String checkIn(int id);
	public String checkOut(int id);
	public List<LogDto> getTimeLogs(int id);
	public List<TotalTime> getTotLogTime(int id);
	
}
