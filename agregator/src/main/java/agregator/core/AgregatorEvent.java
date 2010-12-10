package agregator.core;

public abstract class AgregatorEvent {

    private final Agregator<?,?> agregator;

    public AgregatorEvent(Agregator<?,?> agregator) {
        this.agregator = agregator;
    }

    public Agregator<?,?> getSource() {
        return agregator;
    }

    public static class StartedEvent extends AgregatorEvent {

        public StartedEvent(Agregator<?,?> crAgregator) {
            super(crAgregator);
        }

        @Override
        public String toString() {
            return "[AgregatorEvent.StartedEvent agregator=" + getSource() + "]";
        }

    }


    public static class AgregatorCartridgeEvent extends AgregatorEvent {

        private CartridgeEvent cartridgeEvent;

        public AgregatorCartridgeEvent(CartridgeEvent cartridgeEvent) {
            super(cartridgeEvent.getSource().getAgregator());
            this.cartridgeEvent = cartridgeEvent;
        }

        public CartridgeEvent getCartridgeEvent() {
            return cartridgeEvent;
        }

        @Override
        public String toString() {
            return "[AgregatorEvent.CartridgeEvent agregator=" + getSource() + ", nested=" + getCartridgeEvent() + "]";
        }

    }

    public static class EndedEvent extends AgregatorEvent {

        public EndedEvent(Agregator<?,?> crAgregator) {
            super(crAgregator);
        }

        @Override
        public String toString() {
            return "[AgregatorEvent.EndedEvent agregator=" + getSource() + "]";
        }

    }

}
