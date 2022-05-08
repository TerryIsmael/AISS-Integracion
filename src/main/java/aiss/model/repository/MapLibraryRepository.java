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
	public void addLibrary(Library p) {
		String id = "p" + index++;	
		p.setId(id);
		libraryMap.put(id,p);
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
	public void updateLibrary(Library p) {
		libraryMap.put(p.getId(),p);
	}

	@Override
	public void deleteLibrary(String id) {	
		libraryMap.remove(id);
	}
	

	@Override
	public void addFilm(String LibraryId, String songId) {
		Library playlist = getLibrary(LibraryId);
		playlist.addFilm(filmMap.get(songId));
	}

	@Override
	public Collection<Film> getAll(String LibraryId) {
		return getLibrary(LibraryId).getFilms();
	}

	@Override
	public void removeFilm(String LibraryId, String songId) {
		getLibrary(LibraryId).deleteFilm(songId);
	}

	
	
	@Override
	public void addFilm(Film s) {
		String id = "s" + index++;
		s.setId(id);
		filmMap.put(id, s);
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
	public void updateFilm(Film s) {
		Film song = filmMap.get(s.getId());
		song.setTitle(s.getTitle());
		song.setGenre(s.getGenre());
		song.setPremiere(s.getPremiere());
		song.setRuntime(s.getRuntime());
		song.setScore(s.getScore());
		song.setLanguage(s.getLanguage());
	}

	@Override
	public void deleteFilm(String songId) {
		filmMap.remove(songId);
	}
	
}
