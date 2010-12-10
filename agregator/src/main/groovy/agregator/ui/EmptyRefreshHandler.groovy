package agregator.ui

import com.gargoylesoftware.htmlunit.RefreshHandler
import com.gargoylesoftware.htmlunit.Page

class EmptyRefreshHandler implements RefreshHandler {

  void handleRefresh(Page page, URL url, int i) {
     // do nothing 
  }


}
