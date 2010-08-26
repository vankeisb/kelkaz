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
    private final String photoUrl;

    public ImmoResult(
      Cartridge cartridge,
      String title,
      String url,
      String description,
      Integer price,
      Date date,
      String photoUrl) {
        super(cartridge);
        this.title = title;
        this.url = url;
        this.description = description;
        this.price = price;
        this.date = date;
        this.photoUrl = photoUrl;
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

  public String getPhotoUrl() {
    return photoUrl;
  }

  /*
  List<String> getPhotosUrls();

  ImmoCriteria.Country getCountry();

  String getCity();

  String getPostCode();
  */
    

}
