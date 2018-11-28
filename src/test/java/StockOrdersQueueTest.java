import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class StockOrdersQueueTest {

  private ClientBase clientBase;
  private StockOrdersQueue subj;

  @Before
  public void setUp() {
    clientBase = new ClientBase();
    subj = new StockOrdersQueue(clientBase);

    clientBase.addClientBalance(new ClientBalance.Builder()
        .name("C1")
        .amount(BigDecimal.valueOf(100))
        .stock("A", 100)
        .stock("B", 15)
        .build()
    );

    clientBase.addClientBalance(new ClientBalance.Builder()
        .name("C2")
        .amount(BigDecimal.valueOf(200))
        .stock("A", 5)
        .stock("B", 10)
        .build()
    );

    subj = new StockOrdersQueue(clientBase);
  }

  @Test
  public void addAndProcessBay() {
    Order o0 = new Order.Builder()
        .price(BigDecimal.valueOf(6))
        .stockName("A")
        .type(Order.Type.SELL)
        .count(50)
        .clientName("C1")
        .build();

    Order o1 = new Order.Builder()
        .price(BigDecimal.valueOf(5))
        .stockName("A")
        .type(Order.Type.SELL)
        .count(100)
        .clientName("C1")
        .build();

    Order o2 = new Order.Builder()
        .price(BigDecimal.valueOf(6))
        .stockName("A")
        .type(Order.Type.BAY)
        .count(10)
        .clientName("C2")
        .build();

    subj.addAndProcess(o0);
    subj.addAndProcess(o1);
    subj.addAndProcess(o2);

    Assert.assertThat(subj.bay.isEmpty(), Matchers.is(true));
    Assert.assertThat(subj.sell.size(), Matchers.equalTo(2));
    Assert.assertThat(subj.sell.peek().getCount(), Matchers.equalTo(90));

    Assert.assertThat(clientBase.getClientBalance("C1").getStocks().get("A"), Matchers.equalTo(90));
    Assert.assertThat(clientBase.getClientBalance("C1").getAmount(), Matchers.equalTo(BigDecimal.valueOf(150)));

    Assert.assertThat(clientBase.getClientBalance("C2").getStocks().get("A"), Matchers.equalTo(15));
    Assert.assertThat(clientBase.getClientBalance("C2").getAmount(), Matchers.equalTo(BigDecimal.valueOf(150)));
  }

  @Test
  public void addAndProcessSell() {
    Order o0 = new Order.Builder()
        .price(BigDecimal.valueOf(5))
        .stockName("A")
        .type(Order.Type.BAY)
        .count(100)
        .clientName("C1")
        .build();

    Order o1 = new Order.Builder()
        .price(BigDecimal.valueOf(10))
        .stockName("A")
        .type(Order.Type.BAY)
        .count(10)
        .clientName("C1")
        .build();

    Order o2 = new Order.Builder()
        .price(BigDecimal.valueOf(5))
        .stockName("A")
        .type(Order.Type.SELL)
        .count(5)
        .clientName("C2")
        .build();

    subj.addAndProcess(o0);
    subj.addAndProcess(o1);
    subj.addAndProcess(o2);

    Assert.assertThat(subj.sell.isEmpty(), Matchers.is(true));
    Assert.assertThat(subj.bay.size(), Matchers.equalTo(2));
    Assert.assertThat(subj.bay.peek().getCount(), Matchers.equalTo(5));

    Assert.assertThat(clientBase.getClientBalance("C1").getStocks().get("A"), Matchers.equalTo(105));
    Assert.assertThat(clientBase.getClientBalance("C1").getAmount(), Matchers.equalTo(BigDecimal.valueOf(50)));

    Assert.assertThat(clientBase.getClientBalance("C2").getStocks().get("A"), Matchers.equalTo(0));
    Assert.assertThat(clientBase.getClientBalance("C2").getAmount(), Matchers.equalTo(BigDecimal.valueOf(250)));
  }


  @Test
  public void addAndProcessBayExcludeSelf() {
    Order o0 = new Order.Builder()
        .price(BigDecimal.valueOf(5))
        .stockName("A")
        .type(Order.Type.SELL)
        .count(100)
        .clientName("C2")
        .build();

    Order o1 = new Order.Builder()
        .price(BigDecimal.valueOf(5))
        .stockName("A")
        .type(Order.Type.SELL)
        .count(100)
        .clientName("C1")
        .build();

    Order o2 = new Order.Builder()
        .price(BigDecimal.valueOf(5))
        .stockName("A")
        .type(Order.Type.BAY)
        .count(10)
        .clientName("C2")
        .build();

    subj.addAndProcess(o0);
    subj.addAndProcess(o1);
    subj.addAndProcess(o2);

    Assert.assertThat(subj.bay.isEmpty(), Matchers.is(true));
    Assert.assertThat(subj.sell.size(), Matchers.equalTo(2));
    Assert.assertThat(subj.sell.poll().getCount(), Matchers.equalTo(100));
    Assert.assertThat(subj.sell.poll().getCount(), Matchers.equalTo(90));

    Assert.assertThat(clientBase.getClientBalance("C1").getStocks().get("A"), Matchers.equalTo(90));
    Assert.assertThat(clientBase.getClientBalance("C1").getAmount(), Matchers.equalTo(BigDecimal.valueOf(150)));

    Assert.assertThat(clientBase.getClientBalance("C2").getStocks().get("A"), Matchers.equalTo(15));
    Assert.assertThat(clientBase.getClientBalance("C2").getAmount(), Matchers.equalTo(BigDecimal.valueOf(150)));
  }

  @Test
  public void addAndProcessBayBigOrder() {
    Order o0 = new Order.Builder()
        .price(BigDecimal.valueOf(5))
        .stockName("A")
        .type(Order.Type.SELL)
        .count(10)
        .clientName("C1")
        .build();

    Order o1 = new Order.Builder()
        .price(BigDecimal.valueOf(5))
        .stockName("A")
        .type(Order.Type.SELL)
        .count(10)
        .clientName("C1")
        .build();

    Order o2 = new Order.Builder()
        .price(BigDecimal.valueOf(5))
        .stockName("A")
        .type(Order.Type.BAY)
        .count(100)
        .clientName("C2")
        .build();

    subj.addAndProcess(o0);
    subj.addAndProcess(o1);
    subj.addAndProcess(o2);

    Assert.assertThat(subj.bay.size(), Matchers.equalTo(1));
    Assert.assertThat(subj.bay.peek().getCount(), Matchers.equalTo(80));
    Assert.assertThat(subj.sell.isEmpty(), Matchers.is(true));

    Assert.assertThat(clientBase.getClientBalance("C1").getStocks().get("A"), Matchers.equalTo(80));
    Assert.assertThat(clientBase.getClientBalance("C1").getAmount(), Matchers.equalTo(BigDecimal.valueOf(200)));

    Assert.assertThat(clientBase.getClientBalance("C2").getStocks().get("A"), Matchers.equalTo(25));
    Assert.assertThat(clientBase.getClientBalance("C2").getAmount(), Matchers.equalTo(BigDecimal.valueOf(100)));
  }

}