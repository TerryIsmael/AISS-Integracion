package aiss.model.repository;

import java.util.Collection;
import java.util.List;

import aiss.model.Film;
import aiss.model.Library;
import aiss.model.User;

public interface LibraryRepository {
	
	
	public void addFilm(Film film);
	public void addFilms(List<Film> films);
	public Collection<Film> getAllFilms();
	public Film getFilm(String filmId);
	public void updateFilm(Film oldFilm);
	public void deleteFilm(String filmId);
	

	public void addLibrary(Library lib);
	public void addLibraries(List<Library> libs);
	public Collection<Library> getAllLibraries();
	public Library getLibrary(String libraryId);
	public void updateLibrary(Library lib);
	public void deleteLibrary(String libraryId);
	public Collection<Film> getAll(String libraryId);
	public void addFilm(String libraryId, String filmId);
	public void removeFilm(String libraryId, String filmId);
	
	public void addUser(User user);
	public Collection<User> getAllUsers();
	void updateUser(User user);
	void deleteUser(User user); 


	
	
	
	

}
