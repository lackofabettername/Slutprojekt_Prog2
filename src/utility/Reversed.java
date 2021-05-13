package utility;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Used to loop through a list in reverse order.
 * <pre>
 *  import static Reversed.reversed;
 *  ...
 *  for (class s : reversed(list)) {
 *      ...
 *  }</pre>
 */
public class Reversed<T> implements Iterable<T> {

	//https://stackoverflow.com/a/1098153

	private final List<T> _original;

	public Reversed(List<T> original) {
		_original = original;
	}

	@Override
	public Iterator<T> iterator() {

		final ListIterator<T> i = _original.listIterator(_original.size());

		return new Iterator<>() {

			@Override
			public boolean hasNext() {
				return i.hasPrevious();
			}

			@Override
			public T next() {
				return i.previous();
			}

			@Override
			public void remove() {
				i.remove();
			}
		};
	}

	public static <T> Reversed<T> reversed(List<T> original) {
		return new Reversed<>(original);
	}

	public static <T> Reversed<T> reversed(T[] original) {
		return reversed(Arrays.asList(original));
	}

	public static void main(String[] args) {

		List<String> test = Arrays.asList("a", "b", "c", "d");

		System.out.println("Original:");
		for (String s : test) {
			System.out.println(s);
		}

		System.out.println("Reversed:");
		for (String s : reversed(test)) {
			System.out.println(s);
		}
	}
}
