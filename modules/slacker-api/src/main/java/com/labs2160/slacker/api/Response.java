package com.labs2160.slacker.api;

import java.io.InputStream;

/**
 * Simple response object.
 * One attached media can be associated with the response.
 * The processing of both response message and attached media
 * will be dependent on the Collectors and Endpoints.
 * (some Collectors and Endpoints are able to deliver both
 * pieces of data in the response while it does not make sense
 * for others)
 */
public class Response {

	private String message;

	private InputStream attachedMedia;

	private String attachedMediaType;

	public Response() {}

	public Response(String message, InputStream attachedMedia, String attachedMediaType) {
		this.message = message;
		this.attachedMedia = attachedMedia;
		this.attachedMediaType = attachedMediaType;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setAttachedMedia(InputStream attachedMedia) {
		this.attachedMedia = attachedMedia;
	}

	public void setAttachedMediaType(String attachedMediaType) {
		this.attachedMediaType = attachedMediaType;
	}

	public String getMessage() {
		return message;
	}

	public InputStream getAttachedMedia() {
		return attachedMedia;
	}

	public String getAttachedMediaType() {
		return attachedMediaType;
	}
}
