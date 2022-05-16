package aiss.api.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.spi.BadRequestException;

import aiss.model.User;
import aiss.model.repository.LibraryRepository;
import aiss.model.repository.MapLibraryRepository;

@Path("/users")
public class UserResource {
	/* Singleton */
	public static Predicate<String> p1=v-> v == null || "".equals(v.trim());
	public static Predicate<User> p2=v-> v == null;
	public static UserResource _instance = null;
	LibraryRepository repository;
	
	private UserResource() {
		repository = MapLibraryRepository.getInstance();
	}
	
	public static UserResource getInstance() {
		if(_instance == null)
			_instance=new UserResource();
		return _instance;
	}
	
	@GET	
	@Produces("application/json")
	public Collection<String> getAll() {
		Collection<String> userNames = new ArrayList<String>();
		for(User u:repository.getAllUsers()) {
			userNames.add(u.getName());
		}
		return userNames;
	}
	
	@GET
	@Path("/{token}")
	@Produces("application/json")
	public User get(@PathParam("token") String token) {
		User user = repository.getAllUsers().stream().filter(x->x.getToken()==token).findFirst().orElse(null);
		if (user.equals(null)) {
			throw new BadRequestException("Provided token is not a valid token");
		}
		return user;
	}
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response addUser(@Context UriInfo uriInfo, User user) {
		if(p1.test(user.getName())) {
			throw new BadRequestException("The username cannot be empty");
		}
		if(p1.test(user.getPassword())) {
			throw new BadRequestException("The password cannot be empty");
		}
		if(user.getName().contains(" ") || user.getPassword().contains(" ")) {
			throw new BadRequestException("The username or password cannot contain blank spaces");
		}
		if(repository.getAllUsers().stream().anyMatch(x->x.getName().equals(user.getName()))) {
			throw new BadRequestException("This username is already in use");
		}
		repository.addUser(user);
		
		UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "get");
		URI uri = ub.build(user.getToken());
		ResponseBuilder resp = Response.created(uri);
		resp.entity(user);
		return resp.build();
	}
	@POST
	@Path("/login")
	@Consumes("application/json")
	@Produces("application/json")
	public Response login(@Context UriInfo uriInfo, User user) {
		if(p1.test(user.getName())) {
			throw new BadRequestException("The username cannot be empty");
		}
		if(p1.test(user.getPassword())) {
			throw new BadRequestException("The password cannot be empty");
		}
		Collection<User> users = repository.getAllUsers();
		User respUser = users.stream().filter(x->x.getName().equals(user.getName())).findFirst().orElse(null);
		if (p2.test(respUser)) {
			throw new BadRequestException("The user "+ user.getName()+" does not exist");
		} else if(!respUser.getPassword().equals(user.getPassword())) {
			throw new BadRequestException("The password is incorrect");
		}
		
		UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "get");
		URI uri = ub.build(respUser.getToken());
		ResponseBuilder resp = Response.created(uri);
		resp.entity(respUser);
		return resp.build();
	}
}
