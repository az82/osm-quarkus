package de.az82.demo.osmquarkus;

import io.quarkus.logging.Log;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.vertx.http.runtime.security.HttpSecurityPolicy;
import io.smallrye.mutiny.Uni;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.HashMap;
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
                    var principal = getPrincipalName(identity);

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
        // Unauthenticated -> No principal name
        if (identity.isAnonymous()) {
            return null;
        }

        var principalName = identity.getPrincipal().getName();
        if (principalName != null) {
            // Default: Quarkus has correctly identified the principal
            return principalName;
        } else {
            // GitHub is not OIDC compliant and does not provide an Id token
            // Quarkus creates a "virtual" token but does not map the login userinfo field to the virtual token's
            // sub claim. So we get the GitHub login from userinfo and treat that as principal name.
            var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
            return ((Map<?, ?>) principal.getClaim("userinfo")).get("login").toString();
        }
    }

    private static Map<String, String> flattenHeaders(MultiMap headers) {
        var result = new HashMap<String, String>();

        for (Map.Entry<String, String> entry : headers) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
