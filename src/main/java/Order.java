import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Заявка на бирже
 */
public class Order implements Comparable<Order> {
  /**
   * Тип заявки: покупка или продажа
   */
  enum Type {
    SELL("s"),
    BAY("b");

    private String letter;

    Type(String letter) {
      this.letter = letter;
    }

    /**
     * Выбор типа заявки, в зависимости от буквы
     * @param letter буква заявки
     * @return {@link Type}
     */
    public static Type parseLetter(String letter) {
      for (Type pos: Type.values()) {
        if (letter.equalsIgnoreCase(pos.letter)) {
          return pos;
        }
      }
      throw new IllegalArgumentException("Некорректный тип заявки " + letter);
    }
  }

  private String clientName;
  private Type type;
  private String stockName;
  private BigDecimal price;
  private Integer count;
  private int time;

  /**
   * Иммитация работы с вренем, важен только порядок, в котором создаются заявки.
   * Если цены заявок будут одинаковые, то должна выбираться заявка, созданная раньше.
   */
  private static AtomicInteger timeCounter = new AtomicInteger(0);

  private Order() {
    this.time = timeCounter.incrementAndGet();
  }

  public String getClientName() {
    return clientName;
  }

  public Type getType() {
    return type;
  }

  public String getStockName() {
    return stockName;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public Integer getCount() {
    return count;
  }

  /**
   * Обработка заявки на покупку/продажу акции. Списание денег или акций со счета клиента
   * @param clientBase база клиентов
   * @param price цена покупки/продажи
   * @param count количество единиц на прокупку/продажу
   */
  public void process(ClientBase clientBase, BigDecimal price, int count) {
    ClientBalance clientBalance = clientBase.getClientBalance(clientName);
    switch (type) {
      case SELL:
        clientBalance.sell(stockName, price, count);
        break;
      case BAY:
        clientBalance.bay(stockName, price, count);
        break;
    }
    this.count -= count;
  }

  @Override
  public int compareTo(Order o) {
    if (this.price.compareTo(o.price) != 0) {
      switch (type) {
        case BAY:
          return o.price.compareTo(this.price);
        case SELL:
          return this.price.compareTo(o.price);
      }
    }
    return Integer.valueOf(this.time).compareTo(o.time);
  }

  /**
   * Создает экземпляр {@link Order}
   */
  public static class Builder {
    private String clientName;
    private Type type;
    private String stockName;
    private BigDecimal price;
    private Integer count;

    /**
     * Указывает имя клиента
     * @param clientName имя клиента по заявке
     * @return {@link Order.Builder}
     */
    public Order.Builder clientName(String clientName) {
      this.clientName = clientName;
      return this;
    }

    /**
     * Тип заявки
     * @param type тип заявки
     * @return {@link Order.Builder}
     */
    public Order.Builder type(Type type) {
      this.type = type;
      return this;
    }

    /**
     * Имя акции
     * @param stockName имя акции
     * @return {@link Order.Builder}
     */
    public Order.Builder stockName(String stockName) {
      this.stockName = stockName;
      return this;
    }

    /**
     * Цена акции
     * @param price цена акции
     * @return {@link Order.Builder}
     */
    public Order.Builder price(BigDecimal price) {
      this.price = price;
      return this;
    }

    /**
     * Количество акций
     * @param count количество акций
     * @return {@link Order.Builder}
     */
    public Order.Builder count(Integer count) {
      this.count = count;
      return this;
    }

    /**
     * Создает новый экземпляр {@link Order}
     * @return {@link Order}
     */
    public Order build() {
      Order order = new Order();
      order.clientName = clientName;
      order.type = type;
      order.stockName = stockName;
      order.price = price;
      order.count = count;

      return order;
    }
  }

}
