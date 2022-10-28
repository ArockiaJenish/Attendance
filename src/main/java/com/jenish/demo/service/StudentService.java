package com.jenish.demo.service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.jenish.demo.dto.LogDto;
import com.jenish.demo.dto.StudentDTO;
import com.jenish.demo.dto.TotalTime;
import com.jenish.demo.model.Attendance;
import com.jenish.demo.model.Student;
import com.jenish.demo.model.TimeLogs;
import com.jenish.demo.repository.AttendanceRepository;
import com.jenish.demo.repository.StudentRepository;
import com.jenish.demo.repository.TimeLogRepository;

@Service
public class StudentService implements StuServiceInterface {

	@Autowired
	StudentRepository stuRepo;

	@Autowired
	TimeLogRepository timeRepo;

	@Autowired
	AttendanceRepository atnRepo;

	// -----------------Register Student -----------------
	public ResponseEntity<String> registerStudent(Student student) {

		Student exStu = stuRepo.findByEmail(student.getEmail());
		if (exStu == null) {
			if (student.getName() != null && student.getEmail() != null && student.getPassword() != null) {
				stuRepo.save(student);
				return new ResponseEntity<>("Registered Successfully", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Give required detials", HttpStatus.BAD_REQUEST);
			}
		} else {
			return new ResponseEntity<>("This Student is already Exist", HttpStatus.ALREADY_REPORTED);
		}

	}

	// ------------Login Student-------------
	public StudentDTO login(Student stu) {
		TimeCalculation tc = new TimeCalculation();
		Student exStu = null;
		if (stu.getEmail() != null && stu.getPassword() != null)
			exStu = stuRepo.login(stu.getEmail(), stu.getPassword());
		if (exStu != null) {

			StudentDTO stuDto = new StudentDTO();
			stuDto.setId(exStu.getId());
			stuDto.setEmail(exStu.getEmail());
			stuDto.setName(exStu.getName());

			// ----------For check 'isCheckIn' and get existing login time----------
			TimeLogs l = timeRepo.findLast(getCurrentDate(), exStu.getId());
			Attendance at = atnRepo.stuByDateAndId(getCurrentDate(), exStu.getId());
			/*
			 * TimeLogs l = null; if (!logs.isEmpty()) l = logs.get(0);
			 */
			if (l != null) {
				if (l.getCheckOut() == null) {
					stuDto.setCheckIn(true);
					Time running = tc.addTimes(at.getWorkedTime(), tc.diffOfTime(l.getCheckIn(), getCurrentTime()));
					stuDto.setLoginTime(running);
				} else {
					stuDto.setCheckIn(false);
					stuDto.setLoginTime(at.getWorkedTime());
				}
			} else
				stuDto.setCheckIn(false);

			return stuDto;
		}
		return new StudentDTO();
	}

	// -------------For check in student----------
	public String checkIn(int id) {
		Time totTime = null;
		try {
			stuRepo.findById(id).get();
			Attendance attn = atnRepo.stuByDateAndId(getCurrentDate(), id);// -----------------

			TimeLogs log = timeRepo.findLast(getCurrentDate(), id);// --------------------
			/*
			 * if (!logs.isEmpty()) { TimeLogs log = logs.get(0);
			 * 
			 * }
			 */
			if (log.getCheckOut() == null)
				return "You are already checked in!";

			if (attn == null) {
				Attendance att = new Attendance();
				att.setStuId(id);
				att.setDate(getCurrentDate());// --------------------
				att.setStatus("P");
				att.setLogIn(getCurrentTime());
				att.setWorkedTime(getEmptyTime());
				updateCheckin(att.getLogIn(), id);
				totTime = atnRepo.save(att).getWorkedTime();
			} else {
				updateCheckin(getCurrentTime(), id);
				totTime = attn.getWorkedTime();
			}

		} catch (NoSuchElementException nse) {
			return "No Student available";
		}
		return totTime.toString();
	}

	private Time updateCheckin(Time checkInTime, int id) {

		TimeLogs log = new TimeLogs();
		log.setCheckIn(checkInTime);
		log.setCheckInTime(getEmptyTime());
		log.setDate(getCurrentDate());
		log.setStuId(id);

		return timeRepo.save(log).getCheckIn();
	}

	//for get empty time-------
	private Time getEmptyTime() {
		return Time.valueOf("00:00:00");
	}

	/*
	 * private Date getDummyDate() {// ---------Just for checking------------ return
	 * Date.valueOf("2022-10-19"); }
	 */

	//for get current date------
	private Date getCurrentDate() {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime now = LocalDateTime.now();
		System.out.println(dtf.format(now));
		return Date.valueOf(dtf.format(now));
	}

	//for get current time------
	private Time getCurrentTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println(dtf.format(now));
		return Time.valueOf(dtf.format(now));
	}

	// -----------For checkOut student-------------
	public String checkOut(int id) {
		String checkOutTime = null;
		try {
			stuRepo.findById(id);
			Attendance att = atnRepo.stuByDateAndId(getCurrentDate(), id);
			if (att != null) {
				TimeLogs t = timeRepo.findLast(getCurrentDate(), id);
				//TimeLogs t = tl.get(0);
				if (t.getCheckOut() == null) {
					checkOutTime = updateLogOut(id, att, t);
				} else {
					checkOutTime = "You have already checked out!";
				}
			} else
				checkOutTime = "You have not checked in yet..";
		} catch (NoSuchElementException nse) {
			return "No Student available";
		}
		return checkOutTime;
	}

	private String updateLogOut(int id, Attendance att, TimeLogs t) {

		TimeCalculation tc = new TimeCalculation();

		Time workedTime = null;
		Time checkinTime = null;
		Time currentTime = getCurrentTime();

		String totDayTime = null;
		if (t.getCheckOut() == null) {
			checkinTime = tc.diffOfTime(t.getCheckIn(), currentTime);
			t.setCheckOut(currentTime);
			t.setCheckInTime(checkinTime);
			timeRepo.save(t).getCheckOut();
			workedTime = tc.addTimes(checkinTime, att.getWorkedTime());
			att.setLogOut(getCurrentTime());
			att.setWorkedTime(workedTime);
			totDayTime = atnRepo.save(att).getWorkedTime().toString();
		} else {
			totDayTime = "You have already checked out";
		}
		// }

		return totDayTime;
	}

	// -------------For Total time logs------------
	public List<LogDto> getTimeLogs(int id) {
		TimeCalculation tc = new TimeCalculation();

		List<TimeLogs> logs = timeRepo.findByStuId(id);
		List<LogDto> dto = new ArrayList<LogDto>();

		logs.forEach(t -> {
			LogDto l = new LogDto();
			l.setCheckIn(t.getCheckIn());
			l.setCheckOut(t.getCheckOut());
			l.setDate(t.getDate());
			if (t.getCheckIn() != null && t.getCheckOut() != null)
				l.setTotTime(tc.diffOfTime(t.getCheckIn(), t.getCheckOut()));// getting difference
			dto.add(l);
		});

		return dto;
	}

	// ---------------Calculating group by date logs----------------
	public List<TotalTime> getTotLogTime(int id) {
		TimeCalculation tc = new TimeCalculation();

		List<TotalTime> attnLogs = new ArrayList<TotalTime>();// Empty list of object to store the dto.
		List<Attendance> atns = atnRepo.findByStuId(id);

		for (Attendance a : atns) {
			Time totTime = getEmptyTime();
			TotalTime tt = new TotalTime();
			List<TimeLogs> logs = timeRepo.findByDateAndId(a.getDate(), id);
			for (TimeLogs l : logs) {
				if (l.getCheckIn() != null && l.getCheckOut() != null)
					totTime = tc.addTimes(totTime, l.getCheckInTime());
			}
			tt.setTimeLog(logs);
			tt.setDate(a.getDate());
			tt.setLoginTime(a.getLogIn());
			tt.setLogoutTime(a.getLogOut());
			tt.setStatus(a.getStatus());
			tt.setWorkedTime(totTime);
			if (a.getLogOut() != null && a.getLogOut().toString().contains(":"))
				tt.setLoggedInTime(tc.diffOfTime(a.getLogIn(), a.getLogOut()));
			else
				tt.setLoggedInTime(getEmptyTime());
			/*
			 * overAllTime = tc.addTimes(a.getTotLoginTime(), overAllTime);
			 * tt.setOverAllTime(overAllTime);
			 */
			attnLogs.add(tt);
		}

		return attnLogs;
	}

	// ------This is for updating the table------------
	// @Scheduled(cron = "0 58 23 * * ?")
	public String updateRecord() {
		List<Student> stdns = stuRepo.findAll();
		//String zeroTime = "00:00:00";
		for (Student s : stdns) {
			TimeLogs l = timeRepo.findLast(getCurrentDate(), s.getId());// To get last one
			
			if (l != null) {
				if (l.getCheckOut() == null) {
					l.setCheckOut(getEmptyTime());
					l.setCheckInTime(getEmptyTime());
					timeRepo.save(l);
					updateAttendance(s.getId(), l.getDate(), "A");// Set as absent
				} else {
					
					System.out.println("No need to update already updated");
				}
			} else {
				l = new TimeLogs();
				l.setDate(getCurrentDate());
				l.setStuId(s.getId());
				l.setCheckIn(getEmptyTime());
				l.setCheckOut(getEmptyTime());
				l.setCheckInTime(getEmptyTime());
				timeRepo.save(l);
				updateAttendance(s.getId(), getCurrentDate(), "A");// Set as absent
			}
		}

		return "Updated";
	}

	private void updateAttendance(int id, Date logDate, String status) {// Mark as absent

		//String emptyTime = "00:00:00";
		Attendance at = atnRepo.stuByDateAndId(logDate, id);

		if (at != null) {
			System.out.println("Status = " + at.getStatus());
			at.setStatus(status);
			at.setLogOut(getEmptyTime());
			at.setWorkedTime(getEmptyTime());
			atnRepo.save(at);
		} else {
			Attendance a = new Attendance();
			a.setDate(logDate);
			a.setStatus(status);
			a.setStuId(id);
			a.setLogIn(getEmptyTime());
			a.setLogOut(getEmptyTime());
			a.setWorkedTime(getEmptyTime());
			atnRepo.save(a);
		}

	}
	/*
	 * private void updateAttendance(int id, Date logDate) { // calculate total time
	 * for a day
	 * 
	 * String totTime = "00:00:00"; TimeCalculation tc = new TimeCalculation();
	 * 
	 * List<TimeLogs> logs = timeRepo.findByDateAndId(logDate, id);
	 * 
	 * Attendance a = atnRepo.stuByDateAndId(logDate, id);
	 * 
	 * for (TimeLogs log : logs) { totTime = tc.addTimes(log.getCheckInTime(),
	 * totTime); }
	 * 
	 * a.setTotLoginTime(totTime); atnRepo.save(a); }
	 */
}
