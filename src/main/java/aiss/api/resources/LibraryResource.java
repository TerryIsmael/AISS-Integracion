package aiss.api.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
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
import aiss.model.Library;
import aiss.model.repository.LibraryRepository;
import aiss.model.repository.MapLibraryRepository;





@Path("/libraries")
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
			throw new BadRequestException("Films property is not editable by this way.");

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
			throw new BadRequestException("Films property is not editable by this way.");
		
		if (library.getName()!=null)
			oldlibrary.setName(library.getName());
		
		if (library.getDescription()!=null)
			oldlibrary.setDescription(library.getDescription());
		
		return Response.noContent().build();
	}
	
	@PUT
	@Path("/{myId}/{copyId}")
	public Response updateLibrary(@PathParam("myId") String myId,@PathParam("copyId") String copyId) {
		Library myLibrary = repository.getLibrary(myId);
		Library copyLibrary = repository.getLibrary(copyId);
		
		if (myLibrary == null) {
			throw new NotFoundException("The library with id="+ myId +" was not found");			
		}
		
		if (copyLibrary == null) {
			throw new NotFoundException("The library with id="+ copyId +" was not found");			
		}
		List<Film> newFilms=myLibrary.getFilms();
		newFilms.addAll(copyLibrary.getFilms());
		myLibrary.setFilms(newFilms);
		repository.updateLibrary(myLibrary);
		
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
		if(!filmId.equals("*")) {
			Film film = repository.getFilm(filmId);
			
			if (library==null)
				throw new NotFoundException("The library with id=" + libraryId + " was not found");
			
			if (film == null)
				throw new NotFoundException("The film with id=" + filmId + " was not found");
			
			
			repository.removeFilm(libraryId, filmId);		
		}else {
			library.setFilms(new ArrayList<>());
			repository.updateLibrary(library);
		}
		return Response.noContent().build();
	}
}