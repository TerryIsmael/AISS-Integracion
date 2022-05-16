package aiss.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Film {

	private String id;
	private String title;
	private String genre;
	private String premiere;
	private Integer runtime;
	private Double score;
	private List<String> language;
	private String username;
	
	public Film() {
		
	}
	
	public Film(String title, String genre, String premiere, Integer runtime, Double score, List<String> language) {
		super();
		this.title = title;
		this.genre = genre;
		this.premiere = premiere;
		this.runtime = runtime;
		this.score = score;
		this.language = language;
	}
	
	public Film(String id, String title, String genre, String premiere, Integer runtime, Double score, List<String> language) {
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
		String[] trozos = s.split(";");
		List<String> idiomas=new ArrayList<>();
		String[] idiomas_arr=trozos[5].split("/");
		for(String idioma:idiomas_arr) {
			idiomas.add(idioma);
		}
		this.title = trozos[0].trim();
		this.genre = trozos[1].trim();
		//this.premiere = LocalDate.parse(trozos[2].trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		this.premiere = trozos[2].trim().replace("/", "-");
		this.runtime = Integer.valueOf(trozos[3].trim());
		this.score = Double.valueOf(trozos[4].trim());
		this.language =   idiomas;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username=username;
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

	public String getPremiere() {
		return premiere;
	}

	public void setPremiere(String premiere) {
		this.premiere = premiere;
	}
	
	public LocalDate parsePremiere() {
		return LocalDate.parse(this.premiere, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
	}

	public Integer getRuntime() {
		return runtime;
	}

	public void setRuntime(Integer runtime) {
		this.runtime = runtime;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public List<String> getLanguage() {
		return language;
	}

	public void setLanguage(List<String> language) {
		this.language = language;
	}

	public static Film parse(String linea) {
		return new Film(linea);
	}
}
