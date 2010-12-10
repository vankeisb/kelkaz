package agregator.ui

import static agregator.ui.Util.*

class UtilTest extends GroovyTestCase {

  private void assertExtractInteger(int expected, String s) {
    int actual = extractInteger(s)
    assert expected == actual, "extraction failed for $s"
  }

  public void testIsDigit() {
    char c = '('
    assert !c.isDigit()
  }

  public void testExtractInteger() {
    assertExtractInteger(100000, '100 000 € (1)')
    assertExtractInteger(1000, "1000 euros")
    assertExtractInteger(100000, "Ca fera 100 000 euros")
    assertExtractInteger(100000, """
									100 000 € (1)									""")

  }

}
