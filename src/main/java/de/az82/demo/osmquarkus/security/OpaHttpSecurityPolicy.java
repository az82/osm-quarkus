package de.az82.demo.osmquarkus.security;

import io.quarkus.logging.Log;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.vertx.http.runtime.security.HttpSecurityPolicy;
import io.smallrye.mutiny.Uni;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom security policy that delegates authorization to OPA.
 */
@ApplicationScoped
public class OpaHttpSecurityPolicy implements HttpSecurityPolicy {

    private final OpaRestClient opaRestClient;

    /**
     * Constructor.
     *
     * @param opaRestClient reactive OPA REST client
     */
    @Inject
    public OpaHttpSecurityPolicy(@RestClient OpaRestClient opaRestClient) {
        this.opaRestClient = opaRestClient;
    }

    @Override
    public Uni<CheckResult> checkPermission(RoutingContext routingContext, Uni<SecurityIdentity> identityUni, AuthorizationRequestContext requestContext) {
        return identityUni.chain(identity -> {
                    var request = routingContext.request();
                    var method = request.method().name();
                    var path = request.path();
                    var headers = flattenHeaders(request.headers());
                    var principal = identity.getPrincipal().getName();

                    return Uni.createFrom().completionStage(
                            opaRestClient.allow(new OpaRequest(new OpaRequest.Input(
                                            method,
                                            path,
                                            headers,
                                            principal
                                    )))
                                    .thenApply(r -> {
                                        if (Log.isDebugEnabled()) {
                                            Log.debugv("Access {0} on {1} {2} for {3}",
                                                    r.result() ? "granted" : "denied",
                                                    method,
                                                    path,
                                                    String.valueOf(principal)
                                            );
                                        }
                                        return new CheckResult(r.result());
                                    })
                                    .exceptionally(e -> {
                                        Log.error("Error contacting OPA", e);
                                        return CheckResult.DENY;
                                    })
                    );
                }

        );
    }

    private static String getPrincipalName(SecurityIdentity identity) {
        // The principal name may be:
        // - "" if the identity is anonymous
        // - null if the identity is present, but the user name has not been properly populated
        // For better security we unify these two cases into null
        return !identity.isAnonymous()
                ? identity.getPrincipal().getName()
                : null;
    }

    private static Map<String, List<String>> makeSerializable(MultiMap headers) {
        var result = new HashMap<String, List<String>>();

        for (Map.Entry<String, String> entry : headers) {
            result.computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
                    .add(entry.getValue());
        }

        return result;
    }
}
