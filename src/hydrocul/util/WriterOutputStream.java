package hydrocul.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class WriterOutputStream extends OutputStream {
	
	private final Writer _writer;
	private final String _encoding;
	
	public WriterOutputStream(Writer writer, String encoding){
		_writer = writer;
		_encoding = encoding;
	}
	
	@Override
	public void write(int b) throws IOException {
      _writer.write(new String(new byte[]{(byte)b}, 0, 1, _encoding)); // TODO 厳密にはおかしい 
	}
	
	@Override
	public void write(byte b[], int off, int len) throws IOException {
		_writer.write(new String(b, off, len, _encoding)); // TODO 厳密にはおかしい 
	}
	
}
