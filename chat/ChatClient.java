package chat;

import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;

public class ChatClient extends UnicastRemoteObject implements ClientInterface {
	private ChatInterface server;
	private ChatClientUI ui;

	public ChatClient(ChatInterface server, ChatClientUI ui) throws Exception {
		this.server = server;
		this.ui = ui;
		server.registerClient(this);
	}

	@Override
	public void receiveMessage(String message) {
		ui.showMessage(message);
	}

	public void sendMessage(String message) {
		try {
			server.sendMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
