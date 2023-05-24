package de.az82.demo.osmquarkus.security;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.concurrent.CompletionStage;


/**
 * Reactive REST client for OPA.
 */
@Path("http/authz")
@RegisterRestClient
public interface OpaRestClient {
    /**
     * Check whether a request is allowed
     *
     * @param request OPA request
     * @return OPA response
     */
    @POST
    @Path("/allow")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CompletionStage<OpaResponse> allow(OpaRequest request);
}
