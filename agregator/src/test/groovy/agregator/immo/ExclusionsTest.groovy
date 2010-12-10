package agregator.immo

import agregator.core.MockCartridge
import agregator.core.Exclusions

class ExclusionsTest extends GroovyTestCase {

  String tmpDir = System.getProperty('java.io.tmpdir') + File.separator + 'testExclusions'
  MockCartridge mc = new MockCartridge(null)

  void testIt() {
    // remove previous exclusions if any
    rmdir(new File(tmpDir))

    // create and assert empty
    Exclusions e = new Exclusions(tmpDir)
    assert e.nbExclusions == 0

    // add exclusions
    def fakeResults = []
    for (int i=0 ; i<10 ; i++) {
      def r = new ImmoResult(mc, "title me $i", "http://foobar.com/$i", "description me < <foo> $i", 101, new Date(), "http://other/$i")
      e.addExclusion(r)
      fakeResults << r
    }

    // assert exclusions
    assert e.nbExclusions == 10
    fakeResults.each { r ->
      assert e.isExcluded(r)
    }

    // assert not excluded
    def notExcluded = new ImmoResult(mc, "title me", "http://foobarsqdqdqsddqs", "description me", 101, new Date(), "http://other/")
    assert !e.isExcluded(notExcluded)

    // assert remove exclusion
    def removedFromExclusions = fakeResults[0]
    e.removeExclusion removedFromExclusions
    assert !e.isExcluded(removedFromExclusions)
    assert 9 == e.nbExclusions

    // assert exclusions are re-loaded from the file
    e = new Exclusions(tmpDir)
    assert 9 == e.nbExclusions
    assert !e.isExcluded(removedFromExclusions)
    assert e.isExcluded(fakeResults[1])

  }

  private void rmdir(File f) {
    if (f.exists()) {
      File[] childs = f.listFiles()
      for (File child : childs) {
        rmdir child
      }
      f.delete()
    }
  }

}
