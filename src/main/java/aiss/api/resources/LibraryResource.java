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





@Path("/lists")
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
		Collection<Library> res=repository.getAllPlaylists();
		if (isEmpty!=null) {
			if (isEmpty==true) {
				res= res.stream().filter(x->x.getSongs().size()==0).collect(Collectors.toList());
			}else {
				res= res.stream().filter(x->x.getSongs().size()!=0).collect(Collectors.toList());
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
		Library list = repository.getPlaylist(id);
		
		if (list == null) {
			throw new NotFoundException("The playlist with id="+ id +" was not found");			
		}
		
		return list;
	}
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response addPlaylist(@Context UriInfo uriInfo, Library playlist) {
		if (playlist.getName() == null || "".equals(playlist.getName()))
			throw new BadRequestException("The name of the playlist must not be null");
		
		if (playlist.getSongs()!=null)
			throw new BadRequestException("The songs property is not editable.");

		repository.addPlaylist(playlist);

		// Builds the response. Returns the playlist the has just been added.
		UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "get");
		URI uri = ub.build(playlist.getId());
		ResponseBuilder resp = Response.created(uri);
		resp.entity(playlist);			
		return resp.build();
	}

	
	@PUT
	@Consumes("application/json")
	public Response updatePlaylist(Library playlist) {
		Library oldplaylist = repository.getPlaylist(playlist.getId());
		if (oldplaylist == null) {
			throw new NotFoundException("The playlist with id="+ playlist.getId() +" was not found");			
		}
		
		if (playlist.getSongs()!=null)
			throw new BadRequestException("The songs property is not editable.");
		
		// Update name
		if (playlist.getName()!=null)
			oldplaylist.setName(playlist.getName());
		
		// Update description
		if (playlist.getDescription()!=null)
			oldplaylist.setDescription(playlist.getDescription());
		
		return Response.noContent().build();
	}
	
	@DELETE
	@Path("/{id}")
	public Response removePlaylist(@PathParam("id") String id) {
		Library toberemoved=repository.getPlaylist(id);
		if (toberemoved == null)
			throw new NotFoundException("The playlist with id="+ id +" was not found");
		else
			repository.deletePlaylist(id);
		
		return Response.noContent().build();
	}
	
	
	@POST	
	@Path("/{playlistId}/{songId}")
	@Consumes("text/plain")
	@Produces("application/json")
	public Response addSong(@Context UriInfo uriInfo,@PathParam("playlistId") String playlistId, @PathParam("songId") String songId)
	{				
		
		Library playlist = repository.getPlaylist(playlistId);
		Film song = repository.getSong(songId);
		
		if (playlist==null)
			throw new NotFoundException("The playlist with id=" + playlistId + " was not found");
		
		if (song == null)
			throw new NotFoundException("The song with id=" + songId + " was not found");
		
		if (playlist.getSong(songId)!=null)
			throw new BadRequestException("The song is already included in the playlist.");
			
		repository.addSong(playlistId, songId);		

		// Builds the response
		UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "get");
		URI uri = ub.build(playlistId);
		ResponseBuilder resp = Response.created(uri);
		resp.entity(playlist);			
		return resp.build();
	}
	
	
	@DELETE
	@Path("/{playlistId}/{songId}")
	public Response removeSong(@PathParam("playlistId") String playlistId, @PathParam("songId") String songId) {
		Library playlist = repository.getPlaylist(playlistId);
		Film song = repository.getSong(songId);
		
		if (playlist==null)
			throw new NotFoundException("The playlist with id=" + playlistId + " was not found");
		
		if (song == null)
			throw new NotFoundException("The song with id=" + songId + " was not found");
		
		
		repository.removeSong(playlistId, songId);		
		
		return Response.noContent().build();
	}
}