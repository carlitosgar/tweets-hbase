package master;

public class HBaseApp {
	
	public static void main(String[] args) {
		
		if(args.length < 1) {
			System.err.println("Invalid parameters");
		}
		
		switch(args[0]) {
		
		case "1": 
			//TODO
			System.err.println("To be implemented");
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
