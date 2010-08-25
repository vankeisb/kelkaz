package agregator.immo;

import agregator.core.Criteria;

public class ImmoCriteria implements Criteria {

    public static enum Demand {
        SELL, RENT
    }

    private Demand demand;

    public static enum Type {
        APPT, MAISON, GARAGE, MES_COUILLES
    }

    private Type type;
    private Integer nbRoomsMin;
    private Integer nbRoomsMax;
    private Integer surfaceMin;
    private Integer surfaceMax;
    private Integer priceMin;
    private Integer priceMax;

    // TODO recup liste pays qq part et transformer en enum
    static enum Country {
        FRANCE, BELGIQUE, BLAH
    }

    private Country country;
    private String city;
    private String postCode;
    private Integer searchRadius;
    private Boolean newsOnly;
    private Boolean withPhotosOnly;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Demand getDemand() {
        return demand;
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }

    public Integer getNbRoomsMax() {
        return nbRoomsMax;
    }

    public void setNbRoomsMax(Integer nbRoomsMax) {
        this.nbRoomsMax = nbRoomsMax;
    }

    public Integer getNbRoomsMin() {
        return nbRoomsMin;
    }

    public void setNbRoomsMin(Integer nbRoomsMin) {
        this.nbRoomsMin = nbRoomsMin;
    }

    public Boolean isNewsOnly() {
        return newsOnly;
    }

    public void setNewsOnly(Boolean newsOnly) {
        this.newsOnly = newsOnly;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public Integer getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(Integer priceMax) {
        this.priceMax = priceMax;
    }

    public Integer getPriceMin() {
        return priceMin;
    }

    public void setPriceMin(Integer priceMin) {
        this.priceMin = priceMin;
    }

    public Integer getSearchRadius() {
        return searchRadius;
    }

    public void setSearchRadius(Integer searchRadius) {
        this.searchRadius = searchRadius;
    }

    public Integer getSurfaceMax() {
        return surfaceMax;
    }

    public void setSurfaceMax(Integer surfaceMax) {
        this.surfaceMax = surfaceMax;
    }

    public Integer getSurfaceMin() {
        return surfaceMin;
    }

    public void setSurfaceMin(Integer surfaceMin) {
        this.surfaceMin = surfaceMin;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Boolean isWithPhotosOnly() {
        return withPhotosOnly;
    }

    public void setWithPhotosOnly(Boolean withPhotosOnly) {
        this.withPhotosOnly = withPhotosOnly;
    }
}