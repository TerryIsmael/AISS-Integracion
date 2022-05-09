package aiss.model;

import java.util.ArrayList;
import java.util.List;

public class Library {

	private String id;
	private String name;
	private String description;
	private List<Film> films;
	
	public Library(String name) {
		this.name = name;
	}
	
	protected void setFilms(List<Film> s) {
		films = s;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<Film> getFilms() {
		return films;
	}
	
	public Film getFilm(String id) {
		if (films==null)
			return null;
		
		Film film =null;
		for(Film f: films)
			if (f.getId().equals(id))
			{
				film=f;
				break;
			}
		
		return film;
	}
	
	public void addFilm(Film f) {
		if (films==null)
			films = new ArrayList<Film>();
		films.add(f);
	}
	
	public void deleteFilm(Film f) {
		films.remove(f);
	}
	
	public void deleteFilm(String id) {
		Film f = getFilm(id);
		if (f!=null)
			films.remove(f);
	}
	

}
