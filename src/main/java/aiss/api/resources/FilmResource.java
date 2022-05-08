package aiss.api.resources;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.NotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import aiss.model.Film;
import aiss.model.repository.MapLibraryRepository;
import aiss.model.repository.LibraryRepository;

import java.net.URI;
import java.util.Collection;



@Path("/films")
public class FilmResource {

	public static FilmResource _instance=null;
	LibraryRepository repository;

	private FilmResource(){
		repository=MapLibraryRepository.getInstance();
	}

	public static FilmResource getInstance()
	{
		if(_instance==null)
			_instance=new FilmResource();
		return _instance; 
	}

	@GET
	@Produces("application/json")
	public Collection<Film> getAll()
	{
		return repository.getAllFilms();
	}
	
	
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Film get(@PathParam("id") String filmId)
	{
		Film song=repository.getFilm(filmId);
		
		if (song == null) {
			throw new NotFoundException("The song with id="+ filmId +" was not found");			
		}
		
		return song;
	
	}
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response addSong(@Context UriInfo uriInfo, Film film) {
		if (film.getTitle() == null || "".equals(film.getTitle()))
			throw new BadRequestException("The name of the playlist must not be null");
		
		repository.addFilm(film);

		// Builds the response. Returns the playlist the has just been added.
		UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "get");
		URI uri = ub.build(film.getId());
		ResponseBuilder resp = Response.created(uri);
		resp.entity(film);			
		return resp.build();
	}
	
	
	@PUT
	@Consumes("application/json")
	public Response updateSong(Film film) {
		Film oldsong = repository.getFilm(film.getId());
		if (oldsong == null) {
			throw new NotFoundException("The song with id="+ film.getId() +" was not found");			
		}
		
		if (film.getTitle()!=null)
			oldsong.setTitle(film.getTitle());
		
		if (film.getGenre()!=null)
			oldsong.setGenre(film.getGenre());
		
		if (film.getPremiere()!=null)
			oldsong.setPremiere(film.getPremiere());
		
		if (film.getRuntime()!=null)
			oldsong.setRuntime(film.getRuntime());
		
		if (film.getScore()!=null)
			oldsong.setScore(film.getScore());
		
		if (film.getRuntime()!=null)
			oldsong.setLanguage(film.getLanguage());

		
		return Response.noContent().build();
	}
	
	@DELETE
	@Path("/{id}")
	public Response removeSong(@PathParam("id") String filmId) {
		Film toberemoved=repository.getFilm(filmId);
		if (toberemoved == null)
			throw new NotFoundException("The playlist with id="+ filmId +" was not found");
		else
			repository.deleteFilm(filmId);
		
		return Response.noContent().build();
	}
	
}
