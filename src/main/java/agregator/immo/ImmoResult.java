package agregator.immo;

import agregator.core.Result;
import agregator.core.Cartridge;


public class ImmoResult extends Result {

    private String url;
    private String title;
    private String description;

    public ImmoResult(Cartridge cartridge, String title, String url, String description) {
        super(cartridge);
        this.title = title;
        this.url = url;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ImmoResult");
        sb.append("{title='").append(title).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append('}');
        return sb.toString();
    }
    
    /*
    List<String> getPhotosUrls();

    String getDescription();

    Date getDate();

    Integer getPrice();

    ImmoCriteria.Country getCountry();

    String getCity();

    String getPostCode();
    */
    

}
