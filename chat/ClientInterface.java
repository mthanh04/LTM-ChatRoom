package chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
	String getName() throws RemoteException;

	void receiveMessage(String sender, String message) throws RemoteException;

	void receiveSystemMessage(String message) throws RemoteException;
}
