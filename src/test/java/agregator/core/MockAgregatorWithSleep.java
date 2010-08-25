package agregator.core;

import java.util.List;

public class MockAgregatorWithSleep extends Agregator<MockCriteria,MockResult> {

    protected void createCartridges(List<Cartridge<MockCriteria, MockResult>> cartridges) {
        cartridges.add(new MockCartridgeWithSleep(this));
    }
}
