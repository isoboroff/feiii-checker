package inputCheck;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;


public class Cheker {
	private String logDir = "logs//";
	private Task task;
	private LogWriter log;
	private ArrayList<String> TP_IDs;
	private ArrayList<File> filesToCheck;
	
	
	public Cheker(int TASK, String dataDir, ArrayList<File> files) {
		this.filesToCheck = files;
		
		task = new Task(dataDir); // pass the data directory
		
		if (TASK == MainRun.TASK1){
			task.setName1(MainRun.FFIEC);
			task.setName2(MainRun.LEI);
			task.setIDcolName1(MainRun.FFIEC_ID);
			task.setIDcolName2(MainRun.LEI_ID);			
		}
		else if (TASK == MainRun.TASK2){
			task.setName1(MainRun.FFIEC);
			task.setName2(MainRun.SEC);
			task.setIDcolName1(MainRun.FFIEC_ID);
			task.setIDcolName2(MainRun.SEC_ID);
		}
		else if (TASK == MainRun.TASK3){			
			task.setName1(MainRun.FFIEC);
			task.setName2(MainRun.LEI);
			task.setName3(MainRun.SEC);
			task.setIDcolName1(MainRun.FFIEC_ID);
			task.setIDcolName2(MainRun.LEI_ID);
			task.setIDcolName3(MainRun.SEC_ID);
		}
		else if (TASK == MainRun.TASK4){			
			task.setName1(MainRun.LEI);
			task.setName2(MainRun.SEC);
			task.setIDcolName1(MainRun.LEI_ID);
			task.setIDcolName2(MainRun.SEC_ID);
		}
			
	}
	
	public boolean check(){
		return this.check(this.filesToCheck);
	}
	
	private boolean check(ArrayList<File> files){
		
		boolean errorFound = false;
		
		for (File f:files){
			if (isTPfile(f)){
				System.out.println("\n***Start checking TP file: "+f);
				openLog(f);
				errorFound = checkTP(f);
				closeLog();
				System.out.println("End checking TP file: "+f);
			}
			
			try {
				// waiting is just to have clean console printout. 
				// Since System.out and System.err are redirected, console printout lines 
				// can be mixed up.
				Thread.sleep(50);				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (isTNfile(f)){
				System.out.println("\n***Start checking TN file: "+f);
				openLog(f);				
				errorFound = checkTN(f);
				closeLog();
				System.out.println("End checking TN file: "+f);
			}
		}
		
		return errorFound;
	}
	
	private boolean isTPfile(File f){
		return f.getName().toUpperCase().contains("_TP");
	}
	
	private boolean isTNfile(File f){
		return f.getName().toUpperCase().contains("_TN");
	}
	
	private boolean checkTN(File file){
		TreeSet<String> addSuccess = new TreeSet<String>();
		boolean errorFound = false;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			String line = br.readLine(); // read first line; it must be header;
			
			if (!line.trim().equalsIgnoreCase(task.getIDcolName1())){
				System.err.println("Warning: possibly no header");
				errorFound = true;
			}
			
			int lineCount = 2;
			
			// read the rest of the file
			while((line=br.readLine()) != null){
				line = line.trim();			
				
				if (line.isEmpty()){
					System.err.println("Warning on line "+lineCount+": empty line");
					errorFound = true;
				}
				else if (!addSuccess.add(line)){ // the add() returns false if such item exists
					System.err.println("Error on line "+lineCount+": duplicate entry -- "+line);
					errorFound = true;
				}
				else if (!this.task.getID_list1().contains(line)){
					System.err.println("Error on line "+lineCount+": ID not found in "+task.getName1()+" -- "+line);
					errorFound = true;
				}
				else if (TP_IDs != null && TP_IDs.contains(line)){
					System.err.println("Error on line "+lineCount+": ID is present in both *_TP and *_TN files -- "+line);
					errorFound = true;
				}
				
				lineCount++;
			}
			
			br.close();
			
			if (!errorFound)
				System.out.println("Success! No errors found.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return errorFound;
	}

	private boolean checkTP(File file){
		TreeSet<String> addSuccess = new TreeSet<String>();
		TP_IDs = new ArrayList<String>();
		
		boolean errorFound = false;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			String line = br.readLine(); // read first line; it must be header;
			String[] tokens = cleanLine(line).split(",");
			
			// check header
			if (!tokens[0].equalsIgnoreCase(task.getIDcolName1()) || !tokens[1].equalsIgnoreCase(task.getIDcolName2())){
				System.err.println("Warning: no or wrong header");
				errorFound = false;
			}
			
			int lineCount = 2;
			
			// read the rest of the file
			while((line=br.readLine()) != null){
				line = cleanLine(line);		
				tokens = line.split(",");
				String ID1 = null;
				String ID2 = null;
				String ID3 = null;
				
				int elements = 0;
				
				for (String s:tokens){
					if (!s.trim().isEmpty())
						elements++;
				}
				
				if (elements == 2){ 
					ID1 = tokens[0].trim();
					ID2 = tokens[1].trim();
				}
				else if (elements == 3){ 
					ID1 = tokens[0].trim();
					ID2 = tokens[1].trim();
					ID3 = tokens[2].trim();
				}
				
								
				if (line.isEmpty()){
					System.err.println("Warning on line "+lineCount+": empty line");
					errorFound = true;
				}
				else if (elements != task.getElemNum()){
					System.err.println("Error on line "+lineCount+": wrong number of elements -- "+line);
					errorFound = true;
				}
				else if (!addSuccess.add(line)){ // add returns false if such item exists
					System.err.println("Error on line "+lineCount+": duplicate entry -- "+line);
					errorFound = true;
				}				
				else{
					if (!this.task.getID_list1().contains(ID1)){
						System.err.println("Error on line "+lineCount+": ID not found in "+task.getName1()+" -- "+ID1);
						errorFound = true;
					}
					
					if (!this.task.getID_list2().contains(ID2)){
						System.err.println("Error on line "+lineCount+": ID not found in "+task.getName2()+" -- "+ID2);
						errorFound = true;
					}
					
					if (this.task.getID_list3() != null && !this.task.getID_list3().contains(ID3)){
						System.err.println("Error on line "+lineCount+": ID not found in "+task.getName3()+" -- "+ID3);
						errorFound = true;
					}
				}
				
				TP_IDs.add(ID1);
				
				lineCount++;
			}
			
			br.close();
			if (!errorFound)
				System.out.println("Success! No errors found.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return errorFound;
	}
	
	// remove whitespaces 
	private String cleanLine(String line){
		return line.trim().replaceAll("\\s+", "");
	}
	
	private void openLog(File file) {				
		log = new LogWriter(logDir+file+".log");		
	}
	
	private void closeLog() {				
		log.close();		
	}

}
