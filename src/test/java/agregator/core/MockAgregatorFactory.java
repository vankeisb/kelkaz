package agregator.core;

import agregator.core.Agregator;
import agregator.core.AgregatorFactory;

public class MockAgregatorFactory extends AgregatorFactory {

    public MockAgregatorFactory(String id) {
        super(id, "MockDescription");
    }

    public Agregator create() {
        return new MockAgregator();
    }
}
