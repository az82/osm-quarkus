package de.az82.demo.osmquarkus;

import io.quarkus.logging.Log;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.jose4j.jwt.MalformedClaimException;

import java.util.Map;
import java.util.Objects;

/**
 * Identity augmentor for non-OIDC compliant OAuth2 providers.
 * <p>
 * If Quarkus uses a non-OIDC compliant OAuth2 provider that does not provide an Id token, Quarkus will create a
 * "virtual" Id token. In this case, Quarkus calls the userinfo endpoint of the provider, but does not populate the Id
 * token with user information.
 * <p>
 * This augmentor populates the Id token with information from the userinfo endpoint.
 */
@ApplicationScoped
public class OAuth2IdentityAugmentor implements SecurityIdentityAugmentor {

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
        try {
            var principal = identity.getPrincipal();
            if (principal instanceof OidcJwtCallerPrincipal jwtPrincipal) {
                var claims = jwtPrincipal.getClaims();

                var userinfo = claims.getClaimValue("userinfo", Map.class);
                if (claims.getSubject() == null && userinfo != null) {
                    // Use the login field.
                    // Tested with GitHub
                    claims.setSubject(Objects.toString(userinfo.get("login"), null));
                }
            }
        } catch (MalformedClaimException e) {
            Log.error("Malformed token", e);
        }

        return Uni.createFrom().item(identity);
    }

}
