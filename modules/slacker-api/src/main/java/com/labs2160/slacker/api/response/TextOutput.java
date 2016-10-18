package com.labs2160.slacker.api.response;

/**
 * Most responses will use text response.
 * The Type attribute can be used by other workflow components
 * possibly do conditional processing of the data.
 *
 * Default Type is PLAIN if not specified.
 */
final public class TextOutput extends SlackerOutput {

    public enum Type {
        PLAIN, MARKDOWN, XML, JSON;

        public static Type getDefault() {
            return PLAIN;
        }
    }

    private final String message;

    private final Type type;

    public TextOutput(String message) {
        this(Type.getDefault(), message);
    }

    public TextOutput(Type type, String message) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        if (message == null) {
            throw new IllegalArgumentException("message cannot be null");
        }

        this.type = type;
        this.message = message;
    }

    public Type getType() {
        return this.type;
    }

    /**
     * Returns standard media types
     * see http://www.iana.org/assignments/media-types/media-types.xhtml
     * @return
     */
    public String getMediaType() {
        switch(type) {
            case PLAIN: return "text/plain";
            case MARKDOWN: return "text/markdown";
            case XML: return "text/xml";
            case JSON: return "application/json";
        }
        return null;
    }

    public String getMessage() {
        return this.message;
    }
}
