package agregator.core;

import agregator.util.Logger;

import java.io.*;
import java.util.HashSet;

public class ExcludedResults {

  private static final Logger logger = Logger.getLogger(ExcludedResults.class);

  private final HashSet<String> excludedUrls = new HashSet<String>();
  private final Object lock = new Object();
  private boolean readOnly = false; // If cannot load or create excludedResults.properties -> ReadOnly mode
  private File f;


  public ExcludedResults(){
    f = new File(System.getProperty("user.home")+"/.trouvtoo/excludedResults.properties");
    if (!f.exists()){
      logger.debug("ExcludedResult.properties file doesn't exist -> create it now");
      File parentDir = new File(System.getProperty("user.home")+"/.trouvtoo");
      if (!parentDir.exists()){
        if (!parentDir.mkdirs())
          throw new IllegalAccessError("Cannot create .trouvtoo directory in user home");
      }
      try {
        f.createNewFile();
      } catch (IOException e) {
        readOnly = true;
        e.printStackTrace(); // TODO : error management
      }
    }else{
      logger.debug("ExcludedResult.properties file exists -> Load excluded results");
      try{
        BufferedReader reader = new BufferedReader(new FileReader(f));
        try {

          String line = null;
          while (( line = reader.readLine()) != null){
            excludedUrls.add(line);
          }
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }finally {
          reader.close();
        }

      }catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

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
        BufferedWriter writer = null;
        try {
          writer = new BufferedWriter(new FileWriter(f, true));
          try {
            writer.newLine();
            writer.write(url);
            writer.flush();
          } catch (IOException e) {
            e.printStackTrace();
          }finally {
            writer.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
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
