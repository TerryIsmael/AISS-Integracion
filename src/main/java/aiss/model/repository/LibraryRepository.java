package aiss.model.repository;

import java.util.Collection;

import aiss.model.Library;
import aiss.model.Film;

public interface LibraryRepository {
	
	
	public void addFilm(Film s);
	public Collection<Film> getAllFilms();
	public Film getFilm(String filmId);
	public void updateFilm(Film s);
	public void deleteFilm(String filmId);
	

	public void addLibrary(Library l);
	public Collection<Library> getAllLibraries();
	public Library getLibrary(String libraryId);
	public void updateLibrary(Library l);
	public void deleteLibrary(String libraryId);
	public Collection<Film> getAll(String libraryId);
	public void addFilm(String libraryId, String filmId);
	public void removeFilm(String libraryId, String filmId); 


	
	
	
	

}
