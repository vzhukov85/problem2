import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Эмулятор работы биржи. На вход получает базу с клиентами и набор заявок.
 * В результате обработки всех заявок, обновляет инофрмацию по балансу на клиентских счетах.
 */
public class Exchange {
  Map<String, StockOrdersQueue> ordersQueues = new HashMap<>();
  ClientBase clientBase;

  /**
   * Инициализации биржи, заполнение информации по клиентам
   * @param clientBase база клиентов
   */
  public Exchange(ClientBase clientBase) {
    this.clientBase = clientBase;
  }

  /**
   * Обработка заявок на бирже
   * @param stockOrder файл со списоком заявок
   * @return обновленная база клиентов
   */
  public void processOrderList(File stockOrder) {
    try (BufferedReader br = new BufferedReader(new FileReader(stockOrder))) {
      String line;
      while ((line = br.readLine()) != null) {
        Order order = parseOrder(line);
        processOrder(order);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Order parseOrder(String line) {
    String[] orderLine = line.split("\t");
    if (orderLine.length != 5) {
      throw new IllegalArgumentException("В файле с позициями на продажу ожидается 5 полей в строке");
    }
    Order order = new Order.Builder()
        .clientName(orderLine[0])
        .type(Order.Type.parseLetter(orderLine[1]))
        .stockName(orderLine[2])
        .price(new BigDecimal(orderLine[3]))
        .count(Integer.parseInt(orderLine[4]))
        .build();
    return order;
  }

  private void processOrder(Order order) {
    StockOrdersQueue ordersQueue = ordersQueues.get(order.getStockName());
    if (ordersQueue == null) {
      ordersQueue = new StockOrdersQueue(clientBase);
      ordersQueues.put(order.getStockName(), ordersQueue);
    }
    ordersQueue.addAndProcess(order);
  }
}
