package agregator.core;

public abstract class CartridgeEvent {

    private final Cartridge<?,?> source;

    public CartridgeEvent(Cartridge<?,?> source) {
        this.source = source;
    }

    public Cartridge<?,?> getSource() {
        return source;
    }

    public static class StartedEvent extends CartridgeEvent {

        public StartedEvent(Cartridge<?,?> source) {
            super(source);
        }

        @Override
        public String toString() {
            return "[CartridgeEvent.StartedEvent cartridge=" + getSource() + "]";
        }

    }

    public static class ResultEvent extends CartridgeEvent {

        private final Result result;

        public ResultEvent(Result result) {
            super(result.getCartridge());
            this.result = result;
        }

        public Result getResult() {
            return result;
        }

        @Override
        public String toString() {
            return "[CartridgeEvent.ResultEvent cartridge=" + getSource() + ", result=" + getResult() + "]";
        }

    }

    public static class EndedEvent extends CartridgeEvent {

        public EndedEvent(Cartridge<?,?> source) {
            super(source);
        }

        @Override
        public String toString() {
            return "[CartridgeEvent.EndedEvent cartridge=" + getSource() + "]";
        }
    }

    public static class ErrorEvent extends CartridgeEvent {

        private Throwable t;

        public ErrorEvent(Cartridge<?,?> source, Throwable cause) {
            super(source);
            t = cause;
        }

        public Throwable getCause() {
            return t;
        }

        @Override
        public String toString() {
            return "[CartridgeEvent.ErrorEvent cartridge=" + getSource() + ", cause=" + getCause() + "]";
        }
    }

}
