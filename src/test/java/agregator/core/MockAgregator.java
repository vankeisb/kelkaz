package agregator.core;

import agregator.core.Agregator;
import agregator.core.Cartridge;

import java.util.List;

public class MockAgregator extends Agregator<MockCriteria,MockResult> {

    protected void createCartridges(List<Cartridge<MockCriteria,MockResult>> cartridges) {
        cartridges.add(new MockCartridge(this));
    }
}
