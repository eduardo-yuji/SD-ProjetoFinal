//javac *.java
//rmiregistry
//java AuctionServer
//java AuctionClient

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Auction extends Remote {
    String placeBid(String user, double amount) throws RemoteException;
    String getStatus() throws RemoteException;
}