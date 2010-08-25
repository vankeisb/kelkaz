package agregator.ui;

import agregator.core.Criteria;

import javax.swing.*;

public interface SearchPanel<C extends Criteria> {

    JComponent getComponent();

    C getCriteria();

}
