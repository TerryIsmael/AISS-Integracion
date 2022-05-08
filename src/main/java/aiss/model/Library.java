package aiss.model;

import java.util.ArrayList;
import java.util.List;

public class Library {

	private String id;
	private String name;
	private String description;
	private List<Film> songs;
	
	public Library() {}
	
	public Library(String name) {
		this.name = name;
	}
	
	protected void setSongs(List<Film> s) {
		songs = s;
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
	
	public List<Film> getSongs() {
		return songs;
	}
	
	public Film getSong(String id) {
		if (songs==null)
			return null;
		
		Film song =null;
		for(Film s: songs)
			if (s.getId().equals(id))
			{
				song=s;
				break;
			}
		
		return song;
	}
	
	public void addSong(Film s) {
		if (songs==null)
			songs = new ArrayList<Film>();
		songs.add(s);
	}
	
	public void deleteSong(Film s) {
		songs.remove(s);
	}
	
	public void deleteSong(String id) {
		Film s = getSong(id);
		if (s!=null)
			songs.remove(s);
	}

}
