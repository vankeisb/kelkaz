package agregator.core;

import agregator.util.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

public abstract class Agregator<C extends Criteria, R extends Result> {

    private static final Logger logger = Logger.getLogger(Agregator.class);

    private List<Cartridge<C,R>> cartridges = Collections.synchronizedList(new ArrayList<Cartridge<C,R>>());
    private List<AgregatorListener> listeners = Collections.synchronizedList(new ArrayList<AgregatorListener>());
    private CountDownLatch doneSignal;
    private volatile boolean agregating = false;
    
    public Agregator() {
        // create the cartridges for this agregator
        createCartridges(cartridges);

        logger.debug("Created agregator with " + cartridges.size() + " cartridges");

        // observe cartridges to translate result events
        for (Cartridge<?,?> c : this.cartridges) {
            c.addListener(new CartridgeListener() {
                public void onEvent(CartridgeEvent e) {
                    // wrap cartridge event into agregator event and notify listeners
                    logger.debug("received cartridge event " + e + ", notifying listeners");
                    AgregatorEvent ae = new AgregatorEvent.AgregatorCartridgeEvent(e);
                    notifyListeners(ae);
                }
            });
        }

        doneSignal = new CountDownLatch(this.cartridges.size());
    }

    public List<Cartridge<C,R>> getCartridges() {
        return Collections.unmodifiableList(cartridges);
    }

    protected abstract void createCartridges(List<Cartridge<C,R>> cartridges);

    private void checkAgregating() {
        if (agregating) {
            throw new IllegalStateException("Currently agregating");
        }
    }


    private void notifyListeners(AgregatorEvent evt) {
        for (AgregatorListener al : listeners) {
            al.onEvent(evt);
        }
    }

    public Agregator<C,R> addListener(AgregatorListener listener) {
        checkAgregating();
        this.listeners.add(listener);
        logger.debug("Listener " + listener + " added");
        return this;
    }

    public Agregator<C,R> removeAllListeners() {
        checkAgregating();
        this.listeners = Collections.synchronizedList(new ArrayList<AgregatorListener>());
        logger.debug("All listeners removed");
        return this;
    }

    public Agregator<C,R> agregate(C criteria) {
        checkAgregating();
        agregating = true;
        try {

            logger.debug("Begin agregate, notifying listeners start");
            notifyListeners(new AgregatorEvent.StartedEvent(this));

            // launch all cartridges in separate thread,
            // and wait for the result
            for (Cartridge<C,R> c : this.cartridges) {
                logger.debug("Starting sartridge " + c.getName() + " in new thread");
                new Thread(new Worker<C,R>(c, criteria)).start();
            }

            // wait for everyone to complete
            try {
                logger.debug("Waiting for cartridges");
                doneSignal.await();
            } catch (InterruptedException e) {
                e.printStackTrace();  // TODO error management
            }

        } finally {
            logger.debug("All cartridges done, notifying listeners end");
            notifyListeners(new AgregatorEvent.EndedEvent(this));
            agregating = false;            
        }
        return this;
    }

    class Worker<C extends Criteria,R extends Result> implements Runnable {

        private final Cartridge<C,R> cartridge;
        private final C criteria;

        Worker(Cartridge<C,R> cartridge, C criteria) {
            this.cartridge = cartridge;
            this.criteria = criteria;
            logger.debug("Worker thread created");
        }

        public void run() {
            try {
                logger.debug("Worker thread " + this + " started");
                cartridge.agregate(criteria);
            } finally {
                logger.debug("Worker thread " + this + " over");
                doneSignal.countDown();
            }
        }
    }

}
