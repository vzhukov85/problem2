import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * База с информацией по клиентам
 */
public class ClientBase {

  private Map<String, ClientBalance> clientInfo = new LinkedHashMap<>();
  /**
   * Разбор базы клиентов из файла, возвращает список клиентов с данными
   * @param clientsInfo база клиентов из файла
   * @return разобранная база клиентов в памяти
   */
  public Map<String, ClientBalance> initClientBase(File clientsInfo) {
    clientInfo = new LinkedHashMap<>();
    try (BufferedReader br = new BufferedReader(new FileReader(clientsInfo))) {
      String line;
      while ((line = br.readLine()) != null) {
        ClientBalance clientBalance = parseClientBalance(line);
        clientInfo.put(clientBalance.getName(), clientBalance);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return clientInfo;
  }

  private ClientBalance parseClientBalance(String line) {
    String[] client = line.split("\t");
    if (client.length !=6) {
      throw new IllegalArgumentException("В файле с информацией по клиенту ожидается 6 полей");
    }
    return new ClientBalance.Builder()
        .name(client[0])
        .amount(new BigDecimal(client[1]))
        .stock("A", Integer.valueOf(client[2]))
        .stock("B", Integer.valueOf(client[3]))
        .stock("C", Integer.valueOf(client[4]))
        .stock("D", Integer.valueOf(client[5]))
        .build();
  }

  /**
   * Запись информации по клиентам в файл
   * @param clientsBase результирующий файл с базой по счетам клиентов
   */
  public void clientToFile(File clientsBase) {
    if (clientsBase.exists()) {
      clientsBase.delete();
    }
    try (BufferedWriter br = new BufferedWriter(new FileWriter(clientsBase))) {
      for(ClientBalance clientBalance: clientInfo.values()) {
        String line = String.format("%s\t%s\t%d\t%d\t%d\t%d",
            clientBalance.getName(),
            clientBalance.getAmount().toPlainString(),
            clientBalance.getStocks().get("A"),
            clientBalance.getStocks().get("B"),
            clientBalance.getStocks().get("C"),
            clientBalance.getStocks().get("D"));
        br.write(line);
        br.newLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * Возвращает {@link ClientBalance} по имени клиента
   * @param client имя клиента
   * @return {@link ClientBalance}
   */
  public ClientBalance getClientBalance(String client) {
    return clientInfo.get(client);
  }

  /**
   * Добавление данных по клиенту
   * @param clientBalance {@link ClientBalance}
   */
  public void addClientBalance(ClientBalance clientBalance) {
    clientInfo.put(clientBalance.getName(), clientBalance);
  }
}
