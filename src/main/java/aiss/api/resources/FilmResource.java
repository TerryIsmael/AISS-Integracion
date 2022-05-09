package aiss.api.resources;

import java.net.URI;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.NotFoundException;

import aiss.model.Film;
import aiss.model.repository.LibraryRepository;
import aiss.model.repository.MapLibraryRepository;



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
		Film film=repository.getFilm(filmId);
		
		if (film == null) {
			throw new NotFoundException("The film with id="+ filmId +" was not found");			
		}
		
		return film;
	
	}
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response addFilm(@Context UriInfo uriInfo, Film film) {
		if (film.getTitle() == null || "".equals(film.getTitle()))
			throw new BadRequestException("The name of the playlist must not be null");
		
		if (!(film.getPremiere().matches("[0-3]?[0-9][//][01]?[0-9][//][1-2][0-9][0-9][0-9]")) && !validaFecha(film.getPremiere())) 
			throw new BadRequestException(film.getPremiere()+" is not an valid Premiere value. Try with dd/mm/YYYY");
		
		repository.addFilm(film);

		UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "get");
		URI uri = ub.build(film.getId());
		ResponseBuilder resp = Response.created(uri);
		resp.entity(film);			
		return resp.build();
	}

	
	@PUT
	@Consumes("application/json")
	public Response updateFilm(Film film) {
		Film oldfilm = repository.getFilm(film.getId());
		if (oldfilm == null) {
			throw new NotFoundException("The film with id="+ film.getId() +" was not found");			
		}
		
		if (film.getTitle()!=null)
			oldfilm.setTitle(film.getTitle());
		
		if (film.getGenre()!=null)
			oldfilm.setGenre(film.getGenre());
		
		if (film.getPremiere()!=null) {
			
			if (!(film.getPremiere().matches("[0-3]?[0-9][-][01]?[0-9][-][1-2][0-9][0-9][0-9]")) && !validaFecha(film.getPremiere())) 
				throw new BadRequestException(film.getPremiere() + " is not an valid Premiere value. Try with dd/mm/YYYY");
			
			oldfilm.setPremiere(film.getPremiere());
		}
		
		if (film.getRuntime()!=null)
			oldfilm.setRuntime(film.getRuntime());
		
		if (film.getScore()!=null)
			oldfilm.setScore(film.getScore());
		
		if (film.getRuntime()!=null)
			oldfilm.setLanguage(film.getLanguage());

		
		return Response.noContent().build();
	}
	
	@DELETE
	@Path("/{id}")
	public Response removeFilm(@PathParam("id") String filmId) {
		Film toberemoved=repository.getFilm(filmId);
		if (toberemoved == null)
			throw new NotFoundException("The playlist with id="+ filmId +" was not found");
		else
			repository.deleteFilm(filmId);
		
		return Response.noContent().build();
	}
	
	boolean validaFecha(String data){
		//int[] diasMes= {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		try {
			LocalDate fecha= LocalDate.parse(data, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		 return true;
		}catch(DateTimeException e){
			return false;
		}
//		 System.out.println(fecha);
//	    if ( fecha.getMonthValue()==2 && fecha.getYear()%4==0 ) {
//	    	System.out.println("#############################################################");
//	        return fecha.getDayOfMonth()>=1 && fecha.getDayOfMonth()<=29;
//	    }
//	    System.out.println(fecha.getDayOfMonth()+"  "+(fecha.getDayOfMonth()<=diasMes[fecha.getMonthValue()-1])+"  "+(fecha.getDayOfMonth()>0));
//	    return fecha.getDayOfMonth()>0 && fecha.getDayOfMonth()<=diasMes[fecha.getMonthValue()-1];
	}
	
}
