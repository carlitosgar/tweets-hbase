package master;

import master.modes.Mode1;
import master.modes.Mode4;

public class HBaseApp {
	
	public static void main(String[] args) {
		
		if(args.length < 1) {
			System.err.println("Invalid parameters");
			return;
		}
		
		switch(args[0]) {
		
		case "1": 
			Mode1.execute(args);
			break;
			
		case "2": 
			//TODO
			System.err.println("To be implemented");
			break;
			
		case "3":
			//TODO
			System.err.println("To be implemented");
			break;
			
		case "4":
			Mode4.execute(args);
			break;
		
		default:
			System.err.println("Invalid mode parameter");
			break;
		
		}
		
	}

}
