package chat;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ChatServer extends UnicastRemoteObject implements ChatInterface {
	private final List<ClientInterface> clients;

	protected ChatServer() throws RemoteException {
		clients = new ArrayList<>();
	}

	@Override
	public void sendMessage(String message) throws RemoteException {
		for (ClientInterface client : clients) {
			client.receiveMessage(message);
		}
	}

	@Override
	public void registerClient(ClientInterface client) throws RemoteException {
		clients.add(client);
	}

	public static void main(String[] args) {
		try {
			// rmiregistry 1099
			ChatServer server = new ChatServer();

			Naming.rebind("rmi://localhost:1099/ChatServer", server);

			System.out.println("Chat Server is running...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
