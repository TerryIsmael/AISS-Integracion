package aiss.model.repository;

import java.util.Collection;

import aiss.model.Library;
import aiss.model.Film;

public interface LibraryRepository {
	
	
	// Songs
	public void addSong(Film s);
	public Collection<Film> getAllSongs();
	public Film getSong(String filmId);
	public void updateSong(Film s);
	public void deleteSong(String filmId);
	
	// Playlists
	public void addPlaylist(Library p);
	public Collection<Library> getAllPlaylists();
	public Library getPlaylist(String playlistId);
	public void updatePlaylist(Library p);
	public void deletePlaylist(String playlistId);
	public Collection<Film> getAll(String playlistId);
	public void addSong(String playlistId, String songId);
	public void removeSong(String playlistId, String songId); 

	
	
	
	

}
