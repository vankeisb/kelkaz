package agregator.search

import agregator.core.Result
import agregator.core.Cartridge

public class SearchEngineResult extends Result {

  private String title
  private String shortText
  private String url

  public SearchEngineResult(Cartridge c, String title, String shortText, String url) {
    super(c)
    this.title = title
    this.shortText = shortText
    this.url = url
  }

  public String getTitle() {
    return title
  }

  public String getUrl() {
    return url
  }

  public String getShortText() {
    return shortText
  }


}