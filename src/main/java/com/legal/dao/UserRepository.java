package com.legal.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.legal.entites.Users;

@Repository
public interface UserRepository extends JpaRepository<Users,Integer> {
	
	@Query("select u from Users u where u.Email = :email")
	public Users GetUserByUserName(@Param("email") String email);

}
