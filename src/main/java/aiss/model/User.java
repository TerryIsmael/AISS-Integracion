package aiss.model;

import java.util.Optional;

import aiss.model.repository.LibraryRepository;

public class User {
	private String name;
	private String password;
	private String token;
	
	public User(String name, String password) {
		this.name = name;
		this.password = password;
		
	}
	
	public User() {
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getName() {
		return name;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public static Optional<User> getNameFromToken(String token, LibraryRepository repository) {
		return repository.getAllUsers().stream().filter(x->x.getToken().equals(token)).findFirst();
	}
	
}
