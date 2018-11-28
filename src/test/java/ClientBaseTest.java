import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.*;

public class ClientBaseTest {

  private ClientBase subj = new ClientBase();

  @Test
  public void initClientBase() throws Exception {
    File clientBase = Paths.get(ClientBaseTest.class.getClassLoader().getResource("clients.txt").toURI()).toFile();
    Map<String, ClientBalance> res = subj.initClientBase(clientBase);
    Assert.assertThat(res, Matchers.hasKey("C1"));
    Assert.assertThat(res, Matchers.hasKey("C2"));
    Assert.assertThat(res, Matchers.hasKey("C4"));
    Assert.assertThat(res.size(), Matchers.equalTo(3));

    ClientBalance c1 = res.get("C1");
    Assert.assertThat(c1.getAmount(), Matchers.equalTo(BigDecimal.valueOf(1000)));
    Assert.assertThat(c1.getStocks(), Matchers.hasEntry("A", 130));
    Assert.assertThat(c1.getStocks(), Matchers.hasEntry("B", 240));
    Assert.assertThat(c1.getStocks(), Matchers.hasEntry("C", 760));
    Assert.assertThat(c1.getStocks(), Matchers.hasEntry("D", 320));

    ClientBalance c2 = res.get("C2");
    Assert.assertThat(c2.getAmount(), Matchers.equalTo(BigDecimal.valueOf(4350)));
    Assert.assertThat(c2.getStocks(), Matchers.hasEntry("A", 370));
    Assert.assertThat(c2.getStocks(), Matchers.hasEntry("B", 120));
    Assert.assertThat(c2.getStocks(), Matchers.hasEntry("C", 950));
    Assert.assertThat(c2.getStocks(), Matchers.hasEntry("D", 560));

    ClientBalance c4 = res.get("C4");
    Assert.assertThat(c4.getAmount(), Matchers.equalTo(BigDecimal.valueOf(560)));
    Assert.assertThat(c4.getStocks(), Matchers.hasEntry("A", 450));
    Assert.assertThat(c4.getStocks(), Matchers.hasEntry("B", 540));
    Assert.assertThat(c4.getStocks(), Matchers.hasEntry("C", 480));
    Assert.assertThat(c4.getStocks(), Matchers.hasEntry("D", 950));
  }
}