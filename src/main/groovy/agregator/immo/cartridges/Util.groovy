package agregator.immo.cartridges

import java.text.SimpleDateFormat
import java.text.DateFormat
import agregator.util.Logger

class Util {

  private static final Logger logger = Logger.getLogger(Util.class)

  static Date extractDate(String dateStr) {
    if (dateStr==null) {
      return null
    }
    StringBuilder sb = new StringBuilder()
    for (int i=0; i<dateStr.length(); i++) {
      Character c = dateStr.charAt(i)
      if (c.isDigit() || c=='/') {
        sb.append(c)
      }
    }
    dateStr = sb.toString()
    try {
      DateFormat df = new SimpleDateFormat('dd/MM/yyyy')
      return df.parse(dateStr)
    } catch(Exception e) {
      return null
    } 
  }

  static Integer extractInteger(String priceStr) {
    if (priceStr==null) {
      return 0
    }
    StringBuilder filtered = new StringBuilder()
    for (int i=0 ; i<priceStr.length() ; i++) {
      Character c = priceStr.charAt(i)
      if (c=='.' || c==',') {
        break
      } else if (c.isDigit()) {
        filtered << c
      }
    }
    String s = filtered.toString()
    if (s.length()>0) {
      try {
        return Integer.parseInt(s)
      } catch(NumberFormatException e) {
        return 0
      }
    } else {
      return 0
    }
  }

  static void sleepRandomTime() {
    Random r = Random.newInstance()
    int delay = r.nextInt(10000)
    if (delay<5000) {
      delay = 10000 - delay
    }
    try {
      logger.debug("Sleeping for $delay ms")
      Thread.sleep(delay)
    } catch(Exception e) {
      // do nothing
    }
  }

}