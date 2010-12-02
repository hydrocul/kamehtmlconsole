package hydrocul.util;

import java.io.IOException;
import java.io.Writer;

public class ThreadLocalSwitchWriter extends Writer {
	
	private ThreadLocal<Writer> _src;
	
	public ThreadLocalSwitchWriter(final Writer defaultWriter){
		_src = new ThreadLocal<Writer>(){
			@Override
			protected Writer initialValue(){
				return defaultWriter;
			}
		};
	}
	
	public void setWriter(Writer writer){
		try {
			Writer oldWriter = _src.get();
			if(oldWriter!=null){
				oldWriter.flush();
			}
		} catch(IOException e){
			// nothing
		}
		_src.set(writer);
	}
	
	@Override
	public void write(char[] buf, int off, int len) throws IOException {
		Writer writer = _src.get();
		if(writer!=null){
			writer.write(buf, off, len);
			writer.flush();
		}
	}
	
	@Override
	public void flush() throws IOException {
		Writer writer = _src.get();
		if(writer!=null){
			writer.flush();
		}
	}
	
	@Override
	public void close() throws IOException {
		Writer writer = _src.get();
		if(writer!=null){
			writer.close();
			_src.set(null);
		}
	}
	
}
