package org.joget.plugin.base;

import java.util.Map;

import org.apache.commons.collections.SequencedHashMap;
import org.apache.commons.collections.map.ListOrderedMap;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;

/**
 * Modified FreeMarker SimpleHash that supports ordering using ListOrderedMaps.
 */
@SuppressWarnings("deprecation")
public class ListOrderedHash extends SimpleHash {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5894893988933372064L;

	public ListOrderedHash() {
        super();
    }

    public ListOrderedHash(@SuppressWarnings("rawtypes") Map map) {
        super(map);
    }

    public ListOrderedHash(ObjectWrapper wrapper) {
        super(wrapper);
    }

    public ListOrderedHash(@SuppressWarnings("rawtypes") Map map, ObjectWrapper wrapper) {
        super(map, wrapper);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    protected Map copyMap(Map map) {
        if (map instanceof ListOrderedMap || map.getClass().getName().equals(ListOrderedMap.class.getName())) {
            Map newMap = new ListOrderedMap();
            newMap.putAll(map);
            return newMap;
        } else if (map instanceof SequencedHashMap || map.getClass().getName().equals(SequencedHashMap.class.getName())) {
            Map newMap = new SequencedHashMap();
            newMap.putAll(map);
            return newMap;
        } else {
            return super.copyMap(map);
        }
    }
}
