package org.sixtysecs.dispenser.crayon;

import org.sixtysecs.dispenser.AbstractSelectionFactory;

/**
 * Example factory for refilling selection dispenser
 */
public class CrayonFactory extends AbstractSelectionFactory<Crayon, CrayonColor> {

    @Override
    public Crayon createItem(CrayonColor selection) {
        return new Crayon(selection);
    }
}
