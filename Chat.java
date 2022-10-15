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
	private void help() {
	    
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
	    return myIPAddress.getHostAddress();   // Returns the IP address .
	}
	// getmyIPAddress() end .
	
	// getmyPort() start :
	private int getmyPort() {
	    return myPort;   // Returns the port .
	}
	// getmyPort() end .
	
	// connect() start :
	private void connect(String[] cmdArgs) {
	    
	    if(cmdArgs != null && cmdArgs.length == 3) {
	        try {
	            InetAddress remoteAddress = InetAddress.getByName(cmdArgs[1]);
	            int remotePort = Integer.parseInt(cmdArgs[2]);
	            System.out.println("Connecting to " +remoteAddress+ " on port /" +remotePort+ " ...");   // Attempts to connect to the given IP address and port , a message is displayed stating so .
	            Room roomHost = new Room(remoteAddress, remotePort);
	            if(roomHost.connected()) {
	                roomsHosts.put(clientCounter, roomHost);
	                System.out.println("Successfully Connected , Client ID : " + clientCounter++ + " .");   // If the connection is successful , a message is displayed stating so along with the client id .
	            } else {
	                System.out.println("Error : connection unsuccessful , please try again...");   // If the connection is unsuccessful , a message is displayed stating so and the user is prompted to try again .
	            }
	        } catch(NumberFormatException nfe) {
	            System.out.println("Error : connection unsuccessful (invalid remote host port) , please try again...");   // An error message is displayed when the given port is invalid , the user is prompted to try again .
	        } catch(UnknownHostException e) {
	            System.out.println("Error : connection unsuccessful (invalid remote host address) , please try again...");   // An error message is displayed when the given IP address is invalid , the user is prompted to try again .
	        }
	    } else {
	        System.out.println("Error : the command format is invalid , please try '/connect <Destination> <PortNumber>' ...");   // An error message is displayed when the command format is invalid , the user is prompted to try again and shown the proper format .
	    }
	    
	}
	// connect() end .
	
	// list() start :
	private void list() {
	    
	    System.out.println("ID\tIP ADDRESS\tPORT");   // Header .
	    
	    if(roomsHosts.isEmpty()) {
	        System.out.println("There are no rooms available...");   // Checks for a room(s) . If there is no room(s) available , a message is displayed stating so .
	    } else {
	        for(Integer id : roomsHosts.keySet()) {
	            Room roomHost = roomsHosts.get(id);
	            System.out.println(id +"\t"+ roomHost.toString());   // Checks for a room(s) . If there is a room(s) available , the ID + IP Address + Port are displayed .
	        }
	    }
	    
	}
	// list() end .
	
	// terminate() start :
	private void terminate(String[] cmdArgs) {
	    
	    if(cmdArgs != null && cmdArgs.length == 2) {
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

	            System.out.println("ID :\t" + roomsHosts.get(id));
	            if(roomHost != null) {
	                StringBuilder message = new StringBuilder();
	                for(int i = 2; i < cmdArgs.length; i++) {
	                    message.append(cmdArgs[i]);
	                    message.append(" ");
	                }
					roomHost.send("Message recieved from " + getmyIPAddress());
					roomHost.send("Sender's port: " + getmyPort());
	                roomHost.send("Message: " + message.toString());
					System.out.println("Message Successfully Sent to " + roomHost.getPortNum() + ".");
	                
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
	            System.out.println("Enter a command below : ");
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
	            } else if(userInput.equals("/exit")) {
	                
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
			System.out.println("Closing Connections...\nChat Successfully Exited.");
	    }
	    roomsHosts.clear();
	    serv.shutdown();
	    
	}
	// closeEverything() end .
	
	// ! ! ! //

	//Room class: 
	//Makes managing socket connection and wraps the socket and the output stream for the clients to send messages easier
	
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

		public boolean connected(){			//Sees if client is connected so that it is able to recieve messages
			try{
				this.sock = new Socket(host, port);
				this.statement = new PrintWriter(sock.getOutputStream(), true);
				conec = true;
				
			} catch(IOException e){
				System.out.println("ERROR 404");
			}
			return conec;

		}

		public void send(String message){			//Responsible for outputting sent messages
			if(conec == true){
				statement.println(message);			//Prints the message to the reciever
			} else {
				System.out.println("User not connected, message not sent.");
			}
		}

		public boolean closeCon(){		//Closes connection 
			if(statement != null){
				statement.close();	
			} 
			if(sock != null) {
				try{
					sock.close();
				} catch(IOException e){
					System.out.println("ERROR 404");
				}
			}
			conec = false;
			return conec;
		}

		public int getPortNum(){		//Returns port number for the specific room
			return port;
		}

		@Override
		public String toString() {
			return host + "\t/" + port;
		}


		
	}

	//Client class:
	//Connects to a socket and listens for messages from a connected client

	public class Client implements Runnable{

		BufferedReader connected;
		Socket sock = null;
		boolean open = true;

		public Client(BufferedReader connected, Socket ip){		//Constructor
			this.connected = connected;
			this.sock = ip;
		}

		public void exit(){
			
			if(connected == null){			//Will close any further communication with the others
				try{
					connected.close();
				} catch (IOException e) {
					System.out.println("ERROR 404");
				}
				
			if(sock != null) {				//Will close the socket
				try{
					sock.close();
				}
			 catch(IOException e){
					System.out.println("ERROR 404");
				}
				open = false;
				Thread.currentThread().interrupt();	//Closes the thread
			}
		
			
		} 
	}

		@Override
		public void run() {
			while(!sock.isClosed() && open){		
				String temp;
			try {										//While it is connected to a socket and it is open, will print out what it is given
				temp = connected.readLine();
				
				if(temp != null) {

					System.out.println(temp);		//Prints out any messages from server

				} else {
					exit();
					System.out.println("Connection terminated: " + sock.getInetAddress().getHostAddress() + ", please terminate on your end");	//Once a client terminates connection, it will disconnect from that server and tell the other clients to terminate their connection
					return;
				}
				
				} catch(IOException e){
					System.out.println("ERROR 404");
				}
			}
			
			}
		
		}
	

		//Server class:
		//Creates a new TCP socket to allow multiple connections
	

	public class Server implements Runnable{

		BufferedReader connected;		
		boolean stopped;
		Socket sock = null;

		List<Client> listOfClients = new ArrayList<Client>();

		public void shutdown() throws IOException{
			stopped = true;
			
			for(Client client : listOfClients){					//Goes through the list of clients and removes them since the server is shutting down. 
				client.exit();
			}

			Thread.currentThread().interrupt();					//Exits the thread

		}

		@Override
		public void run() {
			
			try{
				ServerSocket serSoc = new ServerSocket(getmyPort());								//Gets the port of the client and uses it as the serversocket for people to connect to
				
				while(!stopped){
					try{
						sock = serSoc.accept();														//Accepts data from incoming clients
						connected = new BufferedReader(new InputStreamReader(sock.getInputStream()));	//Gets input stream for reading data from the socket
						Client temp = new Client(connected, sock);								//Creates a temporary client with the bufferedreader and the socket
						new Thread(temp).start();												//Creates a thread with the client and starts
						listOfClients.add(temp);
					} catch (IOException ez)	{
						System.out.println("ERROR 404");
					}											
				}
				
				
			} catch (IOException e) {
				System.out.println("ERROR 404");
			}	

		} 

	}
	
}


