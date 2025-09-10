package chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatInterface extends Remote {
	void sendMessage(String message) throws RemoteException;

	void registerClient(ClientInterface client) throws RemoteException;
}
