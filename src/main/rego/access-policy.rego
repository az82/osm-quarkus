package http.authz

# Default: Only authorized access
auth_paths := [
    "**",
    ]

anon_paths := [
    "/",
    "/favicon.ico",
    "/_renarde/security/github-success"
    ]

allow {
    # Allow unauthenticated access to pre-defined paths
    glob.match(anon_paths[_], ["/"], input.path)
}

allow {
    # Allow authenticated access to pre-defined paths
    input.principal != null
    glob.match(auth_paths[_], ["/"], input.path)

    # Disallow queries that contain the string "secret" in any case
    not contains(lower(input.query), "secret")
}
