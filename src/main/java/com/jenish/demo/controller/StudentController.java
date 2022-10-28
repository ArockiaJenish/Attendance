package com.jenish.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jenish.demo.dto.LogDto;
import com.jenish.demo.dto.StudentDTO;
import com.jenish.demo.dto.TotalTime;
import com.jenish.demo.model.Student;
import com.jenish.demo.service.EmailAndPDF;
import com.jenish.demo.service.StudentService;

@RestController
@RequestMapping("/student")
@CrossOrigin
public class StudentController {

	@Autowired
	StudentService stuServ;
	
	@Autowired
	EmailAndPDF emlServ;
	
	//----------For Register Student--------
	@RequestMapping(method = RequestMethod.POST, value = "/registerStudent")
	public ResponseEntity<String> registerStudent(@RequestBody Student student) {
		return stuServ.registerStudent(student);
	}

	//-----------For login Student---------
	@RequestMapping(method = RequestMethod.POST, value = "/login")
	public StudentDTO login(@RequestBody Student stu) {
		return stuServ.login(stu);
	}
	
	//-----------For check-in student-------
	@RequestMapping(method=RequestMethod.GET, value="/checkIn/{id}")
	public String checkIn(@PathVariable("id") int id) {
		return stuServ.checkIn(id);
	}
	
	//-------------For Checkout student----------
	@RequestMapping(method=RequestMethod.GET, value="/checkOut/{id}")
	public String checkOut(@PathVariable("id") int id) {
		return stuServ.checkOut(id);
	}
	
	//-------------For Total time logs------------
	@RequestMapping(method=RequestMethod.GET, value="/getTimeLogs/{id}")
	public List<LogDto> getTimeLogs(@PathVariable("id") int id) {
		return stuServ.getTimeLogs(id);
	}
	
	//---------------Calculating group by date logs----------------
	@RequestMapping(method=RequestMethod.GET, value="/getAttendanceDetails/{id}")
	public List<TotalTime> getTotLogTime(@PathVariable("id") int id){
		return stuServ.getTotLogTime(id);
	}
	
	//----------------For dummy Checking for update status---------------
	@RequestMapping(method=RequestMethod.GET, value="/forTest")
	public String checking() {
		return stuServ.updateRecord();
	}
	
	//-----------For send checkin details through email--------------
	@RequestMapping(method=RequestMethod.GET, value="/sendMail")
	public String sendCheckinDetails() {
		return emlServ.generateAndSend();
	}
}
