package edacc.model;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Generic object cache class
 * To be able to cache a type T, T has to implement the IntegerPKModel interface
 * which guarantees an int getId() method which has to be the primary key of T.
 *
 * Internally, objects are stored in a hash table, so T also has to implement
 * hashCode() and equals().
 *
 * @author daniel
 * @param <T> Type that will be stored in the cache
 */
public class ObjectCache<T extends IntegerPKModel> {
    private Hashtable<Integer, T> cache = new Hashtable<Integer, T>();

    /**
     * returns a cached object by its primary key id
     * or null, if no such object exists in the cache
     * @param id
     * @return
     */
    protected T getCached(int id) {
        return cache.get(id);
    }

    /**
     * inserts an object into the cache or returns immediately, if
     * the object already exists
     * @param i
     */
    protected void cache(T i) {
        if (cache.containsKey(i.getId())) {
            return;
        } else {
            cache.put(i.getId(), i);
        }
    }

    /**
     * removes an object from the cache
     * @param i
     */
    protected void remove(T i) {
        cache.remove(i.getId());
    }

    /**
     * returns an enumeration of all elements in the cache
     * @return
     */
    protected Enumeration<T> elements() {
        return cache.elements();
    }
}
