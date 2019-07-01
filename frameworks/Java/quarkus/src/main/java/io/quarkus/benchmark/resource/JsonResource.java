package io.quarkus.benchmark.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.TreeMap;

@Path("/json")
public class JsonResource {
    private static final Map<String, String> message;
    static {
        message = new TreeMap<>();
        message.put("message", "Hello, World!");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map json() {
        return message;
    }

}
