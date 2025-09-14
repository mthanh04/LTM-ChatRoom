package chat;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer extends UnicastRemoteObject implements ChatInterface {
	private final Map<ClientInterface, String> clients = new ConcurrentHashMap<>();

	protected ChatServer() throws RemoteException {
		super();
	}

	@Override
	public synchronized void registerClient(ClientInterface client) throws RemoteException {
		clients.put(client, client.getName());
		broadcastSystem(client.getName() + " đã tham gia phòng chat");
	}

	@Override
	public void broadcastMessage(String sender, String message) throws RemoteException {
		List<ClientInterface> toRemove = new ArrayList<>();
		for (ClientInterface c : clients.keySet()) {
			try {
				c.receiveMessage(sender, message);
			} catch (RemoteException e) {
				toRemove.add(c);
			}
		}
		toRemove.forEach(clients::remove);
	}

	@Override
	public synchronized void removeClient(ClientInterface client) throws RemoteException {
		String name = clients.remove(client);
		if (name != null) {
			broadcastSystem(name + " đã rời phòng chat");
		}
	}

	private void broadcastSystem(String message) {
		List<ClientInterface> toRemove = new ArrayList<>();
		for (ClientInterface c : clients.keySet()) {
			try {
				c.receiveSystemMessage(message);
			} catch (RemoteException e) {
				toRemove.add(c);
			}
		}
		toRemove.forEach(clients::remove);
	}

	public static void main(String[] args) {
		try {
			ChatServer server = new ChatServer();
			Naming.rebind("rmi://localhost:1099/ChatServer", server);
			System.out.println("Chat Server is running...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
