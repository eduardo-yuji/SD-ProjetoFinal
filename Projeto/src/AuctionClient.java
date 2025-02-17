import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class AuctionClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            Auction auction = (Auction) registry.lookup("AuctionService");
            
            Scanner scanner = new Scanner(System.in);
            System.out.print("Digite seu nome: ");
            String user = scanner.nextLine();
            
            while (true) {
                System.out.print("\n------Menu Cliente------\n");
                System.out.println("1 - Fazer Lance");
                System.out.println("2 - Ver Status");
                System.out.println("3 - Sair");
                int choice = scanner.nextInt();
                
                switch (choice) {
                    case 1:
                        System.out.print("Digite o valor do lance: ");
                        double bid = scanner.nextDouble();
                        System.out.println(auction.placeBid(user, bid));
                        break;
                    case 2:
                        System.out.println(auction.getStatus());
                        break;
                    case 3:
                        System.out.println("Saindo...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Opção inválida!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}