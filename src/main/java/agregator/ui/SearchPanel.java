package agregator.ui;

import agregator.core.Criteria;

import javax.swing.*;
import java.util.List;

public interface SearchPanel<C extends Criteria> {

    JComponent getComponent();

    List<C> getCriterias();

}
