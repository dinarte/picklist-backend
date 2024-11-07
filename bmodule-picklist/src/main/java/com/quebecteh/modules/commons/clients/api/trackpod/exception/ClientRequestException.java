package com.quebecteh.modules.commons.clients.api.trackpod.exception;

/**
 * Exception triggered when a request to an endpoint returns an error.
 * @author Dinarte Alves - dinarte@gmail.com
 * @since 1.0.0
 */

public class ClientRequestException extends RuntimeException{

    private static final long serialVersionUID = 1L;

	public ClientRequestException(int code, String url) {
        super(String.format(MessagesConstants.CLIENT_REQUEST_EXCEPTION, code, url));
    }

    public ClientRequestException(Throwable cause, String url) {
        super(String.format(MessagesConstants.CLIENT_REQUEST_EXCEPTION, cause.getClass().getSimpleName(), url), cause);
    }

}
