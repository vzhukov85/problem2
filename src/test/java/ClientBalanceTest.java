import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class ClientBalanceTest {
  private ClientBalance subj;

  @Before
  public void setUp() {
    ClientBalance.Builder builder = ClientBalance.newBuilder();
    subj = builder.amount(BigDecimal.valueOf(10))
        .stock("A", 10)
        .stock("B", 20)
        .stock("C", 30)
        .stock("D", 0)
        .build();
  }

  @Test
  public void bay() {
    subj.bay("A", 2, 2);
    subj.bay("B", BigDecimal.valueOf(1.5), 1);
    Assert.assertThat(subj.getAmount(), Matchers.equalTo(BigDecimal.valueOf(4.5)));
    Assert.assertThat(subj.getStocks().get("A"), Matchers.equalTo(12));
    Assert.assertThat(subj.getStocks().get("B"), Matchers.equalTo(21));
  }

  @Test
  public void bayNewStock() {
    subj.bay("E", 2, 2);
    Assert.assertThat(subj.getAmount(), Matchers.equalTo(BigDecimal.valueOf(6)));
    Assert.assertThat(subj.getStocks().get("E"), Matchers.equalTo(2));
  }

  @Test
  public void sell() {
    subj.sell("A", 2, 2);
    subj.sell("B", BigDecimal.valueOf(1.5), 1);
    Assert.assertThat(subj.getAmount(), Matchers.equalTo(BigDecimal.valueOf(15.5)));
    Assert.assertThat(subj.getStocks().get("A"), Matchers.equalTo(8));
    Assert.assertThat(subj.getStocks().get("B"), Matchers.equalTo(19));
  }

  @Test
  public void sellAbsentStock() {
    try {
      subj.sell("E", 2, 2);
      Assert.assertThat(true, Matchers.is(false));
    } catch (IllegalArgumentException e) {
      Assert.assertThat(e.getMessage(), Matchers.equalTo("У клиента нет заявленных акций"));
    }
  }

  @Test
  public void sellMoreStock() {
    try {
      subj.sell("D", 2, 2);
      Assert.assertThat(true, Matchers.is(false));
    } catch (IllegalArgumentException e) {
      Assert.assertThat(e.getMessage(), Matchers.equalTo("У клиента меньше акций, чем заявлено на продажу"));
    }
  }

  @Test
  public void builderNegativeAmount() {
    try {
      ClientBalance.Builder builder = ClientBalance.newBuilder();
      builder.amount(BigDecimal.valueOf(-1));
      Assert.assertThat(true, Matchers.is(false));
    } catch (IllegalArgumentException e) {
      Assert.assertThat(e.getMessage(), Matchers.equalTo("Не допускается указывать отрицательный баланс"));
    }
  }

  @Test
  public void builderNegativeCountStock() {
    try {
      ClientBalance.Builder builder = ClientBalance.newBuilder();
      builder.stock("A", -10);
      Assert.assertThat(true, Matchers.is(false));
    } catch (IllegalArgumentException e) {
      Assert.assertThat(e.getMessage(), Matchers.equalTo("Не допускается указывать отрицательное количество акций"));
    }
  }
}