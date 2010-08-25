package agregator.core;

import agregator.core.Agregator;
import agregator.core.Cartridge;

import java.util.List;

public class MockAgregatorWithError extends Agregator<MockCriteria,MockResult> {

    @Override
    protected void createCartridges(List<Cartridge<MockCriteria,MockResult>> cartridges) {
        cartridges.add(new MockCartridgeWithError(this));
        cartridges.add(new MockCartridgeWithSleep(this));
    }
}
