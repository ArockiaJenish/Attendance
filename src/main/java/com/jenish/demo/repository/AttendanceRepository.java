package com.jenish.demo.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jenish.demo.model.Attendance;
	
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer>{
	String dateAndId = "select a from Attendance a where a.date = :date and a.stuId = :id";
	@Query(dateAndId)
	public Attendance stuByDateAndId(@Param("date") Date date, @Param("id") int id);
	
	String byStuId = "select a from Attendance a where a.stuId = :id";
	@Query(byStuId)
	public List<Attendance> findByStuId(@Param("id") int id);
}
