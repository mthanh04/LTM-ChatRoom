package chat;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ChatClient extends UnicastRemoteObject implements ClientInterface {
	private final ChatInterface server;
	private final ChatClientUI ui;
	private final String username;

	public ChatClient(ChatInterface server, ChatClientUI ui, String username) throws RemoteException {
		super();
		this.server = server;
		this.ui = ui;
		this.username = username;
		// đăng ký lên server
		server.registerClient(this);
	}

	@Override
	public String getName() throws RemoteException {
		return username;
	}

	@Override
	public void receiveMessage(String sender, String message) throws RemoteException {
		ui.showMessage(sender, message);
	}

	@Override
	public void receiveSystemMessage(String message) throws RemoteException {
		ui.showSystemMessage(message);
	}

	public void sendMessage(String message) {
		try {
			server.broadcastMessage(username, message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			server.removeClient(this);
			UnicastRemoteObject.unexportObject(this, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Chạy client từ đây
	public static void main(String[] args) {
		try {
			ChatInterface server = (ChatInterface) Naming.lookup("rmi://localhost:1099/ChatServer");
			ChatClientUI ui = new ChatClientUI(server);
			ui.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
