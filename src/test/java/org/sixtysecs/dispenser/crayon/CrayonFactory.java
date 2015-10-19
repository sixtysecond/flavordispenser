package org.sixtysecs.dispenser.crayon;

import org.sixtysecs.dispenser.SelectionFactory;

/**
 * Example factory for refilling selection dispenser
 */
public class CrayonFactory implements SelectionFactory<Crayon, CrayonColor> {

    public Crayon createItem(CrayonColor selection) {
        return new Crayon(selection);
    }
}
