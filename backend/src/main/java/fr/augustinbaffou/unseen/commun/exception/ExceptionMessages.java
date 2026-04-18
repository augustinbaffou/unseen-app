package fr.augustinbaffou.unseen.commun.exception;

public final class ExceptionMessages {

    private ExceptionMessages() {}

    // ── Labels HTTP (champ "error" du ErrorResponse) ──────────────────────────

    public static final String HTTP_NOT_FOUND             = "Not Found";
    public static final String HTTP_BAD_REQUEST           = "Bad Request";
    public static final String HTTP_UNAUTHORIZED          = "Unauthorized";
    public static final String HTTP_FORBIDDEN             = "Forbidden";
    public static final String HTTP_CONFLICT              = "Conflict";
    public static final String HTTP_INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String HTTP_BAD_GATEWAY           = "Bad Gateway";

    // ── Messages génériques ───────────────────────────────────────────────────

    public static final String UNEXPECTED_ERROR       = "An unexpected error occurred";
    public static final String TYPE_MISMATCH          = "Invalid value '%s' for parameter '%s'";
    public static final String RESOURCE_NOT_FOUND      = "%s not found with %s: '%s'";
    public static final String RESOURCE_ALREADY_EXISTS = "%s already exists with %s: '%s'";
    public static final String EXTERNAL_RATE_LIMITED   = "Overpass API rate limit reached, please retry later";
    public static final String EXTERNAL_UNAVAILABLE    = "Overpass API is temporarily unavailable (HTTP %d)";
}
