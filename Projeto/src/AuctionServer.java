import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class AuctionServer extends UnicastRemoteObject implements Auction {

    private String highestBidder; // Armazena o nome do maior lance
    private double highestBid;    // Armazena o valor do maior lance
    private boolean isClosed;     // Indica se o leilão está encerrado
    private final CustomLock lock = new CustomLock(); // Tranca personalizada

    // Construtor do servidor
    protected AuctionServer() throws RemoteException {
        super();
        this.highestBid = 0; // Inicializa o maior lance como 0
        this.highestBidder = "Ninguém"; // Inicializa o maior lance como "Ninguém"
        this.isClosed = false; // Inicializa o leilão como aberto
    }

    @Override
    public String placeBid(String user, double amount) throws RemoteException {
        try {
            lock.lock(); // Adquire a tranca para evitar condições de corrida
            if (isClosed) {
                return "Leilão encerrado!";
            }
            if (amount > highestBid) {
                highestBid = amount;
                highestBidder = user;
                return "Lance de " + user + " aceito: R$" + amount;
            } else {
                return "Lance muito baixo! O maior lance é: R$" + highestBid;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restaura o estado de interrupção
            return "Erro ao processar o lance.";
        } finally {
            lock.unlock(); // Libera a tranca
        }
    }

    @Override
    public String getStatus() throws RemoteException {
        return "Maior lance: R$" + highestBid + " por " + highestBidder;
    }

    // Método para encerrar o leilão
    public void closeAuction() {
        try {
            lock.lock(); // Adquire a tranca
            isClosed = true;
            System.out.println("Leilão encerrado! Vencedor: " + highestBidder + " com R$" + highestBid);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restaura o estado de interrupção
        } finally {
            lock.unlock(); // Libera a tranca
        }
    }

    public static void main(String[] args) {
        try {
            AuctionServer server = new AuctionServer();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("AuctionService", server);
            System.out.println("Servidor de Leilão pronto...");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("---Menu Servidor---");
                System.out.println("1 - Encerrar leilão");
                System.out.println("2 - Sair");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("1")) {
                    server.closeAuction();
                    System.out.println("Para iniciar um novo leilão, digite '1' para iniciar, ou '2' para sair.");
                    input = scanner.nextLine();
                    if (input.equalsIgnoreCase("1")) {
                        server = new AuctionServer();
                        registry.rebind("AuctionService", server);
                        System.out.println("Novo leilão iniciado!");
                    } else if (input.equalsIgnoreCase("2")) {
                        System.out.println("Finalizando servidor...");
                        UnicastRemoteObject.unexportObject(server, true); // Desregistra o servidor RMI
                        System.exit(0); // Encerra o programa
                    }
                } else if (input.equalsIgnoreCase("2")) {
                    System.out.println("Finalizando servidor...");
                    UnicastRemoteObject.unexportObject(server, true); // Desregistra o servidor RMI
                    System.exit(0); // Encerra o programa
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}