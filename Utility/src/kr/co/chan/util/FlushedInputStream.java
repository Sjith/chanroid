package kr.co.chan.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 2.2부터는 쓸 필요 없음
 * @author CINEPOX
 *
 */
public class FlushedInputStream extends FilterInputStream {
	
    public FlushedInputStream(InputStream inputStream) {
        super(inputStream);
    }

    @Override
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