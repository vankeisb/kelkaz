package agregator.core;

public abstract class Result {

    private Cartridge<?,?> cartridge;

    public Result(Cartridge<?,?> cartridge) {
        if (cartridge==null) {
            throw new IllegalArgumentException("Cartridge cannot be null");
        }
        this.cartridge = cartridge;
    }

    public Cartridge<?,?> getCartridge() {
        return cartridge;
    }

    public abstract String getUrl();

    public abstract String getTitle();

}
