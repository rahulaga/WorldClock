package com.irahul.worldclock;
/**
 * Top level exception wrapper
 * @author rahul
 *
 */
@SuppressWarnings("serial")
public class WorldClockException extends RuntimeException {

	public WorldClockException() {
		super();
	}

	public WorldClockException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public WorldClockException(String detailMessage) {
		super(detailMessage);
	}

	public WorldClockException(Throwable throwable) {
		super(throwable);
	}
}
