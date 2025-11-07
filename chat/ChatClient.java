package chat;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.SwingUtilities;

public class ChatClient extends UnicastRemoteObject implements ClientInterface {
	private final ChatInterface server;
	private final ChatClientUI ui;
	private final String username;
	private boolean muted = false;

	public ChatClient(ChatInterface server, ChatClientUI ui, String username) throws RemoteException {
		super();
		this.server = server;
		this.ui = ui;
		this.username = username;
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

	@Override
	public void forceDisconnect(String reason) throws RemoteException {
		SwingUtilities.invokeLater(() -> {
			ui.showSystemMessage("[System] " + reason);
			ui.showKickDialog(reason);
		});
		new Thread(() -> {
			try {
				UnicastRemoteObject.unexportObject(this, true);
			} catch (Exception ignored) {
			}
		}).start();
	}

	@Override
	public void updateClientList(String[] clients) throws RemoteException {
		ui.updateClientList(clients);
	}

	@Override
	public void setMuted(boolean muted) throws RemoteException {
		this.muted = muted;
		ui.setMuted(muted);
	}

	public void sendMessage(String message) {
		if (muted) {
			ui.showSystemMessage("[System] Bạn đang bị cấm chat.");
			return;
		}
		try {
			server.broadcastMessage(username, message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void kickUser(String targetName) {
		try {
			server.kickClient(username, targetName);
		} catch (Exception e) {
			ui.showSystemMessage("[System] Kick thất bại: " + e.getMessage());
		}
	}

	public void muteUser(String targetName, boolean mute) {
		try {
			server.muteClient(username, targetName, mute);
		} catch (Exception e) {
			ui.showSystemMessage("[System] Lỗi khi cấm/mở chat: " + e.getMessage());
		}
	}

	public void disconnect() {
		new Thread(() -> {
			try {
				server.removeClient(this);
				UnicastRemoteObject.unexportObject(this, true);
			} catch (Exception ignored) {
			}
		}).start();
	}

	public String getAdminName() {
		try {
			return server.getAdminName();
		} catch (RemoteException e) {
			return null;
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
