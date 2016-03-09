import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogWriter {
	private String logFile;
	
	private final String LOG_DATE_FORMAT = "EEE, MMM dd, yyyy, h:mm:ss a";

	private PrintStream out;

	private PrintStream err;
	
	public LogWriter(String logFile){
		this.logFile = logFile;
		
		if (!new File(this.logFile).getParentFile().exists())
			new File(this.logFile).getParentFile().mkdirs();
		
		setErrorStream();
	}
	
	public void close(){
		out.close();
        err.close();
	}
	
	/**
	 * 
	 * The method redirects STDOUT (System.out) and STDERR (System.err) 
	 * to the log file.
	 * 
	 */
	private void setErrorStream() {
		try {
			FileOutputStream logFile = new FileOutputStream(this.logFile);
			
			// Tee standard output
	        out = new PrintStream(logFile);	        
	        PrintStream tee = new CheckerOutputStream(System.out, out);
	    
	        SimpleDateFormat formatter = new SimpleDateFormat(LOG_DATE_FORMAT);	
			
	        tee.append(formatter.format(new Date()));
			tee.append("\n\n");
			
	        System.setOut(tee);
	    
	        // Tee standard error
	        err = new PrintStream(logFile);
	        tee = new CheckerOutputStream(System.err, err);
	    
	        System.setErr(tee);   
	        
		} catch (FileNotFoundException e) {
			System.out.println("Could not write to file: " + this.logFile);
		}
	}
	
	/**
	 * 
	 * Writes output to a log file plus the output stream
	 *	
	 */
	class CheckerOutputStream extends PrintStream
	{
		PrintStream out;
	    public CheckerOutputStream(PrintStream out1, PrintStream out2) {
	        super(out1, true);
	        this.out = out2;
	    }
	    public void write(byte buf[], int off, int len) {
	        try {        	
	            super.write(buf, off, len);            
	            out.write(buf, off, len);
	        } catch (Exception e) {
	        }
	    }
	    public void flush() {
	        super.flush();
	        out.flush();
	    }
	    
	    public void close()
	    {    	
	        out.flush();
	        super.flush();
	        super.close();
	        out.close();
	    }   
	}

}
