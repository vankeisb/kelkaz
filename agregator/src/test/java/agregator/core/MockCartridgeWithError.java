package agregator.core;

import agregator.core.Cartridge;
import agregator.core.Result;

public class MockCartridgeWithError extends Cartridge<MockCriteria,MockResult> {

    public MockCartridgeWithError(Agregator<MockCriteria,MockResult> a) {
        super("Mock With Error", a);
    }

    @Override
    protected void doAgregate(MockCriteria c) {
        for (int i=0 ; i<4 ; i++) {
            fireResultEvent(new MockResult(this, "result" + i, "url" + i, "text" + i));
        }
        throw new RuntimeException("shit can happen");
    }
}
