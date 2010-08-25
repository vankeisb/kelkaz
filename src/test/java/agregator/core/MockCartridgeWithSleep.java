package agregator.core;

public class MockCartridgeWithSleep extends MockCartridge {

    public MockCartridgeWithSleep(Agregator<MockCriteria,MockResult> a) {
        super(a);
    }

    @Override
    protected MockResult nextResult() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return super.nextResult();
    }
}
