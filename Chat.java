import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.net.ServerSocket;
import java.net.Socket;



public class Chat {
	
	// main() start :
	public static void main(String[] args) throws IOException {
	    if(args != null && args.length > 0) {
	        try {
	            int listenPort = Integer.parseInt(args[0]);
	            Chat chatApplication = new Chat(listenPort);
				
	            chatApplication.startChat();
	        } catch(NumberFormatException nfe) {
	            System.out.println("Invalid argument for port...");
	        }
	    } else {
	        System.out.println("Invalid Argument : run using 'java Chat <PortNumber>'");
	    }
	}
	// main() end .
	
	private InetAddress myIPAddress;
	private int myPort;
	private Map<Integer, Room> roomsHosts = new TreeMap<>();
	private int clientCounter = 1;
	private Server serv;
	
	// Constructor start :
	private Chat(int myPort) {
	    this.myPort = myPort;
	}
	// Constructor end .
	
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
	
	// getmyPort() start :
	private int getmyPort() {
	    return myPort;
	}
	// getmyPort() end .
	
	// connect() start :
	private void connect(String[] cmdArgs) {
	    
	    if(cmdArgs != null && cmdArgs.length == 3) {
	        try {
	            InetAddress remoteAddress = InetAddress.getByName(cmdArgs[1]);
	            int remotePort = Integer.parseInt(cmdArgs[2]);
	            System.out.println("Connecting to " +remoteAddress+ " on port : " +remotePort+ " .");
	            Room roomHost = new Room(remoteAddress, remotePort);
	            if(roomHost.connected()) {
	                roomsHosts.put(clientCounter, roomHost);
	                System.out.println("Successfully Connected , Client ID : " + clientCounter++ + " .");
	            } else {
	                System.out.println("Error : connection unsuccessful , please try again...");
	            }
	        } catch(NumberFormatException nfe) {
	            System.out.println("Error : connection unsuccessful (invalid remote host port) , please try again...");
	        } catch(UnknownHostException e) {
	            System.out.println("Error : connection unsuccessful (invalid remote host address) , please try again...");
	        }
	    } else {
	        System.out.println("Error : the command format is invalid , please try '/connect <Destination> <PortNumber>' ...");
	    }
	    
	}
	// connect() end .
	
	// list() start :
	private void list() {
	    
	    System.out.println("ID\tIP ADDRESS\tPORT");
	    
	    if(roomsHosts.isEmpty()) {
	        System.out.println("There are no rooms available...");
	    } else {
	        for(Integer id : roomsHosts.keySet()) {
	            Room roomHost = roomsHosts.get(id);
	            System.out.println(id +"\t"+ roomHost.toString());
	        }
	    }
	    
	    //...
	    
	}
	// list() end .
	
	// terminate() start :
	private void terminate(String[] cmdArgs) {
	    
	    if(cmdArgs != null) {
	        System.out.println("Termination of connection id " +cmdArgs[1]+ " in progress...");
	        try {
	            int id = Integer.parseInt(cmdArgs[1]);
	            if(roomsHosts.containsKey(id) == false) {
	                System.out.println("Error : connection id invalid , termination unsuccessful , please try again...");
	                return;
	            }
	            Room roomHost = roomsHosts.get(id);
	            boolean closed = !roomHost.closeCon();
	            if(closed) {
	                System.out.println("Connection ID " +id+ " Successfully Terminated.");
	                roomsHosts.remove(id);
	            }
	        } catch(NumberFormatException nfe) {
	            System.out.println("Error : connection id invalid , termination unsuccessful , please try again...");
	        }
	    } else {
	        System.out.println("Error : the command format is invalid , please try '/terminate <ConnectionID>' ...");
	    }
	    
	}
	// terminate() end .
	
	// send() start :
	private void send(String[] cmdArgs) {
	    
	    if(cmdArgs.length > 2) {
	        try {
	            int id = Integer.parseInt(cmdArgs[1]);
	            Room roomHost = roomsHosts.get(id);
	            System.out.println("ID : " + roomsHosts.get(id));
	            if(roomHost != null) {
	                StringBuilder message = new StringBuilder();
	                for(int i = 2; i < cmdArgs.length; i++) {
	                    message.append(cmdArgs[i]);
	                    message.append(" ");
	                }
	                roomHost.send(message.toString());
	                System.out.println("Message Successfully Sent.");
	            } else {
	                System.out.println("Error : no available connection with the connection id given , please try again...");
	            }
	        } catch(NumberFormatException nfe) {
	            System.out.println("Error : connection id invalid , please try again...");
	        }
	    } else {
	        System.out.println("Error : the command format is invalid , please try '/send <ConnectionID> <Message>' ...");
	    }
	    
	}
	// send() end .
	
	// startChat() start :
	private void startChat() throws IOException {
	    
	    Scanner scanner = new Scanner(System.in);
	    
	    try {
	        
	        myIPAddress = InetAddress.getLocalHost();
			serv = new Server();
			new Thread(serv).start();
	        
	        while(true) {
	            System.out.println("Enter a command : ");
	            String userInput = scanner.nextLine();
	            if(userInput.equals("/help")) {
	                help();
	            } else if(userInput.equals("/myip")) {
	                System.out.println(getmyIPAddress());
	            } else if(userInput.equals("/myport")) {
	                System.out.println(getmyPort());
	            } else if(userInput.startsWith("/connect")) {
	                String[] cmdArgs = userInput.split("\\s+");
	                connect(cmdArgs);
	            } else if(userInput.equals("/list")) {
	                list();
	            } else if(userInput.startsWith("/terminate")) {
	                String[] args = userInput.split("\\s+");
	                terminate(args);
	            } else if(userInput.startsWith("/send")) {
	                String[] cmdArgs = userInput.split("\\s+");
	                send(cmdArgs);
	            } else if(userInput.startsWith("/exit")) {
	                System.out.println("Closing Connections...\nChat Successfully Exited.");
	                closeEverything();
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
	        closeEverything();
	    }
	    
	}
	// startChat() end .
	
	// closeEverything() start :
	private void closeEverything() throws IOException {
	    
	    for(Integer id : roomsHosts.keySet()) {
	        Room roomHost = roomsHosts.get(id);
	        roomHost.closeCon();
	    }
	    roomsHosts.clear();
	    serv.shutdown();
	    
	}
	// closeEverything() end .
	
	// ! ! ! //
	
	public class Room {
		InetAddress host;
		int port;
		boolean conec;
		PrintWriter statement;
		Socket sock;


		Room(InetAddress host, int port){
			this.host = host;
			this.port = port;
		}

		public boolean connected(){
			try{
				this.sock = new Socket(host, port);
				this.statement = new PrintWriter(sock.getOutputStream(), true);
				conec = true;
				return true;
			} catch(IOException e){
				conec = false;
				return false;
			}

		}

		public void send(String message){
			if(conec){
				statement.println(message);
			}
		}

		public boolean closeCon(){
			if(statement != null){
				statement.close();
			} 
			if(sock != null) {
				try{
					sock.close();
				} catch(IOException e){

				}
			}
			conec = false;
			return false;
		}
		
	}

	public class Client implements Runnable{

		BufferedReader connected;
		Socket sock;
		boolean open = true;

		public Client(BufferedReader connected, Socket ip){
			this.connected = connected;
			this.sock = ip;
		}

		public void exit(){
			try{
			if(connected == null){
				connected.close();
			}
			if(sock != null) {
				sock.close();
			}
			open = false;
			Thread.currentThread().interrupt();
			} catch(IOException e){
				
			}
		} 

		@Override
		public void run() {
			while(!sock.isClosed() && open){
				String temp;
			try {
				temp = connected.readLine();
				if(temp != null) {

					System.out.println(sock.getInetAddress().getHostAddress() + " - " + temp);

				} else {
					exit();
					return;
				}
				
			} catch(IOException e){
				
			}
		}
			
		}
		
	}

	public class Server implements Runnable{


		BufferedReader connected;		
		boolean stopped;
		Socket sock = null;

		List<Client> listOfClients = new ArrayList<Client>();

		@Override
		public void run() {
			System.out.println("Server has opened with port " + getmyPort());
			try{
				ServerSocket serSoc = new ServerSocket(getmyPort());										//Gets the port of the client and uses it as the serversocket for people to connect to
				
				while(!stopped){
					Socket client = serSoc.accept();														//Accepts incoming clients
					connected = new BufferedReader(new InputStreamReader(sock.getInputStream()));
					Client temp = new Client(connected, sock);												//Creates a client with the socket and the bufferedreader
					listOfClients.add(temp);													
				}
				
				
			} catch (IOException e) {

			}	

		} 

		public void shutdown() throws IOException{
			stopped = true;
			int i = 0;
			while(!listOfClients.isEmpty()){
				listOfClients.get(i++).exit();
			}
		}

		

	}

	

	
	
}


