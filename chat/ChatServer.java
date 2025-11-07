package chat;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer extends UnicastRemoteObject implements ChatInterface {
	private final Map<ClientInterface, String> clients = new ConcurrentHashMap<>();
	private final Set<String> mutedUsers = Collections.synchronizedSet(new HashSet<>());
	private String adminName = null;

	protected ChatServer() throws RemoteException {
		super();
	}

	@Override
	public synchronized void registerClient(ClientInterface client) throws RemoteException {
		String name = client.getName();
		if (clients.isEmpty()) {
			adminName = name;
			client.receiveSystemMessage("Bạn là chủ nhóm (Admin).");
		}
		clients.put(client, name);
		broadcastSystem(name + " đã tham gia phòng chat");
		broadcastClientList();
	}

	@Override
	public synchronized void removeClient(ClientInterface client) throws RemoteException {
		String name = clients.remove(client);
		if (name != null) {
			broadcastSystem(name + " đã rời phòng chat");
			broadcastClientList();
		}
	}

	@Override
	public synchronized void broadcastMessage(String sender, String message) throws RemoteException {
		if (mutedUsers.contains(sender)) {
			for (Map.Entry<ClientInterface, String> e : clients.entrySet()) {
				if (e.getValue().equals(sender)) {
					e.getKey().receiveSystemMessage("Bạn đang bị admin cấm chat.");
					return;
				}
			}
		}

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
	public synchronized void kickClient(String adminName, String targetName) throws RemoteException {
		if (!adminName.equals(this.adminName)) {
			throw new RemoteException("Chỉ admin mới có quyền đuổi người khác.");
		}
		if (targetName.equals(adminName)) {
			throw new RemoteException("Không thể kick chính bạn!");
		}

		ClientInterface target = null;
		for (Map.Entry<ClientInterface, String> entry : clients.entrySet()) {
			if (entry.getValue().equals(targetName)) {
				target = entry.getKey();
				break;
			}
		}

		if (target != null) {
			target.forceDisconnect("Bạn đã bị admin đuổi khỏi phòng chat.");
			clients.remove(target);
			broadcastSystem(targetName + " đã bị admin đuổi.");
			broadcastClientList();
		} else {
			throw new RemoteException("Không tìm thấy thành viên " + targetName);
		}
	}

	@Override
	public synchronized void muteClient(String adminName, String targetName, boolean mute) throws RemoteException {
		if (!adminName.equals(this.adminName))
			throw new RemoteException("Chỉ admin mới có quyền cấm/mở chat.");

		if (targetName.equals(adminName))
			throw new RemoteException("Không thể cấm chính bạn.");

		if (mute)
			mutedUsers.add(targetName);
		else
			mutedUsers.remove(targetName);

		for (ClientInterface c : clients.keySet()) {
			if (clients.get(c).equals(targetName)) {
				c.setMuted(mute);
				c.receiveSystemMessage(mute ? "Bạn đã bị admin cấm chat." : "Admin đã mở chat cho bạn.");
				break;
			}
		}
		broadcastSystem("Admin đã " + (mute ? "cấm" : "mở") + " chat cho " + targetName + ".");
	}

	@Override
	public String getAdminName() throws RemoteException {
		return adminName;
	}

	private void broadcastSystem(String message) {
		clients.keySet().forEach(c -> {
			try {
				c.receiveSystemMessage(message);
			} catch (Exception ignored) {
			}
		});
	}

	private void broadcastClientList() {
		String[] names = clients.values().toArray(new String[0]);
		clients.keySet().forEach(c -> {
			try {
				c.updateClientList(names);
			} catch (Exception ignored) {
			}
		});
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
