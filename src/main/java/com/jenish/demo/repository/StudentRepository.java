package com.jenish.demo.repository;

import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jenish.demo.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer>{
	
	String emailQuery = "select s from Student s where s.email = :email";
	String loginQuery = "select s from Student s where s.email = :email and s.password = :pass";
	
	
	@Query(emailQuery)
	public Student findByEmail(@Param("email") String email);
	
	@Query(loginQuery)
	public Student login(@Param("email") String email, @Param("pass") String pass);
	
	
	
}
