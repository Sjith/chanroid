package kr.co.chan.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Adapter to change blocking input into polled input with timeout.
 * 
 * @author Filip Larsen
 */
public class PolledInputStream extends InputStream {
	
	public class PolledTimeoutException extends IOException	 {

		public PolledTimeoutException(String string) {
			super(string);
		}

		private static final long serialVersionUID = 1L;
		
	}
	
	private InputStream input;
	private long timeout;

	public PolledInputStream(InputStream input, long timeout) {
		this.input = input;
		this.timeout = timeout;
	}

	public int available() throws IOException {
		return input.available();
	}

	public void close() throws IOException {
		input.close();
	}

	public void mark(int readlimit) {
		input.mark(readlimit);
	}

	public boolean markSupported() {
		return input.markSupported();
	}

	public int read() throws IOException {
		waitForAvailable();
		return input.read();
	}

	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	public int read(byte[] b, int off, int len) throws IOException {
		waitForAvailable();
		int n = available();
		return input.read(b, off, Math.min(len, n));
	}

	public void reset() throws IOException {
		input.reset();
	}

	public long skip(long n) throws IOException {
		return input.skip(n);
	}

	private void sleep(long time) {
		try {
			Thread.sleep(Math.max(0, time));
		} catch (InterruptedException ignore) {
		}
	}

	private void waitForAvailable() throws IOException {
		long until = System.currentTimeMillis() + timeout;
		while (available() == 0) {
			if (System.currentTimeMillis() > until)
				throw new PolledTimeoutException("input timed out");
			sleep(100);
		}
	}
}