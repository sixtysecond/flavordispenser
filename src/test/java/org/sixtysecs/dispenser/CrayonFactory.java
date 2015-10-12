package org.sixtysecs.dispenser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edriggs on 10/11/15.
 */
public class CrayonFactory implements SelectionFactory<Crayon, CrayonColor> {

    public List<Crayon> create(CrayonColor selection, int count) {
        List<Crayon> crayonList = new ArrayList<Crayon>();
        for (int i = 0; i < count; i++) {
            crayonList.add(new Crayon(selection));
        }
        return crayonList;
    }
}
