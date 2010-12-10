package agregator.core;

import agregator.util.Logger;

import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Exclusions {

    private static final Logger logger = Logger.getLogger(Exclusions.class);

    Set<String> urls = Collections.synchronizedSet(new HashSet<String>());

    private final Object diskOpLock = new Object();

    private String path = System.getProperty("user.home") + File.separator + ".kelkaz";
    private String fileName;

    public Exclusions() {
        this(null);
    }

    public Exclusions(String basePath) {
        // useful for testing, allows to pass another directory
        if (basePath!=null) {
            path = basePath;
        }
        fileName = path + File.separator + "exclusions.txt";

        // initialize base dir if needed
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
            try {
                new File(fileName).createNewFile();
            } catch(IOException e) {
                logger.error("Unable to create exclusions file " + fileName, e);
                throw new RuntimeException("Unable to create exclusions file " + fileName);
            }
            logger.debug("Exclusions not found in " + fileName);
        } else {
            logger.debug("Loading exclusions from " + fileName);
            try {
                BufferedReader br = new BufferedReader(new FileReader(fileName));
                String line;
                while ((line = br.readLine()) != null) {
                    urls.add(line);
                }
            } catch(Exception e) {
                logger.error("Error reading exclusions ", e);
            }
        }
    }

    public int getNbExclusions() {
        return urls.size();
    }

    public boolean isExcluded(Result result) {
        return urls.contains(result.getUrl());
    }

    public boolean addExclusion(Result result) {

        logger.debug("Adding result to exclusions : " + result);

        String url = result.getUrl();
        if (urls.contains(url)) {
            return false;
        }
        synchronized(diskOpLock) {
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new FileWriter(fileName, true));
                out.write(url + "\n");
                logger.debug("Added url " + url + " to " + fileName);
                urls.add(url);
                return true;
            } catch(Exception e) {
                logger.error("Error while saving exclusion for " + result, e);
                return false;
            } finally {
                if (out!=null) {
                    try {
                        out.flush();
                        out.close();
                    } catch(IOException e) {
                        logger.error("Unable to flush/close " + fileName, e);
                    }
                }
            }
        }
    }

    public void removeExclusion(Result result) {
        logger.debug("Removing from exclusions : " + result);
        String url = result.getUrl();
        if (urls.contains(url)) {
            BufferedWriter out = null;
            urls.remove(url);
            synchronized(diskOpLock) {
                try {
                    out = new BufferedWriter(new FileWriter(fileName));
                    for (String u : urls) {
                        out.write(u + "\n");
                    }
                    logger.debug("Rewrote exclusions file to " + fileName);
                } catch(IOException e) {
                    logger.error("Error while writing exclusions following removal", e);
                } finally {
                    if (out!=null) {
                        try {
                            out.flush();
                            out.close();
                        } catch(IOException e) {
                            logger.error("Unable to flush/close " + path, e);
                        }
                    }
                }
            }
        }
    }
}
