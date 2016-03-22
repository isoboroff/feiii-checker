package inputCheck;
import java.io.File;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class MainRun {
	private final String t1 = "t1";
	private final String t2 = "t2";
	private final String t3 = "t3";
	private final String t4 = "t4";
	private final String d = "d";
	
	public static final int TASK1 = 1;
	public static final int TASK2 = 2;
	public static final int TASK3 = 3;
	public static final int TASK4 = 4;
	
	public static final String FFIEC = "FFIEC";
	public static final String SEC = "SEC";
	public static final String LEI = "LEI";
	
	public static final String FFIEC_ID = "FFIEC_IDRSSD";
	public static final String SEC_ID = "SEC_CIK";
	public static final String LEI_ID = "LEI_LEI";
	
	private final String ext = ".csv";
		
	private int TASK = 0;
	
	private String dataDir;
	private ArrayList<File> filesToCheck;
	
	public static void main(String[] args) {
				
		MainRun mr = new MainRun();
				
		mr.dataDir = null;
		
		boolean errorFound = mr.parseCmd(args);			
			
		if (errorFound)
			System.exit(1); // exit with error
		
		Cheker ch = new Cheker(mr.TASK, mr.dataDir, mr.filesToCheck);
		
		errorFound = ch.check();
				
		System.exit(errorFound ? 1 : 0); // exit with error if errors found
	}
	
	private boolean parseCmd(String[] args) {
		Options options = new Options();

		options.addOption("d", true, "The directory where FFIEC.csv, LEI.csv, and SEC.csv are located.");
		
		
		Option option = new Option("t1", "<ORG_FFIEC_LEI_TP_X.csv> [ORG_FFIEC_LEI_TN_X.csv]");
		option.setArgs(Option.UNLIMITED_VALUES);		
		options.addOption(option);
		
		option = new Option("t2", "<ORG_FFIEC_SEC_TP_X.csv> [ORG_FFIEC_SEC_TN_X.csv]");
		option.setArgs(Option.UNLIMITED_VALUES);		
		options.addOption(option);
		
		option = new Option("t3", "<ORG_FFIEC_LEI_SEC_TP_X.csv> [ORG_FFIEC_LEI_SEC_TN_X.csv");
		option.setArgs(Option.UNLIMITED_VALUES);		
		options.addOption(option);
		
		option = new Option("t4", "<ORG_LEI_SEC_TP_X.csv> [ORG_LEI_SEC_TN_X.csv");
		option.setArgs(Option.UNLIMITED_VALUES);		
		options.addOption(option);
		
		return processArgs(options, args);
	}
	
	private boolean processArgs(Options options, String[] args){
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			
			if (cmd.hasOption(t1))
				TASK = TASK1;
			else if (cmd.hasOption(t2))
				TASK = TASK2;
			else if (cmd.hasOption(t3))
				TASK = TASK3;
			else if (cmd.hasOption(t4))
				TASK = TASK4;
						
			if (args.length == 0){
				System.err.println("No arguments.");
				printUsage(options);
				return true;
			}
			else if (TASK == 0){
				System.err.println("No task argument (-t1|t2|t3|t4).");
				printUsage(options);
				return true;
			}
			else if (cmd.hasOption(d) && !isDataDirValid(cmd.getOptionValue(d))){
				printUsage(options);
				return true;
			}
			else if (!cmd.hasOption(d) && !isDataDirValid(null)){
				printUsage(options);
				return true;
			}			
			else if (args.length > 5 || args.length < 2){
				System.err.println("Wrong number of arguments.");
				printUsage(options);
				return true;
			}	
			
			ArrayList<File> files = this.getFilesToCheck(cmd);
			
			for (File f:files){
							
				if (!f.exists()){
					System.err.println("File doesn't not exist: "+f);
					printUsage(options);
					return true;
				}
									
			}
				
			boolean correctVersion = checkFileVersion(files);
			boolean correctFileName = checkFileName(files);
			boolean correctOrgID = checkOrgID(files);
			
			if (!correctFileName){
				System.err.println("Filename(s) don't satisfy naming rule: "+files);
				printUsage(options);
				return true;
			}
			
			if (!correctVersion){
				System.err.println("*_TP and *_TN are of different version: "+files);
				printUsage(options);
				return true;
			}
			
			if (!correctOrgID){
				System.err.println("*_TP and *_TN have different ORG ID: "+files);
				printUsage(options);
				return true;
			}
			
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	
	private boolean isDataDirValid(String dir){
		this.dataDir = dir;
		
		if (this.dataDir != null){
			File f = new File(this.dataDir);
			boolean exists = f.exists() && f.isDirectory();
			if (!exists){
				System.err.println("Data directory doesn't exists:\n"+this.dataDir);
				return false;
			}				
		}
		
		return areDataFilesValid();		
	}
	
	// It checks if  FFIEC.csv, LEI.csv, and SEC.csv exist in the data directory
	private boolean areDataFilesValid(){
		String dirPath = (this.dataDir == null) ? "" : this.dataDir+File.separator;
		
		boolean isValid = true;
		if (TASK == TASK1){
			File f = new File (dirPath+FFIEC+ext);
			isValid = isFileExist(f);			
			
			if (!isValid)
				return false;
			
			f = new File (dirPath+LEI+ext);
			isValid = isFileExist(f);
		}
		else if (TASK == TASK2){
			File f = new File (dirPath+FFIEC+ext);
			isValid = isFileExist(f);			
			
			if (!isValid)
				return false;
			
			f = new File (dirPath+SEC+ext);
			isValid = isFileExist(f);
		}
		else if (TASK == TASK3){
			File f = new File (dirPath+FFIEC+ext);
			
			isValid = isFileExist(f);			
			
			if (!isValid)
				return false;
			
			f = new File (dirPath+SEC+ext);
			isValid = isFileExist(f);
			
			if (!isValid)
				return false;
			
			f = new File (dirPath+LEI+ext);
			isValid = isFileExist(f);
		}
		else if (TASK == TASK4){
			File f = new File (dirPath+LEI+ext);
			isValid = isFileExist(f);			
			
			if (!isValid)
				return false;
			
			f = new File (dirPath+SEC+ext);
			isValid = isFileExist(f);
		}
			
		return isValid;
	}
	
	private boolean isFileExist(File f){
		if (!f.exists()){
			System.err.println("Data file doesn't exists:\n"+f);
			return false;
		}
		
		return true;
	}
	
	private boolean checkFileName(ArrayList<File> files){
		String check = "";
		if (TASK == TASK1)
			check = "_"+FFIEC+"_"+LEI+"_";
		else if (TASK == TASK2)
			check = "_"+FFIEC+"_"+SEC+"_";
		else if (TASK == TASK3)
			check = "_"+FFIEC+"_"+LEI+"_"+SEC+"_";
		else if (TASK == TASK4)
			check = "_"+LEI+"_"+SEC+"_";
		
		
		for (File fname:files){
			if (!fname.getName().toUpperCase().contains(check))
				return false;
		}
		
		return true;
		
	}
	
	private boolean checkFileVersion(ArrayList<File> files){
		if (files.size() == 1)
			return true;
		
		
		String name1 = files.get(0).getName();
		String name2 = files.get(1).getName();
		
		int index1 = name1.lastIndexOf("_")+1;
		int index2 = name1.lastIndexOf(".");
		
		String version1 = name1.substring(index1, index2).trim();
		
		index1 = name2.lastIndexOf("_")+1;
		index2 = name2.lastIndexOf(".");
		
		String version2 = name2.substring(index1, index2).trim();
				
		return version1.equalsIgnoreCase(version2);
	}
	
	private boolean checkOrgID(ArrayList<File> files){
		if (files.size() == 1)
			return true;
		
		String name1 = files.get(0).getName().split("_", 2)[0];
		String name2 = files.get(1).getName().split("_", 2)[0];
						
		return name1.equalsIgnoreCase(name2);
	}
	
	private void printUsage(Options options){
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(
				"java -jar Checker.jar [-d <dir>] -t1|t2|t3|t4 <ORG_*_TP_X.csv> [ORG_*_TN_X.scv]\n\n", options );	
				
	}
	
	// It returns the list of file(s) for the task. 
	private ArrayList<File> getFilesToCheck(CommandLine cmd){
		filesToCheck = new ArrayList<File>();
		
		String[] values = null; 
		
		if (cmd.hasOption(t1))
			values = cmd.getOptionValues(t1);
		else if (cmd.hasOption(t2))
			values = cmd.getOptionValues(t2);
		else if (cmd.hasOption(t3))
			values = cmd.getOptionValues(t3);
		else if (cmd.hasOption(t4))
			values = cmd.getOptionValues(t4);
		
		for (String s:values)
			filesToCheck.add(new File(s));
				
		return filesToCheck;
	}
}
