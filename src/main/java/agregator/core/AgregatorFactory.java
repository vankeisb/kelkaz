package agregator.core;

public abstract class AgregatorFactory<C extends Criteria,R extends Result> {

    private String id;
    private String description;

    public AgregatorFactory(String id, String description) {
        this.description = description;
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public abstract Agregator<C,R> create();

}
