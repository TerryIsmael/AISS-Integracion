package aiss.api.resources;

import java.net.URI;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
	
	public static Predicate<String> p1=v-> v == null || "".equals(v);

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
    public Collection<Film> getAll(@QueryParam("sort") String sort,@QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
    		@QueryParam("title") String title, @QueryParam("genre") String genre, @QueryParam("premiere") String premiere,
            @QueryParam("runtime") Integer runtime, @QueryParam("score") String score, @QueryParam("language") String language){    
		Collection<Film>  res= repository.getAllFilms();
		
		if ( title != null) {
            res = res.stream().filter(x -> x.getTitle().contains(title)).collect(Collectors.toList());
        } 
        if (genre != null) {
            res = res.stream().filter(x -> x.getGenre().equals(genre)).collect(Collectors.toList());
        }
        if (premiere != null) {
        	if(premiere.matches("[0-3]?[1-9][/-][01]?[1-9][/-][12][0-9][0-9][0-9]")) {
        		res = res.stream().filter(x -> x.getPremiere().equals(premiere)).collect(Collectors.toList());
            }else if(premiere.matches("[12][0-9][0-9][0-9]")) {
            	res = res.stream().filter(x-> x.getPremiere().split("-")[2].equals(premiere)).collect(Collectors.toList());
            }else {
            	throw new BadRequestException("Illegal query parameter: premiere. This must have the format dd-m-YYYY or YYYY");		
            }
        }
        if (runtime != null) {
        	if(runtime>=0) {
        		res = res.stream().filter(x -> x.getRuntime().equals(runtime)).collect(Collectors.toList());
        	}else {
        		throw new BadRequestException("Illegal query parameter: runtime. Its value cannot be negative");	
			}
        }
		
		if (score != null) {
			if(score.matches("[0-9.]+[-][0-9.]+")) {
	            String[] trozos = score.split("-");
	            Double rangoInferior = Double.valueOf(trozos[0].trim());
	            Double rangoSuperior = Double.valueOf(trozos[1].trim());
	            res = res.stream().filter(x -> x.getScore() <= rangoSuperior && x.getScore() >= rangoInferior).collect(Collectors.toList());
			}else {
				throw new BadRequestException("Query parameter: score. Its value is not set properly. try -> X-X. Example: 2.5-4 ");	
			}
		}
		
		if (language != null) {
            res = res.stream().filter(x -> x.getLanguage().contains(language)).collect(Collectors.toList());
        }
		
		if (!p1.test(sort)) {
	        if(sort.equals("score")) {
	            res= repository.getAllFilms().stream().sorted(Comparator.comparing(Film::getScore).reversed()).collect(Collectors.toList());
	        } else if(sort.equals("-score")) {
	            res= repository.getAllFilms().stream().sorted(Comparator.comparing(Film::getScore)).collect(Collectors.toList());
	        } else if(sort.equals("date")) {
	            res= repository.getAllFilms().stream().sorted(Comparator.comparing(Film::parsePremiere).reversed()).collect(Collectors.toList());
	        } else if(sort.equals("-date")) {
	        	res= repository.getAllFilms().stream().sorted(Comparator.comparing(Film::parsePremiere)).collect(Collectors.toList());
	        } else if(sort.equals("title")) {
	        	res= repository.getAllFilms().stream().sorted(Comparator.comparing(Film::getTitle)).collect(Collectors.toList());
	        } else if(sort.equals("-title")) {
	        	res= repository.getAllFilms().stream().sorted(Comparator.comparing(Film::getTitle).reversed()).collect(Collectors.toList());
	        } else if(sort.equals("runtime")) {
	        	res= repository.getAllFilms().stream().sorted(Comparator.comparing(Film::getRuntime).reversed()).collect(Collectors.toList());
	        } else if(sort.equals("-runtime")) {
	        	res= repository.getAllFilms().stream().sorted(Comparator.comparing(Film::getRuntime)).collect(Collectors.toList());
	        } else {
	            throw new BadRequestException("Query parameter: sort. Its value is not set properly. try -> 'score', '-score', 'date', '-date', 'title', '-title', 'runtime', '-runtime'");
	        }
		}
		
		if(offset!=null || limit != null)
	        if(offset!=null && limit != null && limit>=0 && offset>=0) {
	        		res = res.stream().collect(Collectors.toList()).subList(offset, offset+limit);	
	        }else if(offset!= null && limit == null && offset>=0) {
		           res= res.stream().collect(Collectors.toList()).subList(offset, res.size());
	        } else if(offset == null && limit != null && limit>=0) {
		            res= res.stream().collect(Collectors.toList()).subList(0, limit);
	        }else {
	        		throw new BadRequestException("Illegal query parameters: Offset and limit values cannot be negative");	
	        }
		return res;
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
		if (p1.test(film.getTitle()))
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
		try {
			LocalDate.parse(data, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		 return true;
		}catch(DateTimeException e){
			return false;
		}
	}
	
}
