package agregator.immo

import agregator.core.Cartridge
import agregator.core.MockCartridge

class ImmoExclusionsTest extends GroovyTestCase {

  String tmpDir = System.getProperty('java.io.tmpdir') + File.separator + 'testExclusions'
  MockCartridge mc = new MockCartridge(null)
  List<Cartridge<?, ?>> cartridges = [mc]


  void testIt() {
    // remove previous exclusions if any
    rmdir(new File(tmpDir))

    // create and assert empty
    ImmoExclusions ie = new ImmoExclusions(tmpDir, cartridges)
    assert ie.exclusions.size() == 0

    // add exclusions
    def fakeResults = []
    for (int i=0 ; i<10 ; i++) {
      def r = new ImmoResult(mc, "title me $i", "http://foobar.com/$i", "description me < <foo> $i", 101, new Date(), "http://other/$i")
      ie.addExclusion(r)
      fakeResults << r
    }

    // assert exclusions
    assert ie.exclusions.size() == 10
    fakeResults.each { r ->
      assert ie.isExcluded(r)
    }

    // assert not excluded
    def notExcluded = new ImmoResult(mc, "title me", "http://foobarsqdqdqsddqs", "description me", 101, new Date(), "http://other/")
    assert !ie.isExcluded(notExcluded)

    // assert remove exclusion
    def removedFromExclusions = fakeResults[0]
    ie.removeExclusion removedFromExclusions
    assert !ie.isExcluded(removedFromExclusions)
    assert 9 == ie.exclusions.size()

    // assert exclusions are re-loaded from the file
    ie = new ImmoExclusions(tmpDir, cartridges)
    assert 9 == ie.exclusions.size()
    assert !ie.isExcluded(removedFromExclusions)
    assert ie.isExcluded(fakeResults[1])

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
