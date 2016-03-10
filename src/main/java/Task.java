package inputCheck;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * @author ezotkina
 * 
 * Task represents the challenge task from https://ir.nist.gov/dsfin/guidelines.html.
 * It contains the description of up to three collections since Task 3 (FFIEC -> LEI, SEC) 
 * requires the comparison among three sets.
 * Other tasks require the allignment of two sets; in this case the third variables (name2, ID_list3, etc.) will be null.
 *
 */
public class Task {
	
	private String name1;
	private String name2;
	private String name3;
	
	private String IDcolName1;
	private String IDcolName2;
	private String IDcolName3;
	
	private ArrayList<String> ID_list1;
	private ArrayList<String> ID_list2;
	private ArrayList<String> ID_list3;
	
	private File dataDir;
	
	
	public Task(File file) {
		this.dataDir = file;
		
	}

	public ArrayList<String> getID_list1() {
		if (ID_list1 == null)
			this.ID_list1  = getIDList(this.name1+".csv", this.getIDCol(this.name1));
		return this.ID_list1;
	}
	
	public ArrayList<String> getID_list2() {
		if (ID_list2 == null)
			this.ID_list2  = getIDList(this.name2+".csv", this.getIDCol(this.name2));
		return this.ID_list2;
	}

	
	public ArrayList<String> getID_list3() {
		if (this.name3 == null)
			return null;
		
		if (ID_list3 == null)
			this.ID_list3  = getIDList(this.name3+".csv", this.getIDCol(this.name3));
		return this.ID_list3;
	}
	

	public String getName1() {
		return name1;
	}

	public void setName1(String name1) {
		this.name1 = name1;	
	}

	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	public String getName3() {
		return name3;
	}

	public void setName3(String name3) {
		this.name3 = name3;
	}

	public String getIDcolName1() {
		return IDcolName1;
	}

	public void setIDcolName1(String iDcolName1) {
		IDcolName1 = iDcolName1;
	}

	public String getIDcolName2() {
		return IDcolName2;
	}

	public void setIDcolName2(String iDcolName2) {
		IDcolName2 = iDcolName2;
	}

	public String getIDcolName3() {
		return IDcolName3;
	}

	public void setIDcolName3(String iDcolName3) {
		IDcolName3 = iDcolName3;
	}

	// If this is task3, input must have three columns; otherwise two.
	public int getElemNum(){
		if (this.name3 == null)
			return 2;
		else
			return 3;
	}
	
	/**
	 * FFIEC and SEC have ID in the first column;
	 * LEI has ID in the third column;
	 */
	private int getIDCol(String name){
		if (name.equalsIgnoreCase("LEI"))
			return 2;
		else
			return 0;
	}

	/**
	 * For FFIEC and SEC, split line into two tokens; ID is the first token.
	 * For LEI, split line into four tokens; ID is the third token.
	 */
	private ArrayList<String> getIDList(String file, int IDColumn){
		File dataFile = new File(file);
		
		if (this.dataDir != null)
			dataFile = new File(this.dataDir.getPath()+File.separator+file);
		
		if (!dataFile.exists()){
			System.err.println("Error: file doesn't exist: "+file);
			System.err.println("FFIEC.csv, LEI.csv, and SEC.csv must be in the same directory with the checker.");
			System.exit(1);
		}
		
		ArrayList<String> list = new ArrayList<String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(dataFile));
			
			String line;
			
			while((line=br.readLine()) != null){
				int limit = (IDColumn == 2) ? 4 : 2;
				String[] tokens = line.trim().split(",", limit);  
				list.add(tokens[IDColumn]);
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
}
