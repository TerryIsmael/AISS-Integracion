package aiss.api.resources;

import java.net.URI;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
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
import aiss.model.User;
import aiss.model.repository.LibraryRepository;
import aiss.model.repository.MapLibraryRepository;



@Path("/films")
public class FilmResource {
	
	public static Predicate<String> isNull=v-> v == null || "".equals(v.trim());

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
            res = res.stream().filter(x -> x.getTitle().toLowerCase().contains(title.toLowerCase())).collect(Collectors.toList());
        } 
        if (genre != null) {
            res = res.stream().filter(x -> x.getGenre().stream().map(y->y.toLowerCase()).collect(Collectors.toList()).contains(genre)).collect(Collectors.toList());
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
				throw new BadRequestException("Illegal query parameter: score. Its value is not set properly. try -> X-X. Example: 2.5-4 ");	
			}
		}
		
		if (language != null) {
            res = res.stream().filter(x -> x.getLanguage().contains(language)).collect(Collectors.toList());
        }
		
		if (!isNull.test(sort)) {
	        if(sort.equals("score")) {
	            res= res.stream().sorted(Comparator.comparing(Film::getScore).reversed()).collect(Collectors.toList());
	        } else if(sort.equals("-score")) {
	            res= res.stream().sorted(Comparator.comparing(Film::getScore)).collect(Collectors.toList());
	        } else if(sort.equals("date")) {
	            res= res.stream().sorted(Comparator.comparing(Film::parsePremiere).reversed()).collect(Collectors.toList());
	        } else if(sort.equals("-date")) {
	        	res= res.stream().sorted(Comparator.comparing(Film::parsePremiere)).collect(Collectors.toList());
	        } else if(sort.equals("title")) {
	        	res= res.stream().sorted(Comparator.comparing(Film::getTitle)).collect(Collectors.toList());
	        } else if(sort.equals("-title")) {
	        	res= res.stream().sorted(Comparator.comparing(Film::getTitle).reversed()).collect(Collectors.toList());
	        } else if(sort.equals("runtime")) {
	        	res= res.stream().sorted(Comparator.comparing(Film::getRuntime).reversed()).collect(Collectors.toList());
	        } else if(sort.equals("-runtime")) {
	        	res= res.stream().sorted(Comparator.comparing(Film::getRuntime)).collect(Collectors.toList());
	        } else {
	            throw new BadRequestException("Query parameter: sort. Its value is not set properly. try -> 'score', '-score', 'date', '-date', 'title', '-title', 'runtime', '-runtime'");
	        }
		}
		
		if(offset!=null || limit != null) {
			if(offset == null && limit != null && limit>=0) {
		       	Integer newLimit=limit<res.size()?limit:res.size();
		       	res= res.stream().limit(newLimit).collect(Collectors.toList());
	        }else if(offset!=null) {
	        	if(limit != null && limit>=0 && offset>=0) {
		        	Integer newLimit=limit<res.size()-offset?limit:res.size()-offset;
		        	Integer newOffset=offset<res.size()?offset:res.size();
		        	res = res.stream().collect(Collectors.toList()).subList(newOffset, offset+newLimit);	
		        }else if(limit == null && offset>=0) {
		        	Integer newOffset=offset<res.size()?offset:res.size();
		        	res= res.stream().collect(Collectors.toList()).subList(newOffset, res.size());    
		        }else {
	        	throw new BadRequestException("Illegal query parameters: Offset and limit values cannot be negative");	
		        }
	        }else {
	        	throw new BadRequestException("Illegal query parameters: Limit value cannot be negative");	
	        }
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
	public Response addFilm(@Context UriInfo uriInfo,@PathParam("token") String token,Film film) {
		if(isNull.test(token)) {
			throw new BadRequestException("Token parameter cannot be null");
		}
		
		Optional<User> user = User.getNameFromToken(token, repository);
		if(!user.isPresent()) {
			throw new BadRequestException("Provided token is not valid");
		}
		
		if (isNull.test(film.getTitle()))
			throw new BadRequestException("The name of the library cannot be null");
		
		if (!(film.getPremiere().matches("[0-3]?[0-9][//][01]?[0-9][//][1-2][0-9][0-9][0-9]")) && !validaFecha(film.getPremiere())) 
			throw new BadRequestException(film.getPremiere()+" is not an valid Premiere value. Try with dd/mm/YYYY");
		
		film.setUsername(user.get().getName());
		
		repository.addFilm(film);

		UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "get");
		URI uri = ub.build(film.getId());
		ResponseBuilder resp = Response.created(uri);
		resp.entity(film);			
		return resp.build();
	}

	
	@PUT
	@Consumes("application/json")
	public Response updateFilm(Film film, @PathParam("token") String token) {
		Film oldfilm = repository.getFilm(film.getId());
		if (oldfilm == null) {
			throw new NotFoundException("The film with id="+ film.getId() +" was not found");			
		}
		
		if(isNull.test(token)) {
			throw new BadRequestException("Token parameter cannot be null");
		}
		
		Optional<User> user = User.getNameFromToken(token, repository);
		if(!user.isPresent()) {
			throw new BadRequestException("Provided token is not valid");
		}
		
		if(!user.get().getName().equals(oldfilm.getUsername())) {
			throw new BadRequestException("You cannot modify a film that does not belong to you");
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
		
		repository.updateFilm(oldfilm);
		
		return Response.noContent().build();
	}
	
	@DELETE
	@Path("/{id}")
	public Response removeFilm(@PathParam("id") String filmId, @PathParam("token") String token) {
		Film toberemoved=repository.getFilm(filmId);
		if (toberemoved == null)
			throw new NotFoundException("The playlist with id="+ filmId +" was not found");
		
		if(isNull.test(token)) {
			throw new BadRequestException("Token parameter cannot be null");
		}
		
		Optional<User> user = User.getNameFromToken(token, repository);
		if(!user.isPresent()) {
			throw new BadRequestException("Provided token is not valid");
		}
		
		if(!user.get().getName().equals(toberemoved.getUsername())) {
			throw new BadRequestException("You cannot modify a film that does not belong to you");
		}
		
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
