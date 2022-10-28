package com.jenish.demo.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jenish.demo.model.TimeLogs;

@Repository
public interface TimeLogRepository extends JpaRepository<TimeLogs, Integer> {

	String byDateAndId = "select t from TimeLogs t where t.date = :date and t.stuId = :id";
	@Query(byDateAndId)
	public List<TimeLogs> findByDateAndId(@Param("date") Date date, @Param("id") int id);
	
	String orderByTime = "select * from time_logs where stu_id = :id and date = :date order by id desc limit 1";
	@Query(nativeQuery = true, value=orderByTime)
	public TimeLogs findLast(@Param("date") Date date, @Param("id") int id);
	
	String byStuId = "select t from TimeLogs t where t.stuId = :id";
	@Query(byStuId)
	public List<TimeLogs> findByStuId(@Param("id") int id);
	
	String groupByDate = "select * from time_logs where stu_id = :id group by date";
	@Query(nativeQuery=true, value=groupByDate)
	public List<TimeLogs> findGroupByDate(@Param("id") int id);
	
}
