package org.sixtysecs.dispenser;

import java.util.Queue;

/**
 * A thread-safe blocking dispenser
 *
 * @see SelectionDispenser
 */
public class SimpleSelectionDispenser<T, E> extends AbstractSelectionDispenser<T, E> {

    public T dispense(E selection) {
        Queue<T> queue = inventory.get(selection);
        if (queue == null) {
            return null;
        }
        return queue.poll();
    }

}
