package agregator.core;

import agregator.util.Logger;

import javax.swing.*;
import java.net.URL;
import java.util.*;

/**
 * Base class for cartridges, grabbing results from one site, based on passed
 * criteria. Implements an observable pattern that allows the caller to
 * be notified of the results.
 *  
 */
public abstract class Cartridge<C extends Criteria, R extends Result> {

    private static final String DEFAULT_ICON_NAME = "/defaultCatridgeIcon.gif";

    private static final Logger logger = Logger.getLogger(Cartridge.class);

    private final List<CartridgeListener> listeners = Collections.synchronizedList(new ArrayList<CartridgeListener>());
    private final Agregator<C,R> agregator;
    private final String name;
    private volatile boolean agregating = false;
    private volatile boolean killed = false;

    private ImageIcon imageIcon = null;

    protected Cartridge(String name, Agregator<C,R> agregator) {
        this.name = name;
        this.agregator = agregator;
        logger.debug("Created cartridge '" + name + "' for agregator " + agregator);
    }

    public String getName() {
        return name;
    }

    private void checkAgregation() {
        if (agregating) {
            throw new IllegalStateException("Currently agregating !");
        }
    }

    /**
     * Add passed listener to the cartridge.
     * @param listener the listener to be added (can't be null).
     * @return the cartridge (this)
     */
    public Cartridge<C,R> addListener(CartridgeListener listener) {
        if (listener==null) {
            throw new IllegalArgumentException("listener can't be null");
        }
        checkAgregation();
        listeners.add(listener);
        logger.debug("Added listener " + listener + " to cartridge " + name);
        return this;
    }

    private void invokeListeners(CartridgeEvent e) {
        for (CartridgeListener cl : listeners) {
            cl.onEvent(e);
        }
    }

    /**
     * Start agregation for passed criterias : obtains the data for all passed criterias
     * and the target of this cartridge, and notifies registered observers on start/results/end.
     * @param criterias the list of criterias
     */
    public final void agregate(List<C> criterias) {
        logger.debug(name +  " start aggregating");
        checkAgregation();
        killed = false;
        agregating = true;
        List<C> crits = new ArrayList<C>(criterias);
        try {
            // invoke listeners start
            logger.debug(name +  " invoking listeners start");
            invokeListeners(new CartridgeEvent.StartedEvent(this));

            // loop on agregation results while we found
            // some and invoke listeners
            for (C crit : crits) {
                logger.debug("calling agregation routine for criteria " + crit);
                if (!isKilled()) {
                    doAgregate(crit);
                }
            }

            // invoke listeners stop
            logger.debug(name + " invoking listeners end");
            invokeListeners(new CartridgeEvent.EndedEvent(this));
        } catch(Throwable t) {
            logger.error("Error while agregating", t);
            invokeListeners(new CartridgeEvent.ErrorEvent(this, t));
        } finally {
            agregating = false;
        }
    }

    /**
     * To be implemented by concrete subclasses : performs agregation for passed criteria and fires
     * result events
     */
    protected abstract void doAgregate(C criteria);

    /**
     * @return the agregator passed at construction time
     */
    public Agregator<C,R> getAgregator() {
        return agregator;
    }

    protected void fireResultEvent(R result) {
      invokeListeners(new CartridgeEvent.ResultEvent(result));
    }

    public ImageIcon getIcon() {
        if (imageIcon==null) {
            String iconName = "/" + getClass().getName() + ".gif";
            URL u = getClass().getResource(iconName);
            if (u==null) {
                u = getClass().getResource(DEFAULT_ICON_NAME);
                if (u==null) {
                    throw new IllegalStateException("Could not find icon for cartridge " + this);
                }
            }
            imageIcon = new ImageIcon(u);
        }
        return imageIcon;
    }

    public void kill() {
        killed = true;
    }

    protected final boolean isKilled() {
        return killed;
    }


    private static final int SLEEP_DELAY = 500;

    /**
     * Utility method that can be called by cartridges in order to
     * simulate "real" navigation.
     * sleeps the current thread for a random period between
     * 5 and 10 secs, but returns fast if the cartridge is killed.
     */
    protected void sleepRandomTime() {
      Random r = new Random();
      int delay = r.nextInt(10000);
      if (delay<5000) {
        delay = 10000 - delay;
      }
      try {
          int nbSleeps = delay / SLEEP_DELAY;
          int totalSleep = nbSleeps * SLEEP_DELAY;
          logger.debug("Sleeping for " + totalSleep + " ms");
          for (int i=0 ; i<nbSleeps && !killed; i++) {
              Thread.sleep(SLEEP_DELAY);
          }
      } catch(Exception e) {
        // do nothing
      }
    }

}
