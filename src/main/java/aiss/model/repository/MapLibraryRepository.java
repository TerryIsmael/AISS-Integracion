package aiss.model.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aiss.model.Film;
import aiss.model.Library;


public class MapLibraryRepository implements LibraryRepository{

	Map<String, Library> libraryMap;
	Map<String, Film> filmMap;
	private static MapLibraryRepository instance=null;
	private int index=0;			
	
	
	public static MapLibraryRepository getInstance() {
		
		if (instance==null) {
			instance = new MapLibraryRepository();
			instance.init();
		}
		
		return instance;
	}
	
	
	public void init() {
		
		libraryMap = new HashMap<String,Library>();
		filmMap = new HashMap<String,Film>();
		
		List<Film> data= Data.lectura();
		for (Film dato:data)
			addFilm(dato);
		
	}
	
	@Override
	public void addLibrary(Library lib) {
		String id = "l" + index++;	
		lib.setId(id);
		libraryMap.put(id,lib);
	}
	
	@Override
	public Collection<Library> getAllLibraries() {
			return libraryMap.values();
	}

	@Override
	public Library getLibrary(String id) {
		return libraryMap.get(id);
	}
	
	@Override
	public void updateLibrary(Library lib) {
		libraryMap.put(lib.getId(),lib);
	}

	@Override
	public void deleteLibrary(String id) {	
		libraryMap.remove(id);
	}
	

	@Override
	public void addFilm(String LibraryId, String filmId) {
		Library library = getLibrary(LibraryId);
		library.addFilm(filmMap.get(filmId));
	}

	@Override
	public Collection<Film> getAll(String LibraryId) {
		return getLibrary(LibraryId).getFilms();
	}

	@Override
	public void removeFilm(String LibraryId, String filmId) {
		getLibrary(LibraryId).deleteFilm(filmId);
	}

	
	
	@Override
	public void addFilm(Film film) {
		String id = "f" + index++;
		film.setId(id);
		filmMap.put(id, film);
	}
	
	@Override
	public Collection<Film> getAllFilms() {
			return filmMap.values();
	}

	@Override
	public Film getFilm(String filmId) {
		return filmMap.get(filmId);
	}

	@Override
	public void updateFilm(Film oldFilm) {
		Film film = filmMap.get(oldFilm.getId());
		film.setTitle(oldFilm.getTitle());
		film.setGenre(oldFilm.getGenre());
		film.setPremiere(oldFilm.getPremiere());
		film.setRuntime(oldFilm.getRuntime());
		film.setScore(oldFilm.getScore());
		film.setLanguage(oldFilm.getLanguage());
	}

	@Override
	public void deleteFilm(String filmId) {
		filmMap.remove(filmId);
	}
	
}
