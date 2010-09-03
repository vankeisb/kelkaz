package agregator.core;

import java.util.HashSet;

public class ExcludedResults {

  private final HashSet<String> excludedUrls = new HashSet<String>();
  private final Object lock = new Object();

  private void assertNotNull(Result r) {
    if (r==null) {
      throw new IllegalArgumentException("result cannot be null");
    }
  }

  public void addExclusion(Result r) {
    String url = r.getUrl();
    synchronized (lock) {
      if (!excludedUrls.contains(url)) {
        excludedUrls.add(url);
      }
    }
  }

  public boolean isExcluded(Result r) {
    String url = r.getUrl();
    if (url==null) {
      return false;
    }
    synchronized (lock) {
      return excludedUrls.contains(url);
    }
  }

  public int getNbExcluded() {
    synchronized (lock) {
      return excludedUrls.size();
    }
  }
}
