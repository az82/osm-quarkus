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
    glob.match(anon_paths[_], ["/"], input.path)
}

allow {
    input.principal != null
    glob.match(auth_paths[_], ["/"], input.path)
}