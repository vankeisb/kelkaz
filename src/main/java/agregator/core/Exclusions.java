package agregator.core;

import agregator.util.Logger;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Exclusions<R extends Result> {

    private static final Logger logger = Logger.getLogger(Exclusions.class);

    private List<R> exclusions = new ArrayList<R>();

    private String path = System.getProperty("user.dir") + File.separator + ".kelkaz" + File.separator + "exclusions";
    private File baseDir;
    private List<Cartridge<?,?>> cartridges;

    protected Exclusions(String basePath, List<Cartridge<?,?>> allCartridges) {

        cartridges = allCartridges;

        // useful for testing, allows to pass another directory
        if (basePath!=null) {
            path = basePath;
        }

        // initialize base dir if needed
        baseDir = new File(path);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        logger.debug("Loading exclusions from " + path + ", cartridges = " + cartridges);

        // scan the dir for exclusions
        File[] files = baseDir.listFiles();
        for (File f : files) {
            try {
                logger.debug("  * Handling file " + f.getAbsolutePath());
                R result = deserializeResult(new FileInputStream(f));
                if (result!=null) {
                    logger.debug("  * => deserialized result" + result);
                    exclusions.add(result);
                } else {
                    logger.warn("undable to deserialize result from file " + f.getAbsolutePath());
                }
            } catch(FileNotFoundException e) {
                // should not happen : for now we just skip that result
            }
        }

        if (exclusions==null) {
            exclusions = Collections.emptyList();
        }
    }

    protected Cartridge<?,?> getCartridge(String name) {
        for (Cartridge<?,?> c : cartridges) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public List<R> getExclusions() {
        return Collections.unmodifiableList(exclusions);
    }

    public boolean isExcluded(R result) {
        return exclusions.contains(result);
    }

    public synchronized boolean addExclusion(R result) {

        logger.debug("Adding result to exclusions : " + result);

        if (exclusions.contains(result)) {
            return false;
        }
        String encodedUrl;
        try {
            encodedUrl = URLEncoder.encode(result.getUrl(), "utf-8");
        } catch(UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String fileName = path + File.separator + encodedUrl;
        logger.debug("  * file name : " + fileName);
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            if (serializeResult(result, fos)) {
                logger.debug("  => result serialized to " + fileName);
                exclusions.add(result);
                return true;
            } else {
                logger.warn("Unable to serialize result to " + fileName + ", result " + result);
                return false;
            }
        } catch(FileNotFoundException fnfex) {
            // should never happen so we throw a runtime ex
            throw new RuntimeException(fnfex);
        }
    }

    public synchronized void removeExclusion(R result) {

        logger.debug("Removing from exclusions : " + result);

        // find the file
        String url;
        try {
            url = URLEncoder.encode(result.getUrl(), "utf-8");
        } catch(UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String fileName = path + File.separator + url;
        logger.debug("   * file name " + fileName);
        File f = new File(fileName);
        if (f.exists()) {
            // remove the file
            f.delete();
        } else {
            logger.warn("No file found for result " + result + ", path supposed to be " + fileName);
        }
        exclusions.remove(result);
    }

    protected abstract R deserializeResult(InputStream is);

    protected abstract boolean serializeResult(R result, OutputStream os);

}
