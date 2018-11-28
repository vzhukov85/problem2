import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.*;

public class ExchangeTest {

  private Exchange subj;
  private ClientBase clientBase;

  @Before
  public void setUp() throws Exception {
    File clientBaseFile = Paths.get(ExchangeTest.class.getClassLoader().getResource("clients.txt").toURI()).toFile();

    clientBase = new ClientBase();
    clientBase.initClientBase(clientBaseFile);
    subj = new Exchange(clientBase);
  }

  @Test
  public void processOrderList() throws Exception {
    File orders = Paths.get(ExchangeTest.class.getClassLoader().getResource("orders.txt").toURI()).toFile();

    subj.processOrderList(orders);

    assertStockQueues();
    assertStockA();
    assertStockC();
    assertStockD();

    assertC1();
    assertC2();
  }

  private void assertStockQueues() {
    Assert.assertThat(subj.ordersQueues.size(), Matchers.equalTo(3));
    Assert.assertThat(subj.ordersQueues, Matchers.hasKey("A"));
    Assert.assertThat(subj.ordersQueues, Matchers.hasKey("C"));
    Assert.assertThat(subj.ordersQueues, Matchers.hasKey("D"));
  }

  private void assertStockA() {
    StockOrdersQueue stockOrdersQueueA = subj.ordersQueues.get("A");
    Assert.assertThat(stockOrdersQueueA.sell.size(), Matchers.is(1));
    Assert.assertThat(stockOrdersQueueA.sell.peek().getPrice(), Matchers.equalTo(BigDecimal.valueOf(15)));
    Assert.assertThat(stockOrdersQueueA.sell.peek().getCount(), Matchers.equalTo(3));
    Assert.assertThat(stockOrdersQueueA.sell.peek().getClientName(), Matchers.equalTo("C1"));
    Assert.assertThat(stockOrdersQueueA.bay.isEmpty(), Matchers.is(true));
  }

  private void assertStockC() {
    StockOrdersQueue stockOrdersQueueC = subj.ordersQueues.get("C");
    Assert.assertThat(stockOrdersQueueC.sell.isEmpty(), Matchers.is(true));
    Assert.assertThat(stockOrdersQueueC.bay.size(), Matchers.equalTo(1));
    Assert.assertThat(stockOrdersQueueC.bay.peek().getPrice(), Matchers.equalTo(BigDecimal.valueOf(15)));
    Assert.assertThat(stockOrdersQueueC.bay.peek().getCount(), Matchers.equalTo(2));
    Assert.assertThat(stockOrdersQueueC.bay.peek().getClientName(), Matchers.equalTo("C2"));
  }

  private void assertStockD() {
    StockOrdersQueue stockOrdersQueueD = subj.ordersQueues.get("D");
    Assert.assertThat(stockOrdersQueueD.sell.isEmpty(), Matchers.is(true));
    Assert.assertThat(stockOrdersQueueD.bay.size(), Matchers.equalTo(1));
    Assert.assertThat(stockOrdersQueueD.bay.peek().getPrice(), Matchers.equalTo(BigDecimal.valueOf(4)));
    Assert.assertThat(stockOrdersQueueD.bay.peek().getCount(), Matchers.equalTo(3));
    Assert.assertThat(stockOrdersQueueD.bay.peek().getClientName(), Matchers.equalTo("C2"));
  }

  private void assertC1() {
    ClientBalance c1 = clientBase.getClientBalance("C1");
    Assert.assertThat(c1.getAmount(), Matchers.equalTo(BigDecimal.valueOf(1045)));
    Assert.assertThat(c1.getStocks().get("A"), Matchers.equalTo(130	));
    Assert.assertThat(c1.getStocks().get("B"), Matchers.equalTo(240));
    Assert.assertThat(c1.getStocks().get("C"), Matchers.equalTo(757	));
    Assert.assertThat(c1.getStocks().get("D"), Matchers.equalTo(320	));
  }

  private void assertC2() {
    ClientBalance c2 = clientBase.getClientBalance("C2");
    Assert.assertThat(c2.getAmount(), Matchers.equalTo(BigDecimal.valueOf(4305)));
    Assert.assertThat(c2.getStocks().get("A"), Matchers.equalTo(370	));
    Assert.assertThat(c2.getStocks().get("B"), Matchers.equalTo(120));
    Assert.assertThat(c2.getStocks().get("C"), Matchers.equalTo(953	));
    Assert.assertThat(c2.getStocks().get("D"), Matchers.equalTo(560	));
  }

}