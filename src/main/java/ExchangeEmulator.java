import java.io.File;
import java.nio.file.Paths;
import java.util.Map;

public class ExchangeEmulator {
  public static void main(String[] args) throws Exception {
    File clientsInitFile = Paths.get(ExchangeEmulator.class.getClassLoader().getResource("clients.txt").toURI()).toFile();
    File clientsResultFile = Paths.get(ExchangeEmulator.class.getClassLoader().getResource("result.txt").toURI()).toFile();
    File orders = Paths.get(ExchangeEmulator.class.getClassLoader().getResource("orders.txt").toURI()).toFile();

    ClientBase clientBase = new ClientBase();
    clientBase.initClientBase(clientsInitFile);

    Exchange exchange = new Exchange(clientBase);
    exchange.processOrderList(orders);

    clientBase.clientToFile(clientsResultFile);
  }
}
