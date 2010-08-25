package agregator.immo.cartridges

import java.text.SimpleDateFormat
import java.text.DateFormat

class Util {

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

  static Integer extractPrice(String priceStr) {
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

}
