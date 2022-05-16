package aiss.model.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aiss.model.Film;
import aiss.model.Library;
import aiss.model.TokenGen;
import aiss.model.User;

public class MapLibraryRepository implements LibraryRepository{

	Map<String, Library> libraryMap;
	Map<String, Film> filmMap;
	Map<String, User> userMap;
	private static MapLibraryRepository instance=null;
	private int indexF=0;
	private int indexL=0;
	
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
		userMap = new HashMap<String, User>();
		addFilms(Data.lectura_films());
		addLibraries(Data.lectura_libraries());
		for (int i=0;i<10;i++) {
			for(int j=0;j<20;j++) {
				addFilm("l"+i,"f"+((20*i)+j));
			}
			
		}
		addUser(Data.lectura_users());
	}
	@Override
	public void addUser(User user) {
		String token=TokenGen.RandomToken();
		user.setToken(token);
		userMap.put(token, user);
	}
	
	@Override
	public Collection<User> getAllUsers() {
		return userMap.values();
	}
	
	@Override
	public void updateUser(User user) {
		userMap.put(user.getToken(), user);
	}
	
	@Override
	public void deleteUser(User user) {
		userMap.remove(user.getToken());
	}
	
	@Override
	public void addLibrary(Library lib) {
		String id = "l" + indexL++;	
		lib.setId(id);
		libraryMap.put(id,lib);
	}
	
	@Override
	public void addLibraries(List<Library> libs) {	
		for (Library lib:libs) {
			addLibrary(lib);
		}
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
	public void addFilms(List<Film> films) {
		for (Film film:films)
			addFilm(film);
	}
	
	@Override
	public void addFilm(Film film) {
		String id = "f" + indexF++;
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
