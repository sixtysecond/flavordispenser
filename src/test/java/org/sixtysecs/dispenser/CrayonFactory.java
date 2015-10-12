package org.sixtysecs.dispenser;

/**
 * Created by edriggs on 10/11/15.
 */
public class CrayonFactory implements SelectionFactory<Crayon,CrayonColor> {
    public Crayon newInstance(CrayonColor selection) {
        return new Crayon(selection);
    }
}
