package com.webapp.models;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.validator.constraints.*;

@Entity
@Table(name="USERS")
public class User implements Serializable {
	
	/* Instance Vars */
	@Id 
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	@Column(name="EMAIL", nullable=false)
	@NotEmpty(message="Email Address Required.")
	@Email(message="Invalid Email Address.")
	private String email;

	@Column(name="PASSWORD", nullable=false)
	@NotEmpty(message="Password Required.")
	@Size(min=8, message="Password must contain at least 8 characters.")
	private String password;
	
	@Column(name="SALT")
	private String salt;

	
	/* Getters */
	public long getId() { return id; }
	public String getEmail() { return email; }
	public String getPassword() { return password; }
	public String getSalt() { return salt; }

	/* Setters */
	public void setId(long id) { this.id = id; }
	public void setEmail(String e) { this.email = e; }
	public void setPassword(String p) { this.password = p; }
	public void setSalt(String s) { this.salt = s; }
}
