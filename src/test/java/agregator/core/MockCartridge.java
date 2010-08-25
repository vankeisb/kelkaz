package agregator.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MockCartridge extends Cartridge<MockCriteria,MockResult> {

    protected final List<MockResult> results = new ArrayList<MockResult>();

    public MockCartridge(Agregator<MockCriteria,MockResult> a) {
        super("MockCartridge", a);
        results.add(new MockResult(this, "result1", "http://www.google.com", "Some text"));
        results.add(new MockResult(this, "result2", "http://www.google.com", "Some text"));
        results.add(new MockResult(this, "result3", "http://www.google.com", "Some text"));
        results.add(new MockResult(this, "result4", "http://www.google.com", "Some text"));
    }

    @Override
    protected void doAgregate() {
      for (MockResult r : results) {
        fireResultEvent(r);
      }
    }
}
