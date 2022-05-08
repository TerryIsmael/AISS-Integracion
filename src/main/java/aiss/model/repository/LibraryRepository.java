package aiss.model.repository;

import java.util.Collection;

import aiss.model.Library;
import aiss.model.Film;

public interface LibraryRepository {
	
	
	// Songs
	public void addFilm(Film f);
	public Collection<Film> getAllFilms();
	public Film getFilm(String filmId);
	public void updateFilm(Film f);
	public void deleteFilm(String filmId);
	
	// Playlists
	public void addLibrary(Library l);
	public Collection<Library> getAllLibraries();
	public Library getLibrary(String libraryId);
	public void updateLibrary(Library l);
	public void deleteLibrary(String libraryId);
	public Collection<Film> getAll(String libraryId);
	public void addFilm(String libraryId, String filmId);
	public void removeFilm(String libraryId, String filmId); 

	
	
	
	

}
