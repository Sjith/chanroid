package com.kr.rulubla.VideoUploader.Uploader;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

// 스트림 전송 버그 해결을 위한 별도의 스트림 객체
public class FlushedInputStream extends FilterInputStream {
	
	public FlushedInputStream(InputStream inputStream) {
		super(inputStream);
	}

	public long skip(long n) throws IOException {
		long totalBytesSkipped = 0L;
		while (totalBytesSkipped < n) {
			long bytesSkipped = in.skip(n - totalBytesSkipped);

			if (bytesSkipped == 0L) {
				int b = read();
				if (b < 0) {
					break;
				} else {
					bytesSkipped = 1;
				}
			}
			totalBytesSkipped += bytesSkipped;
		}
		return totalBytesSkipped;
	}
}