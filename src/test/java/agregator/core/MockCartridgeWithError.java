package agregator.core;

import agregator.core.Cartridge;
import agregator.core.Result;

public class MockCartridgeWithError extends Cartridge<MockCriteria,MockResult> {

    private int i = 0;

    public MockCartridgeWithError(Agregator<MockCriteria,MockResult> a) {
        super("Mock With Error", a);
    }

    protected boolean hasMoreResults() {
        return i<10;
    }

    protected MockResult nextResult() {
        if (i++ < 5) {
            return new MockResult(this, "result" + i, "url" + i, "text" + i);
        } else {
            throw new RuntimeException("shit can happen");
        }
    }
}
