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
	
	public static void main(String[] args) {
		MainRun mr = new MainRun();
				
		mr.processArgs(args);			
			
		new Cheker(mr.TASK, mr.getArgFiles(args));		
	}
	
	private void processArgs(String[] argsStr){
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
		
		checkArgs(args);
		
	}

	private void checkArgs(ArrayList<String> args){
		boolean exit = false;
		
		if (args.size() == 0){
			System.err.println("No arguments.");
			exit = true;
		}
		else if (TASK == 0){
			System.err.println("No task argument (-t1|t2|t3|t4).");
			exit = true;
		}
		else if (args.size() > 3 || args.size() < 2){
			System.err.println("Wrong number of arguments.");
			exit = true;
		}		
		else if (args.size() <= 3){
			ArrayList<String> files = new ArrayList<String>(2);
			
			for (String arg:args){
				if (arg.startsWith("-t"))
					continue;
				
				File f = new File(arg);
				if (!f.exists()){
					System.err.println("File doesn't not exist: "+f);
					exit = true;
				}
				
				files.add(f.getName());				
			}
			
			boolean correctVersion = checkFileVersion(files);
			boolean correctFileName = checkFileName(files);
			boolean correctOrgID = checkOrgID(files);
			
			if (!correctFileName){
				System.err.println("Filename(s) don't satisfy naming rule: "+files);
				exit = true;
			}
			
			if (!correctVersion){
				System.err.println("*_TP and *_TN are of different version: "+files);
				exit = true;
			}
			
			if (!correctOrgID){
				System.err.println("*_TP and *_TN have different ORG ID: "+files);
				exit = true;
			}
			
		}
		
		if (exit){
			printUsage();			
			System.exit(0);
		}
		
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
				"java -jar Checker.jar -t1|t2|t3|t4 <ORG_*_TP_X.csv> [ORG_*_TN_X.scv]\n\n" +
				"Task 1: -t1 <ORG_FFIEC_LEI_TP_X.csv> [ORG_FFIEC_LEI_TN_X.csv]\n" + 
				"Task 2: -t2 <ORG_FFIEC_SEC_TP_X.csv> [ORG_FFIEC_SEC_TN_X.csv]\n" +
				"Task 3: -t3 <ORG_FFIEC_LEI_SEC_TP_X.csv> [ORG_FFIEC_LEI_SEC_TN_X.csv]\n" +
				"Task 4: -t4 <ORG_LEI_SEC_TP_X.csv> [ORG_LEI_SEC_TN_X.csv]\n");
				
	}
	
	private ArrayList<File> getArgFiles(String[] args){
		ArrayList<File> files = new ArrayList<File>(2);
		
		for (String arg:args){
			if (arg.contains("-t"))
				continue;
			
			files.add(new File(arg));
		}
		
		return files;
	}
}
