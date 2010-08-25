package agregator.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MockCartridge extends Cartridge<MockCriteria,MockResult> {

    private Iterator<MockResult> iterator = null;

    public MockCartridge(Agregator<MockCriteria,MockResult> a) {
        super("MockCartridge", a);
        List<MockResult> results = new ArrayList<MockResult>();
        results.add(new MockResult(this, "result1", "http://www.google.com", "Some text"));
        results.add(new MockResult(this, "result2", "http://www.google.com", "Some text"));
        results.add(new MockResult(this, "result3", "http://www.google.com", "Some text"));
        results.add(new MockResult(this, "result4", "http://www.google.com", "Some text"));
        iterator = results.iterator();
    }


    protected boolean hasMoreResults() {
        return iterator.hasNext();
    }

    protected MockResult nextResult() {
        return iterator.next();
    }
}
