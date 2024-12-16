package com.legal.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity
@Table(name="users")
public class Users {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@NotBlank(message="username should not be blank")
	@Size(min=3,max=13,message="username should be between 3 to 13 characters")
	private String Name;

	@Column(unique=true)
	private String Phone;
	
	@Column(unique=true)
	@NotBlank(message="Email should not be blank")
	@Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",message ="Please enter valid email id")
	private String Email;
	
	private boolean ismale;
	
	private String Password;
	
	@AssertTrue(message="must agree terms and conditions")
	private boolean agreement;
	
	private String role;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Users() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public boolean isIsmale() {
		return ismale;
	}

	public void setIsmale(boolean ismale) {
		this.ismale = ismale;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public boolean isAgreement() {
		return agreement;
	}

	public void setAgreement(boolean agreement) {
		this.agreement = agreement;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "Users [id=" + id + ", Name=" + Name + ", Phone=" + Phone + ", Email=" + Email + ", ismale=" + ismale
				+ ", Password=" + Password + ", agreement=" + agreement + ", role=" + role + "]";
	}
	
	
	

	
}
