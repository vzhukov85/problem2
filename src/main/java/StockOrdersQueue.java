import java.util.ArrayList;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Заявки на куплю/продажи по одному типу акций
 */
public class StockOrdersQueue {

  /**
   * База клиентов
   */
  ClientBase clientBase;
  /**
   * Очередь заявок на продажу
   */
  Queue<Order> sell = new PriorityQueue<>();
  /**
   * Очередь заявок на покупку
   */
  Queue<Order> bay = new PriorityQueue<>();

  /**
   * Инициализация очереди на куплю/продажу. Передается ссылка на базу клиентов
   * @param clientBase база клиентов
   */
  public StockOrdersQueue(ClientBase clientBase) {
    this.clientBase = clientBase;
  }

  /**
   * Обработка позиции купли/продажи. Алгоритм работы на примере покупки.
   * При размещении заявки на покупку, проверяется наличие заявок на продажу по такой же  или более низкой цене.
   * Если такие заявки имеются, то обрабатываются все доступные заявки, за сключением заявок, выставленных тем же пользователем.
   * Цена новой заявки и заявки в очереди могут отличаться, в этом случае операция будет производится по цене заявке в очереди.
   * Если нет подходящих заявок на продажу, то заявка на покупку выставляется в очередь.
   *
   * Очереди заявок отсортированы по цене и времени
   *
   * @param newOrder новая заявка на обработку или выставление в очередь
   */
  public void addAndProcess(Order newOrder) {
    List<Order> selfOrders = new ArrayList<>();
    while(isGoodPriceOrder(newOrder)) {

      Order queueOrder = pollOrder(newOrder.getType());
      if (queueOrder.getClientName().equals(newOrder.getClientName())) {
        selfOrders.add(queueOrder);
        continue;
      }

      int minCount = Math.min(newOrder.getCount(), queueOrder.getCount());
      newOrder.process(clientBase, queueOrder.getPrice(), minCount);
      queueOrder.process(clientBase, queueOrder.getPrice(), minCount);

      if (queueOrder.getCount() > 0) {
        addOrder(queueOrder);
      }
      if (newOrder.getCount() <= 0) {
        break;
      }
    }
    if (newOrder.getCount() > 0) {
      addOrder(newOrder);
    }
    returnSelfOrder(selfOrders);
  }

  private boolean isGoodPriceOrder(Order newOrder) {
    boolean isGoodPrice = false;
    switch (newOrder.getType()) {
      case SELL:
        isGoodPrice = !bay.isEmpty() && bay.peek().getPrice().compareTo(newOrder.getPrice()) >= 0;
        break;
      case BAY:
        isGoodPrice = !sell.isEmpty() && newOrder.getPrice().compareTo(sell.peek().getPrice()) >= 0;
        break;
    }
    return isGoodPrice;
  }

  private Order pollOrder(Order.Type type) {
    switch (type) {
      case SELL:
        return bay.poll();
      case BAY:
        return sell.poll();
    }
    return null;
  }

  private void addOrder(Order order) {
    switch (order.getType()) {
      case SELL:
        sell.add(order);
        break;
      case BAY:
        bay.add(order);
        break;
    }
  }

  private void returnSelfOrder(List<Order> orders) {
    for(Order order: orders) {
      switch (order.getType()) {
        case SELL:
          sell.add(order);
          break;
        case BAY:
          bay.add(order);
          break;
      }
    }
  }


}
