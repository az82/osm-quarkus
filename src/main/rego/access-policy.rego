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

# 1. Add a rule to allow unauthenticated access to anon_paths

# 2. Add a rule to allow authenticated access to auth_paths

# 3. Extend the rule to only allow requests with query strings that do not contain any bad words
