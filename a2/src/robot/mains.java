package robot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class mains {
	
	//Output file name
	public static String outname = "";
	
	//Problem type
	public static String type = "";
	
	//Initial config
	public static List<Double> initcon;
	
	//Goal config
	public static List<Double> goalcon;
	
	//Obstacles
	public static List<Double[]> obstacles;
	
	public static void main (String[] args) {
		//Argument read in, checks for correct num of arguments
		if (args.length == 2){
			//Reads files and sets output file name if correct no of args
			outname = args[1];
			initLists();
			readFiles(args[0]);
		}else{
			//Returns error message and exits if incorrect number of arguments
			System.out.println("Please specify the correct file names");
			System.exit(0);
		}
		
		dataPrint();
		/*
		 Problem solving code here
		 */
	}
	
	//Reads files in
		public static void readFiles(String inname) {
			try {
				File file = new File(inname);
				BufferedReader reader = new BufferedReader(new FileReader(file));
				System.out.println("\nFile Loaded!");
			    String line = null;
			    
			    //Line Count
			    int count = 1;
			    
		    	while ((line = reader.readLine()) != null) {
			    	String[] temp = line.split("\\s+");
				    
			    	//Switch function handles lines based on which line reader is at
			    	switch(count) {
					    case (1) :
					    	//Problem type
					    	type = temp[0];
					    	break;
					    case (2) :
					    	//Initial configuration
					    	for(String c: temp) {
					    		initcon.add(Double.parseDouble(c));
					    	}
					    	break;
					    case (3) :
					    	//Goal configuration
					    	for(String c: temp) {
					    		goalcon.add(Double.parseDouble(c));
					    	}
					    	break;
					    case (4) :
					    	//Obstacles
					    	int numobs = Integer.parseInt(temp[0]);
					    	
					    	for(int i=0; i<numobs; i++) {
					    		if ((line = reader.readLine()) != null) {
					    			obstacles.add(obstacleReader(line.split("\\s+")));
					    		} else {
					    			System.out.println("File format error.");
					    			System.exit(0);
					    		}
					    		
					    	}
					    	break;
				    }
			    	//Increments line count
				    count ++;
			    }
			    reader.close();
			    
		    	
			} catch (IOException x) {
			    System.err.format("IOException: %s%n", x);
			}
		}
		
		//Converts a a line for an obstacle in the file to doubles
		public static Double[] obstacleReader(String[] x) {
			Double[] result = new Double[4];
			
			for (int i=0; i < 4; i++) {
				result[i] = Double.parseDouble(x[i]);
			}
			return result;
		}
		
		//Prints all read-in data for testing purposes
		public static void dataPrint() {
			//Problem Type (Line 1 in file)
			System.out.println("Problem type: " + type);
			
			//Initial Configuration (Line 2 in file)
			System.out.print("Initial Configuration: ");
			for (Double d: initcon) {
				System.out.print(d + " ");
			}
			System.out.println();
			
			//Final Configuration (Line 3 in file)
			System.out.print("Final Configuration: ");
			for (Double d: goalcon) {
				System.out.print(d + " ");
			}
			System.out.println();
			
			//Obstacles (Line 4 to [4 + value in line 4]  in file)
			System.out.println("Number of Obstacles: " + obstacles.size());
			for (int i = 0; i<obstacles.size(); i++) {
				System.out.print("Obstacle " + (i+1) + ": ");
				for(Double e: obstacles.get(i)) {
					System.out.print(e + " ");
				}
				System.out.println();
			}
		}
		
		//Initializes public lists
		public static void initLists() {
			initcon = new ArrayList<Double>();
			goalcon = new ArrayList<Double>();
			obstacles = new ArrayList<Double[]>();
		}
}
