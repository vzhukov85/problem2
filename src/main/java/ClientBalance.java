import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Инфомация по клиету, баланс в долларах и список акций.
 * Класс не является потокобезопасным, использовать только в однопоточном режиме.
 * Исходные данные:
 * <ol>
 *    <li> Количество денег на счете может быть дробным числом</li>
 *    <li> Цена акции может быть дробным числом</li>
 *    <li> Количство акций только целое (дробные акции не рассматриваются)</li>
 * </ol>
 */
public class ClientBalance {
  private BigDecimal amount;
  private String name;
  private Map<String, Integer> stocks;

  private ClientBalance(BigDecimal amount) {
    this.amount = amount;
  }

  /**
   * Возвращает имя клиента
   * @return имя клиента
   */
  public String getName() {
    return name;
  }

  /**
   * Покупка акции клиентом
   * @param stockName имя акции
   * @param price цена за одну акцию
   * @param count количество акций на покупку
   * @throws IllegalArgumentException ошибка в случае нехватка денег на счету у клиента для покупки новой позиции
   */
  public void bay(String stockName, Integer price, Integer count) {
    bay(stockName, BigDecimal.valueOf(price), count);
  }

  /**
   * Покупка акции клиентом
   * @param stockName имя акции
   * @param price цена за одну акцию
   * @param count количество акций на покупку
   * @throws IllegalArgumentException ошибка в случае нехватка денег на счету у клиента для покупки новой позиции
   */
  public void bay(String stockName, BigDecimal price, Integer count) {
    BigDecimal fullPrice = price.multiply(BigDecimal.valueOf(count));

    Integer stockCountCurrent = 0;
    if (stocks.containsKey(stockName)) {
      stockCountCurrent = stocks.get(stockName);
    }
    stockCountCurrent += count;

    amount = amount.subtract(fullPrice);
    stocks.put(stockName, stockCountCurrent);
  }

  /**
   * Продажа акций клиентом
   * @param stockName имя акции
   * @param price цена
   * @param count количество акций
   */
  public void sell(String stockName, Integer price, Integer count) {
    sell(stockName, BigDecimal.valueOf(price), count);
  }

  /**
   * Продажа акций клиентом
   * @param stockName имя акции
   * @param price цена
   * @param count количество акций
   */
  public void sell(String stockName, BigDecimal price, Integer count) {
    if (!stocks.containsKey(stockName)) {
      throw new IllegalArgumentException("У клиента нет заявленных акций");
    }
    Integer stockCountCurrent = stocks.get(stockName);
    if (stockCountCurrent < count) {
      throw new IllegalArgumentException("У клиента меньше акций, чем заявлено на продажу");
    }
    stockCountCurrent -= count;
    BigDecimal fullPrice = price.multiply(BigDecimal.valueOf(count));

    amount = amount.add(fullPrice);
    stocks.put(stockName, stockCountCurrent);
  }

  /**
   * Возвращает баланс клиента
   * @return баланс клиента
   */
  public BigDecimal getAmount() {
    return amount;
  }

  /**
   * Возвращает список акций
   * @return список акций с количеством, по каждой акции
   */
  public Map<String, Integer> getStocks() {
    return Collections.unmodifiableMap(stocks);
  }


  /**
   * Создает экземляр {@link Builder} для инициализации начальных данных по балансу клиента
   * @return {@link Builder}
   */
  public static Builder newBuilder() {
    return new ClientBalance.Builder();
  }

  /**
   * Создает экземпляр {@link ClientBalance}
   */
  public static class Builder {
    private BigDecimal amount = BigDecimal.ZERO;
    private String name = "";
    private Map<String, Integer> stocks  = new LinkedHashMap<>();

    /**
     * Указывает баланс в долларах по клиенту
     * @param amount
     * @return {@link Builder}
     */
    public Builder amount(BigDecimal amount) {
      if (amount.signum() < 0) {
        throw new IllegalArgumentException("Не допускается указывать отрицательный баланс");
      }
      this.amount = amount;
      return this;
    }

    /**
     * Указывает имя клиента
     * @param name имя клиента
     * @return {@link Builder}
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Инициализирует данные по акциям
     * @param stockName имя акции
     * @param count количество начальный акций
     * @return {@link Builder}
     */
    public Builder stock(String stockName, Integer count) {
      if (count < 0) {
        throw new IllegalArgumentException("Не допускается указывать отрицательное количество акций");
      }
      stocks.put(stockName, count);
      return this;
    }

    /**
     * Создает новый экземпляр {@link ClientBalance}
     * @return {@link ClientBalance}
     */
    public ClientBalance build() {
      ClientBalance clientBalance = new ClientBalance(amount);
      clientBalance.stocks = stocks;
      clientBalance.name = name;
      return clientBalance;
    }
  }
}
