package aiss.model;

import java.time.Duration;
import java.util.Date;

public class Film {

	private String id;
	private String title;
	private String genre;
	private Date premiere;
	private Duration runtime;
	private Double score;
	private String language;

	public Film() {
	}

	public Film(String title, String genre, Date premiere, Duration runtime, Double score, String language) {
		super();
		this.title = title;
		this.genre = genre;
		this.premiere = premiere;
		this.runtime = runtime;
		this.score = score;
		this.language = language;
	}
	
	public Film(String id, String title, String genre, Date premiere, Duration runtime, Double score, String language) {
		super();
		this.id=id;
		this.title = title;
		this.genre = genre;
		this.premiere = premiere;
		this.runtime = runtime;
		this.score = score;
		this.language = language;
	}
	
	public Film(String s) {
	
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public Date getPremiere() {
		return premiere;
	}

	public void setPremiere(Date premiere) {
		this.premiere = premiere;
	}

	public Duration getRuntime() {
		return runtime;
	}

	public void setRuntime(Duration runtime) {
		this.runtime = runtime;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}


}
