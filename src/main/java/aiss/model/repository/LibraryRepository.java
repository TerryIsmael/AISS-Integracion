package aiss.model.repository;

import java.util.Collection;

import aiss.model.Library;
import aiss.model.Film;

public interface LibraryRepository {
	
	
	// Songs
	public void addFilm(Film s);
	public Collection<Film> getAllFilms();
	public Film getFilm(String filmId);
	public void updateFilm(Film s);
	public void deleteFilm(String filmId);
	
	// Playlists
	public void addLibrary(Library p);
	public Collection<Library> getAllLibraries();
	public Library getLibrary(String playlistId);
	public void updateLibrary(Library p);
	public void deleteLibrary(String playlistId);
	public Collection<Film> getAll(String playlistId);
	public void addFilm(String playlistId, String songId);
	public void removeFilm(String playlistId, String songId); 

	
	
	
	

}
