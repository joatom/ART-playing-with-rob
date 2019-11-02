package de.joatom.imageProcessor.image.factory.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * @author Johannes Tomasoni
 *
 */
public class Histogram {

	private TreeMap<Integer, Integer> histogram = new TreeMap<Integer, Integer>();

	public Histogram() {
	}

	public int add(int color) {
		int quantity = 0;
		if (histogram.get(color) != null)
			quantity = histogram.get(color) + 1;
		else
			quantity = 1;
		histogram.put(color, quantity);
		return quantity;
	}

	// color-int, quantity
	public Set<Entry<Integer, Integer>> getData() {
		return histogram.entrySet();
	}

	public int firstKey() {
		return histogram.firstKey();
	}

	public int lastKey() {
		return histogram.lastKey();
	}

	public int range() {
		return histogram.lastKey() - histogram.firstKey();
	}

	public int[] getTopByQuantityGray(int top) {
		SortedSet<Entry<Integer, Integer>> sortedSet = entriesSortedByValues(histogram);
		top = (top <= sortedSet.size()) ? top : sortedSet.size();
		int[] values = new int[top];
		int i = 0;
		for (final Iterator<Entry<Integer, Integer>> it = sortedSet
				.iterator(); it.hasNext();) {
			if (i >= top) {
				break;
			}
			Entry<Integer, Integer> entry = it.next();
			values[i] = entry.getKey();
			i++;
		}
		return values;
	}

	private <K, V extends Comparable<? super V>> SortedSet<Entry<K, V>> entriesSortedByValues(
			Map<K, V> map) {
		SortedSet<Entry<K, V>> sortedEntries = new TreeSet<Entry<K, V>>(
				new Comparator<Entry<K, V>>() {
					//@Override
					public int compare(Entry<K, V> e1, Entry<K, V> e2) {
						return e2.getValue().compareTo(e1.getValue());
					}
				});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

}
