package chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatInterface extends Remote {
	void registerClient(ClientInterface client) throws RemoteException;

	void removeClient(ClientInterface client) throws RemoteException;

	void broadcastMessage(String sender, String message) throws RemoteException;

	void kickClient(String adminName, String targetName) throws RemoteException;

	void muteClient(String adminName, String targetName, boolean mute) throws RemoteException;

	String getAdminName() throws RemoteException;
}
