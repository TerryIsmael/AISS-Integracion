package aiss.model.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
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
	
	
	//TODO Read the file
	public void init() {
		
		libraryMap = new HashMap<String,Library>();
		filmMap = new HashMap<String,Film>();
	
		try{
			Files.lines(Path.of(""));
		}catch(IOException e) {
			System.out.println(e+". No se puedo encontrar/leer el fichero");
		}
		
	}
	
	@Override
	public void addPlaylist(Library p) {
		String id = "p" + index++;	
		p.setId(id);
		libraryMap.put(id,p);
	}
	
	@Override
	public Collection<Library> getAllPlaylists() {
			return libraryMap.values();
	}

	@Override
	public Library getPlaylist(String id) {
		return libraryMap.get(id);
	}
	
	@Override
	public void updatePlaylist(Library p) {
		libraryMap.put(p.getId(),p);
	}

	@Override
	public void deletePlaylist(String id) {	
		libraryMap.remove(id);
	}
	

	@Override
	public void addSong(String LibraryId, String songId) {
		Library playlist = getPlaylist(LibraryId);
		playlist.addSong(filmMap.get(songId));
	}

	@Override
	public Collection<Film> getAll(String LibraryId) {
		return getPlaylist(LibraryId).getSongs();
	}

	@Override
	public void removeSong(String LibraryId, String songId) {
		getPlaylist(LibraryId).deleteSong(songId);
	}

	
	// Song related operations
	
	@Override
	public void addSong(Film s) {
		String id = "s" + index++;
		s.setId(id);
		filmMap.put(id, s);
	}
	
	@Override
	public Collection<Film> getAllSongs() {
			return filmMap.values();
	}

	@Override
	public Film getSong(String songId) {
		return filmMap.get(songId);
	}

	@Override
	public void updateSong(Film s) {
		Film song = filmMap.get(s.getId());
		song.setTitle(s.getTitle());
		song.setAlbum(s.getAlbum());
		song.setArtist(s.getArtist());
		song.setYear(s.getYear());
	}

	@Override
	public void deleteSong(String songId) {
		filmMap.remove(songId);
	}
	
}
