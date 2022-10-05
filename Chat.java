import java.util.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.ServerSocket;



public class Chat {
    
    	// main() start :
	public static void main(String[] args) {
	    Chat chatApplication = new Chat();
	    chatApplication.startChat();
	}
	// main() end .
	
	private InetAddress myIPAddress;
	
	// help() start :
	private void help() { // TODO: Type descriptions later !
	    
	    System.out.println("--------------------------------------");
	    System.out.println("~ Available Options , Command Manual ~");
	    System.out.println("/help");
	    System.out.println("/myip");
	    System.out.println("/myport");
	    System.out.println("/connect");
	    System.out.println("/list");
	    System.out.println("/terminate");
	    System.out.println("/send");
	    System.out.println("/exit");
	    System.out.println("--------------------------------------");
	    
	}
	// help() end .
	
	// getmyIPAddress() start :
	private String getmyIPAddress() {
	    return myIPAddress.getHostAddress();
	}
	// getmyIPAddress() end .
	
	// startChat() start :
	private void startChat() {
	    
	    Scanner scanner = new Scanner(System.in);
	    
	    try {
	        
	        myIPAddress = InetAddress.getLocalHost();
	        //...
	        //...
	        
	        while(true) {
	            System.out.println("Enter a command : ");
	            String userInput = scanner.nextLine();
	            if(userInput.equals("/help")) {
	                help();
	            } else if(userInput.equals("/myip")) {
	                System.out.println(getmyIPAddress());
	            } else if(userInput.equals("/myport")) {
	                System.out.println("/myport"); // TODO: Fix later .
	            } else if(userInput.equals("/connect")) {
	                System.out.println("/connect"); // TODO: Fix later .
	            } else if(userInput.equals("/list")) {
	                System.out.println("/list"); // TODO: Fix later .
	            } else if(userInput.equals("/terminate")) {
	                System.out.println("/terminate"); // TODO: Fix later .
	            } else if(userInput.equals("/send")) {
	                System.out.println("/send"); // TODO: Fix later .
	            } else if(userInput.equals("/exit")) {
	                System.out.println("Closing Connections...\nChat Successfully Exited.");
	                // closeAll() ...
	                System.exit(0);
	            } else {
	                System.out.println("Error : invalid command , please try again...");
	            }
	        }
	        
	    } catch(UnknownHostException e) {
	        e.printStackTrace();
	    } finally {
	        if(scanner != null) {
	            scanner.close();
	        }
	        // closeAll() ...
	    }
	    
	}
	// startChat() end .
	public class Server implements Closeable{
	
		List<Client> listOfClients = new ArrayList<Client>();
		@Override
		public run(int port){
			ServerSocket servSock = new ServerSocket(port);
			Socket sock = servSock.accept("Connected");
		} 
	}
	
	
}



