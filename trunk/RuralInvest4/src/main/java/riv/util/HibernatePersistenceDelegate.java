package riv.util;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.internal.PersistentIdentifierBag;
import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.internal.PersistentMap;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.collection.internal.PersistentSortedMap;
import org.hibernate.collection.internal.PersistentSortedSet;
 
/**
 * An extension of {@link PersistenceDelegate PersistenceDelegate}
 * that handles Hibernate collections classes. These classes cannot normally
 * be instantiated by the application, so they don't pass successfully through
 * an XML encoded stream. On the other hand, Hibernate will let you replace
 * them with the usual collections classes from <code>java.util</code>. So
 * that's what we do here.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class HibernatePersistenceDelegate extends DefaultPersistenceDelegate {
                                                                               
    static HibernatePersistenceDelegate INSTANCE
      = new HibernatePersistenceDelegate();
 
    static final Map CLASS_MAP;
    static {
        HashMap map = new HashMap();
//        map.put(PersistentBag.class, ArrayList.class);
//        map.put(org.hibernate.collection.PersistentIdentifierBag.class,
//          ArrayList.class);
//        map.put(org.hibernate.collection.PersistentList.class, ArrayList.class);
        map.put(PersistentMap.class, HashMap.class);
        map.put(PersistentSet.class, HashSet.class);
        map.put(PersistentSortedMap.class, TreeMap.class);
        map.put(PersistentSortedSet.class, TreeSet.class);
        CLASS_MAP = Collections.synchronizedMap(map);
    }
 
    private HibernatePersistenceDelegate() {
    }
 
    /**
     * Get the set of delegates to install in an
     * {@link java.beans.Encoder Encoder}
     *  to properly handle Hibernate collections types. This method returns
     * the set as a mapping from <code>Class</code> -&gt;
     * <code>PersistenceDelegate</code>.
     */
    public static Map getDelegates() {
        HashMap map = new HashMap();
        for (Iterator i = CLASS_MAP.keySet().iterator(); i.hasNext(); )
            map.put(i.next(), INSTANCE);
        return map;
    }
 
    /**
     * Overridden to handle conversion of Hibernate collection
     * classes into <code>java.util</code> collection classes.
     */
    protected Expression instantiate(Object oldInstance, Encoder out) {
        Class replaceType = (Class)CLASS_MAP.get(oldInstance.getClass());
        if (replaceType != null) {
            return new Expression(oldInstance,
              replaceType, "new", new Object[0]);
        }
        return super.instantiate(oldInstance, out);
    }
 
    /**
     * Overridden to handle conversion of Hibernate collection
     * classes into <code>java.util</code> collection classes.
     */
    protected boolean mutatesTo(Object oldInstance, Object newInstance) {
        if (oldInstance != null && newInstance != null
          && CLASS_MAP.get(oldInstance.getClass()) == newInstance.getClass())
            return true;
        return super.mutatesTo(oldInstance, newInstance);
    }
 
    /**
     * Overridden to handle conversion of Hibernate collection
     * classes into <code>java.util</code> collection classes.
     */
    protected void initialize(Class type, Object oldInstance,
      Object newInstance, Encoder out) {
        Class replaceType = (Class)CLASS_MAP.get(oldInstance.getClass());
        if (replaceType != null) {
            Object replace = replace(oldInstance);
            if (type == oldInstance.getClass())
                type = replace.getClass();
            super.initialize(type, replace, newInstance, out);
        }
        super.initialize(type, oldInstance, newInstance, out);
    }
 
    Object replace(Object obj) {
        if (obj instanceof PersistentSet)
            return new HashSet((Set)obj);
        else if (obj instanceof PersistentMap)
            return new HashMap((Map)obj);
        else if (obj instanceof PersistentSortedSet)
            return new TreeSet((SortedSet)obj);
        else if (obj instanceof PersistentSortedMap)
            return new TreeMap((SortedMap)obj);
        else if (obj instanceof PersistentList
          || obj instanceof PersistentBag
          || obj instanceof PersistentIdentifierBag)
            return new ArrayList((Collection)obj);
        throw new RuntimeException("unexpected");
    }
} 