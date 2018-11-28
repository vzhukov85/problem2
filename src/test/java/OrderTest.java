import com.sun.org.apache.xpath.internal.operations.Or;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.regex.Matcher;

import static org.junit.Assert.*;

public class OrderTest {

  private Order o1;
  private Order o2;
  private Order o3;

  @Test
  public void compareToSell() {
    o1 = new Order.Builder()
        .price(BigDecimal.ONE)
        .type(Order.Type.SELL)
        .build();

    o2 = new Order.Builder()
        .price(BigDecimal.TEN)
        .type(Order.Type.SELL)
        .build();

    o3 = new Order.Builder()
        .price(BigDecimal.ONE)
        .type(Order.Type.SELL)
        .build();

    Assert.assertThat(o1.compareTo(o2), Matchers.lessThan(0));
    Assert.assertThat(o2.compareTo(o1), Matchers.greaterThan(0));
    Assert.assertThat(o1.compareTo(o3), Matchers.lessThan(0));
    Assert.assertThat(o1.compareTo(o1), Matchers.equalTo(0));
  }

  @Test
  public void compareToBay() {
    o1 = new Order.Builder()
        .price(BigDecimal.ONE)
        .type(Order.Type.BAY)
        .build();

    o2 = new Order.Builder()
        .price(BigDecimal.TEN)
        .type(Order.Type.BAY)
        .build();

    o3 = new Order.Builder()
        .price(BigDecimal.ONE)
        .type(Order.Type.BAY)
        .build();

    Assert.assertThat(o1.compareTo(o2), Matchers.greaterThan(0));
    Assert.assertThat(o2.compareTo(o1), Matchers.lessThan(0));
    Assert.assertThat(o1.compareTo(o3), Matchers.lessThan(0));
    Assert.assertThat(o1.compareTo(o1), Matchers.equalTo(0));
  }

  @Test
  public void processBay() {
    o1 = new Order.Builder()
        .type(Order.Type.BAY)
        .count(10)
        .stockName("A")
        .clientName("C1")
        .build();

    ClientBase clientBase = new ClientBase();

    clientBase.addClientBalance(new ClientBalance.Builder()
        .name("C1")
        .amount(BigDecimal.valueOf(10))
        .stock("A", 10)
        .stock("B", 15)
        .build()
    );

    o1.process(clientBase, BigDecimal.ONE, 5);

    ClientBalance c1 = clientBase.getClientBalance("C1");
    Assert.assertThat(c1.getAmount(), Matchers.equalTo(BigDecimal.valueOf(5)));
    Assert.assertThat(c1.getStocks().get("A"), Matchers.equalTo(15));
    Assert.assertThat(o1.getCount(), Matchers.equalTo(5));

  }

  @Test
  public void processSell() {
    o2 = new Order.Builder()
        .type(Order.Type.SELL)
        .count(10)
        .stockName("A")
        .clientName("C2")
        .build();

    ClientBase clientBase = new ClientBase();

    clientBase.addClientBalance(new ClientBalance.Builder()
        .name("C2")
        .amount(BigDecimal.valueOf(10))
        .stock("A", 10)
        .stock("B", 15)
        .build()
    );

    o2.process(clientBase, BigDecimal.ONE, 5);

    ClientBalance c2 = clientBase.getClientBalance("C2");
    Assert.assertThat(c2.getAmount(), Matchers.equalTo(BigDecimal.valueOf(15)));
    Assert.assertThat(c2.getStocks().get("A"), Matchers.equalTo(5));
    Assert.assertThat(o2.getCount(), Matchers.equalTo(5));
  }

}