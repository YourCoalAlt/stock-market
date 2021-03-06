package ca.maldahleh.stockmarket.utils;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class SortingUtils {
    public static Map sortByValue(Map unsortedMap) {
        Map sortedMap = new TreeMap(new ValueComparator(unsortedMap));
        sortedMap.putAll(unsortedMap);
        return sortedMap;
    }
}

class ValueComparator implements Comparator {
    private Map map;

    public ValueComparator(Map map) {
        this.map = map;
    }

    public int compare(Object keyA, Object keyB) {
        Comparable valueA = (Comparable) map.get(keyA);
        Comparable valueB = (Comparable) map.get(keyB);

        return valueB.compareTo(valueA);
    }
}
