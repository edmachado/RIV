package riv.util;

public class RivRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RivRuntimeException(String message) {
		super(message);
	}
	
	public RivRuntimeException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
}