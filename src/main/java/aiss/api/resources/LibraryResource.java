package aiss.api.resources;

import java.net.URI;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.NotFoundException;

import aiss.model.Library;
import aiss.model.Film;
import aiss.model.repository.MapLibraryRepository;
import aiss.model.repository.LibraryRepository;





@Path("api/libraries")
public class LibraryResource {
	
	/* Singleton */
	private static LibraryResource _instance=null;
	LibraryRepository repository;
	
	private LibraryResource() {
		repository=MapLibraryRepository.getInstance();

	}
	
	public static LibraryResource getInstance()
	{
		if(_instance==null)
				_instance=new LibraryResource();
		return _instance;
	}


	@GET
	@Produces("application/json")
	public Collection<Library> getAll(@QueryParam("order") String order,@QueryParam("isEmpty") Boolean isEmpty, @QueryParam("name") String name)
	{
		Collection<Library> res=repository.getAllLibraries();
		if (isEmpty!=null) {
			if (isEmpty==true) {
				res= res.stream().filter(x->x.getFilms().size()==0).collect(Collectors.toList());
			}else {
				res= res.stream().filter(x->x.getFilms().size()!=0).collect(Collectors.toList());
			}
		}

		if (name!=null) {
			res= res.stream().filter(x->x.getName()==name).collect(Collectors.toList());
		}
		if (order!=null && (order.equals("name") || order.equals("-name"))) {
			if (order.equals("name")) {
				res= res.stream().sorted(Comparator.comparing(x->x.getName())).collect(Collectors.toList());
			}else {
				res= res.stream().sorted(Comparator.comparing(x->x.getName(),Comparator.reverseOrder())).collect(Collectors.toList());
			}
		}
		return res;
	}
	
	
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Library get(@PathParam("id") String id)
	{
		Library list = repository.getLibrary(id);
		
		if (list == null) {
			throw new NotFoundException("The library with id="+ id +" was not found");			
		}
		
		return list;
	}
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response addLibrary(@Context UriInfo uriInfo, Library library) {
		if (library.getName() == null || "".equals(library.getName()))
			throw new BadRequestException("The name of the library must not be null");
		
		if (library.getFilms()!=null)
			throw new BadRequestException("The films property is not editable.");

		repository.addLibrary(library);

		// Builds the response. Returns the library the has just been added.
		UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "get");
		URI uri = ub.build(library.getId());
		ResponseBuilder resp = Response.created(uri);
		resp.entity(library);			
		return resp.build();
	}

	
	@PUT
	@Consumes("application/json")
	public Response updateLibrary(Library library) {
		Library oldlibrary = repository.getLibrary(library.getId());
		if (oldlibrary == null) {
			throw new NotFoundException("The library with id="+ library.getId() +" was not found");			
		}
		
		if (library.getFilms()!=null)
			throw new BadRequestException("The films property is not editable.");
		
		// Update name
		if (library.getName()!=null)
			oldlibrary.setName(library.getName());
		
		// Update description
		if (library.getDescription()!=null)
			oldlibrary.setDescription(library.getDescription());
		
		return Response.noContent().build();
	}
	
	@DELETE
	@Path("/{id}")
	public Response removeLibrary(@PathParam("id") String id) {
		Library toberemoved=repository.getLibrary(id);
		if (toberemoved == null)
			throw new NotFoundException("The library with id="+ id +" was not found");
		else
			repository.deleteLibrary(id);
		
		return Response.noContent().build();
	}
	
	
	@POST	
	@Path("/{libraryId}/{filmId}")
	@Consumes("text/plain")
	@Produces("application/json")
	public Response addFilm(@Context UriInfo uriInfo,@PathParam("libraryId") String libraryId, @PathParam("filmId") String filmId)
	{				
		
		Library library = repository.getLibrary(libraryId);
		Film film = repository.getFilm(filmId);
		
		if (library==null)
			throw new NotFoundException("The library with id=" + libraryId + " was not found");
		
		if (film == null)
			throw new NotFoundException("The film with id=" + filmId + " was not found");
		
		if (library.getFilm(filmId)!=null)
			throw new BadRequestException("The film is already included in the library.");
			
		repository.addFilm(libraryId, filmId);		

		// Builds the response
		UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "get");
		URI uri = ub.build(libraryId);
		ResponseBuilder resp = Response.created(uri);
		resp.entity(library);			
		return resp.build();
	}
	
	
	@DELETE
	@Path("/{libraryId}/{filmId}")
	public Response removeFilm(@PathParam("libraryId") String libraryId, @PathParam("filmId") String filmId) {
		Library library = repository.getLibrary(libraryId);
		Film film = repository.getFilm(filmId);
		
		if (library==null)
			throw new NotFoundException("The library with id=" + libraryId + " was not found");
		
		if (film == null)
			throw new NotFoundException("The film with id=" + filmId + " was not found");
		
		
		repository.removeFilm(libraryId, filmId);		
		
		return Response.noContent().build();
	}
}