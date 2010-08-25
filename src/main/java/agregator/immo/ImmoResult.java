package agregator.immo;

import agregator.core.Result;
import agregator.core.Cartridge;

import java.util.Date;


public class ImmoResult extends Result {

    private final String url;
    private final String title;
    private final String description;
    private final Integer price;
    private final Date date;

    public ImmoResult(
      Cartridge cartridge,
      String title,
      String url,
      String description,
      Integer price,
      Date date) {
        super(cartridge);
        this.title = title;
        this.url = url;
        this.description = description;
        this.price = price;
        this.date = date;
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

  public Integer getPrice() {
    return price;
  }

  public Date getDate() {
    return date;
  }

  /*
  List<String> getPhotosUrls();

  ImmoCriteria.Country getCountry();

  String getCity();

  String getPostCode();
  */
    

}
