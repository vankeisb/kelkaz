package agregator.core;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class MockTest extends TestCase {

    class MyCartridgeListener implements CartridgeListener {

        private List<CartridgeEvent> events = new ArrayList<CartridgeEvent>();

        public void onEvent(CartridgeEvent e) {
            events.add(e);
            System.out.println(e);
        }

        public List<CartridgeEvent> getEvents() {
            return events;
        }
    }

    public void testMockCartridgeWithListener() {
        MyCartridgeListener l = new MyCartridgeListener();
        MockCartridge c = new MockCartridge(new MockAgregator());
        c.addListener(l);
        c.agregate(Arrays.asList(new MockCriteria()));
        assertEquals(6, l.getEvents().size());
    }

    class MyAgregatorListener implements AgregatorListener {

        private List<AgregatorEvent> evts = new ArrayList<AgregatorEvent>();

        public void onEvent(AgregatorEvent e) {
            evts.add(e);
            System.out.println(e);
        }

        public List<AgregatorEvent> getEvents() {
            return evts;
        }
    }


    public void testMockAgregator() {
        MockAgregator a = new MockAgregator();
        MyAgregatorListener l = new MyAgregatorListener();
        a.addListener(l).agregate(Arrays.asList(new MockCriteria()));
        assertEquals(8, l.getEvents().size());
    }

    public void testMockWithError() {
        Agregator<MockCriteria,MockResult> a = new MockAgregatorWithError();
        MyAgregatorListener l = new MyAgregatorListener();
        a.addListener(l).agregate(Arrays.asList(new MockCriteria()));
        // expected evts : agr start/stop (2) + mock evts (4) + start/stop failing cartridge (2) + result evts (5) + error event (1)
        assertEquals(14, l.getEvents().size());
    }

    class EmptyAgregator extends Agregator<MockCriteria,MockResult> {
        protected void createCartridges(List<Cartridge<MockCriteria, MockResult>> cartridges) {
            // do nothing            
        }
    }

    public void testCartridgeAgregateTwiceThrowsException() {
        // use cartridge with sleep otherwise it's too fast !
        final MockCartridge c = new MockCartridgeWithSleep(new EmptyAgregator());
        final MockCriteria crit = new MockCriteria();
        final CountDownLatch latch = new CountDownLatch(2);
        final Vector<Throwable> v = new Vector<Throwable>();
        for (int i=0 ; i<2 ; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        c.agregate(Arrays.asList(crit));
                    } catch(Throwable t) {
                        v.add(t);
                    } finally {
                        latch.countDown();
                    }
                }
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(1, v.size());
        assertTrue(v.get(0) instanceof IllegalStateException);
    }

    public void testCartridgeAddListenerWhileAgregatingThrowsException() {
        final MockCartridge c = new MockCartridgeWithSleep(new EmptyAgregator());
        final MockCriteria crit = new MockCriteria();
        new Thread(new Runnable() {
            public void run() {
                c.agregate(Arrays.asList(crit));
            }
        }).start();
        // wait for our thread to start...
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            c.addListener(new CartridgeListener() {
                public void onEvent(CartridgeEvent e) {
                    fail("should never receive events !");
                }
            });
            fail("should have thrown");
        } catch(Throwable t) {
            assertTrue(t instanceof IllegalStateException);
        }
    }

    public void testAgregatorAgregateTwiceThrowsException() {
        // use cartridge with sleep otherwise it's too fast !
        final MockAgregatorWithSleep c = new MockAgregatorWithSleep();
        final MockCriteria crit = new MockCriteria();
        final CountDownLatch latch = new CountDownLatch(2);
        final Vector<Throwable> v = new Vector<Throwable>();
        for (int i=0 ; i<2 ; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        c.agregate(Arrays.asList(crit));
                    } catch(Throwable t) {
                        v.add(t);
                    } finally {
                        latch.countDown();
                    }
                }
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(1, v.size());
        assertTrue(v.get(0) instanceof IllegalStateException);
    }

    public void testAgregatorAddListenerWhileAgregatingThrowsException() {
        final MockAgregatorWithSleep c = new MockAgregatorWithSleep();
        final MockCriteria crit = new MockCriteria();
        new Thread(new Runnable() {
            public void run() {
                c.agregate(Arrays.asList(crit));
            }
        }).start();
        // wait for our thread to start...
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            c.addListener(new AgregatorListener() {
                public void onEvent(AgregatorEvent e) {
                    fail("should never receive events !");
                }
            });
            fail("should have thrown");
        } catch(Throwable t) {
            assertTrue(t instanceof IllegalStateException);
        }
    }

    public void testKill() {
        final MockAgregatorWithSleep c = new MockAgregatorWithSleep();
        final MockCriteria crit = new MockCriteria();
        MyAgregatorListener l = new MyAgregatorListener();
        c.addListener(l);
        new Thread(new Runnable() {
            public void run() {
                c.agregate(Arrays.asList(crit));
            }
        }).start();
        // wait for our thread to start...
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        // kill agregator
        c.kill();

        // store result count

        // wait a bit to make sure all threads have died
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        int nbEvents = l.getEvents().size();

        // wait a bit to make sure all threads have died
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        assertEquals(nbEvents, l.getEvents().size());
    }


}
