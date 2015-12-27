package master.modes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import master.KeyGenerator;
import master.RankTable;

public class Mode4 {
	
	public static void execute(String[] args) {
		
		if(args.length != 2) {
			System.err.println("Invalid arguments for mode 4");
			return;
		}
		
		File dir = new File(args[1]);
		
		if(!dir.isDirectory() || !dir.canRead()) {
			System.err.println("Directory is not readable");
			return;
		}
		
		// Get files of the directory with .out extension
		File[] directoryListing = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".out");
		    }
		});
		
		if (directoryListing != null) {
			
			// First delete the table in case it already existed
			RankTable.deleteTable();
			
			// Create a new table
			RankTable.createTable();
			
			// Open the new hbase table
			try {
				RankTable.open();
			} catch (IOException e) {
				System.err.println("Could not open HBase Rank table!");
				e.printStackTrace();
				return;
			}
			
			// Load all the files inside that directory
			for (File file : directoryListing) {
				try {
					loadFile(file);
				} catch (IOException e) {
					System.err.println("Could not load file " + file.getName());
				}
		    }
			
			// Close hbase table
			RankTable.close();
			
		} else {
			System.err.println("Directory is not readable");
		}
		
		
	}
		
		
	private static void loadFile(File file) throws FileNotFoundException, IOException {
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	
		    	// Clear spaces
		    	line = line.replaceAll(" ", "");
		    	
		    	String[] splitted = line.split(",");
		    	
		    	if(splitted.length != 8) {
		    		System.err.println("Invalid line format in file " + file.getName());
		    		continue;
		    	}
		    	
		    	Long timestamp = new Long(splitted[0]);
		    	String lang = splitted[1].toLowerCase();
		    	
		    	byte[] key = KeyGenerator.generateKey(timestamp, lang);
		    	
				try {
					Method addEntry = RankTable.class.getMethod("addEntry", byte[].class, String.class, int.class,
							String.class, int.class, String.class, int.class);

					// Prepare the arguments
					Object[] arguments = new Object[7];
					arguments[0] = key;

					for (int x = 1; x < 7; x += 2) {
						arguments[x] = splitted[x + 1];
						arguments[x + 1] = Integer.parseInt(splitted[x + 2]);
					}

					// Execute the method
					addEntry.invoke(null, arguments);

				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
		    	
		    	
		    }
		}
		
	}
	
}
