package agregator.core;

import agregator.core.Result;

public class MockResult extends Result {

    private String title;
    private String url;
    private String text;

    public MockResult(Cartridge c,String title, String url, String text) {
        super(c);
        this.title = title;
        this.url = url;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getText() {
        return text;
    }
}
