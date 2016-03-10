package inputCheck;
import java.io.File;
import java.util.ArrayList;


public class MainRun {
	private final String t1 = "-t1";
	private final String t2 = "-t2";
	private final String t3 = "-t3";
	private final String t4 = "-t4";
	
	public static final int TASK1 = 1;
	public static final int TASK2 = 2;
	public static final int TASK3 = 3;
	public static final int TASK4 = 4;
		
	private int TASK = 0;
	
	private String dataDir;
	
	public static void main(String[] args) {
				
		MainRun mr = new MainRun();
				
		mr.dataDir = null;
		
		boolean errorFound = mr.processArgs(args);			
			
		if (errorFound)
			System.exit(1); // exit with error
		
		Cheker ch = new Cheker(mr.TASK, mr.getArgFiles(args));
		
		errorFound = ch.check();
				
		System.exit(errorFound ? 1 : 0); // exit with error if errors found
	}
	
	private boolean processArgs(String[] argsStr){
		ArrayList<String> args = new ArrayList<String>();
		
		for (String s:argsStr)
			args.add(s);
		
		if (args.contains(t1))
			TASK = TASK1;
		else if (args.contains(t2))
			TASK = TASK2;
		else if (args.contains(t3))
			TASK = TASK3;
		else if (args.contains(t4))
			TASK = TASK4;
		
		return checkArgs(args);
		
	}

	private boolean checkArgs(ArrayList<String> args){
		boolean errorFound = false;
		
		if (args.size() == 0){
			System.err.println("No arguments.");
			errorFound = true;
		}
		else if (!isDataDirValid(args))
			errorFound = true;
		else if (TASK == 0){
			System.err.println("No task argument (-t1|t2|t3|t4).");
			errorFound = true;
		}
		else if (args.size() > 5 || args.size() < 2){
			System.err.println("Wrong number of arguments.");
			errorFound = true;
		}		
		else if (args.size() <= 3){
			ArrayList<String> files = new ArrayList<String>(2);
			
			for (String arg:args){
				if (arg.startsWith("-t"))
					continue;
				
				File f = new File(arg);
				if (!f.exists()){
					System.err.println("File doesn't not exist: "+f);
					errorFound = true;
				}
				
				files.add(f.getName());				
			}
			
			boolean correctVersion = checkFileVersion(files);
			boolean correctFileName = checkFileName(files);
			boolean correctOrgID = checkOrgID(files);
			
			if (!correctFileName){
				System.err.println("Filename(s) don't satisfy naming rule: "+files);
				errorFound = true;
			}
			
			if (!correctVersion){
				System.err.println("*_TP and *_TN are of different version: "+files);
				errorFound = true;
			}
			
			if (!correctOrgID){
				System.err.println("*_TP and *_TN have different ORG ID: "+files);
				errorFound = true;
			}
			
		}
		
		if (errorFound)
			printUsage();
			
		
		
		return errorFound;
	}
	private boolean isDataDirValid(ArrayList<String> args){
		for (int i=0; i<args.size(); i++){
			if (args.get(i).contains("-d")){
				this.dataDir = args.get(i+1);
				break;
			}
		}
		
		if (this.dataDir != null){
			File f = new File(this.dataDir);
			boolean exists = f.exists() && f.isDirectory();
			if (!exists)
				System.err.println("Data directory doesn't exists:\n"+this.dataDir);
			
			return exists;
		}
		else
			return true;			
	}
	
	private boolean checkFileName(ArrayList<String> files){
		String check = "";
		if (TASK == TASK1)
			check = "_FFIEC_LEI_";
		else if (TASK == TASK2)
			check = "_FFIEC_SEC_";
		else if (TASK == TASK3)
			check = "_FFIEC_LEI_SEC_";
		else if (TASK == TASK4)
			check = "_LEI_SEC_";
		
		
		for (String fname:files){
			if (!fname.toUpperCase().contains(check))
				return false;
		}
		
		return true;
		
	}
	
	private boolean checkFileVersion(ArrayList<String> files){
		if (files.size() == 1)
			return true;
		
		String name1 = files.get(0);
		String name2 = files.get(1);
		
		int index1 = name1.lastIndexOf("_")+1;
		int index2 = name1.lastIndexOf(".");
		
		String version1 = name1.substring(index1, index2).trim();
		
		index1 = name2.lastIndexOf("_")+1;
		index2 = name2.lastIndexOf(".");
		
		String version2 = name2.substring(index1, index2).trim();
				
		return version1.equalsIgnoreCase(version2);
	}
	
	private boolean checkOrgID(ArrayList<String> files){
		if (files.size() == 1)
			return true;
		
		String name1 = files.get(0).split("_", 2)[0];
		String name2 = files.get(1).split("_", 2)[0];
						
		return name1.equalsIgnoreCase(name2);
	}
	
	private void printUsage(){
		System.out.println("Usage:\n" +
				"java -jar Checker.jar [-d <dir>] -t1|t2|t3|t4 <ORG_*_TP_X.csv> [ORG_*_TN_X.scv]\n\n" +
				"Task 1: -d <dir> -t1 <ORG_FFIEC_LEI_TP_X.csv> [ORG_FFIEC_LEI_TN_X.csv]\n" + 
				"Task 2: -d <dir> -t2 <ORG_FFIEC_SEC_TP_X.csv> [ORG_FFIEC_SEC_TN_X.csv]\n" +
				"Task 3: -d <dir> -t3 <ORG_FFIEC_LEI_SEC_TP_X.csv> [ORG_FFIEC_LEI_SEC_TN_X.csv]\n" +
				"Task 4: -d <dir> -t4 <ORG_LEI_SEC_TP_X.csv> [ORG_LEI_SEC_TN_X.csv]\n" +
				"-d <dir>: the directory where FFIEC.csv, LEI.csv, and SEC.csv are located.\n");
				
	}
	
	private ArrayList<File> getArgFiles(String[] args){
		ArrayList<File> files = new ArrayList<File>(2);
		
		for (int i=0; i<args.length; i++){
			if (args[i].trim().equalsIgnoreCase("-d"))
				files.add(0, new File(args[i+1])); // first file in the list is the data directory			
			else if (args[i].trim().equalsIgnoreCase("-t"))
				files.add(new File(args[i+1]));			
		}
		
		return files;
	}
}
