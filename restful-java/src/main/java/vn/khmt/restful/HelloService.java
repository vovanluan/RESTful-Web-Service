package vn.khmt.restful;

import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;
import vn.khmt.db.ConnectToSQL;
/**
 * REST Web Service
 *
 * @author TheNhan
 */
@Path("hello")
public class HelloService {

    @GET
    @Path("/{param}")
    public Response getMsg(@PathParam("param") String msg) {

        String output = "Hello " + msg;
        
        return Response.status(200).entity(output).build();
    }
}
