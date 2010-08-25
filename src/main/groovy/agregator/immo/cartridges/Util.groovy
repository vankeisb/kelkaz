package agregator.immo.cartridges

class Util {

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
