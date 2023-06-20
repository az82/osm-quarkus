package http.authz

import future.keywords

# Default: Only authorized access
auth_paths := {
    "**",
    }

anon_paths := {
    "/",
    "/favicon.ico",
    "/_renarde/security/github-success"
    }

bad_strings := {
    "secret",
    "password",
    "credential"
}

default allow := false

# Allow unauthenticated access to anon_paths
allow {
    glob.match(anon_paths[_], ["/"], input.path)
}

# Allow authenticated access to auth_paths
allow {
    # Allow authenticated access to pre-defined paths
    input.principal != null
    glob.match(auth_paths[_], ["/"], input.path)

    # Only allow requests with query strings that do not contain any bad words
    not contains_bad_words
}

# Tells whether the query string contains one of the bad words
contains_bad_words {
    contains(lower(input.query), bad_strings[_])
}