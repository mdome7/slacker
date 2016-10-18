package com.labs2160.slacker.api.response;

import java.io.InputStream;

/**
 * Binary data response to handle pretty much all other types of data.
 * This response fairly unrestricted and may require more consideration
 * during configuration so downstream workflow components are able to
 * handle the payload.
 */
final public class BinaryDataOutput extends SlackerOutput {

    /** unrecognized media types are recommended to be treated as this arbitrary binary data type */
    public final static String DEFAULT_MEDIA_TYPE = "application/octet-stream";

    private InputStream inputStream;

    /** a valid media type (see IETF RFC2046) */
    private String mediaType;

    public BinaryDataOutput(InputStream inputStream) {
        this(DEFAULT_MEDIA_TYPE, inputStream);
    }

    public BinaryDataOutput(String mediaType, InputStream inputStream) {
        if (mediaType == null) {
            throw new IllegalArgumentException("mediaType cannot be null");
        }
        if (inputStream == null) {
            throw new IllegalArgumentException("message cannot be null");
        }
        this.mediaType = mediaType;
        this.inputStream = inputStream;
    }

    public String getMediaType() {
        return this.mediaType;
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }
}
