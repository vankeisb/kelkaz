package agregator.core;

public abstract class Result {

    private Cartridge<?,?> cartridge;

    public Result(Cartridge<?,?> cartridge) {
        this.cartridge = cartridge;
    }

    public Cartridge<?,?> getCartridge() {
        return cartridge;
    }

    public abstract String getUrl();

    public abstract String getTitle();

}
