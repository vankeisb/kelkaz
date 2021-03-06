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
    }

    public boolean isAgregating() {
        return agregating;
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

    public Agregator<C,R> agregate(List<C> criterias) {
        checkAgregating();
        List<C> crits = new ArrayList<C>(criterias);
        doneSignal = new CountDownLatch(this.cartridges.size());        
        agregating = true;
        try {

            logger.debug("Begin agregate, notifying listeners start");
            notifyListeners(new AgregatorEvent.StartedEvent(this));

            // launch all cartridges in separate thread,
            // and wait for the result
            for (Cartridge<C,R> c : this.cartridges) {
                logger.debug("Starting cartridge " + c.getName() + " in new thread");
                Worker<C,R> w = new Worker<C,R>(c, crits);
                new Thread(w).start();
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

    public void kill() {
        for (Cartridge<C,R> c : cartridges) {
            logger.debug("Killing " + c.getName());
            c.kill();
        }
    }

    class Worker<C extends Criteria,R extends Result> implements Runnable {

        private final Cartridge<C,R> cartridge;
        private final List<C> criterias;

        Worker(Cartridge<C,R> cartridge, List<C> criterias) {
            this.cartridge = cartridge;
            this.criterias = criterias;
            logger.debug("Worker thread created");
        }

        public void run() {
            try {
                logger.debug("Worker thread " + this + " started");
                cartridge.agregate(criterias);
            } finally {
                logger.debug("Worker thread " + this + " over");
                doneSignal.countDown();
            }
        }
    }

}
