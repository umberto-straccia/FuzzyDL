
package fuzzydl.graph;

import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 *  The <tt>Stack</tt> class represents a last-in-first-out (LIFO) stack of generic items.
 *  It supports the usual <em>push</em> and <em>pop</em> operations, along with methods
 *  for peeking at the top item, testing if the stack is empty, and iterating through
 *  the items in LIFO order.
 *  <p>
 *  All stack operations except iteration are constant time.
 *  <p>
 *  For additional documentation, see <a href="/algs4/13stacks">Section 1.3</a> of
 *  <i>Algorithms in Java, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 */
class Stack<Item> implements Iterable<Item>
{
	private int N;		  // size of the stack
	private Node first;	 // top of stack

	// helper linked list class
	private class Node
	{
		private Item item;
		private Node next;
	}

	/**
	 * Create an empty stack.
	 */
	public Stack()
	{
		first = null;
		N = 0;
	}

	/**
	 * Is the stack empty?
	 */
	public boolean isEmpty()
	{
		return first == null;
	}

	/**
	 * Return the number of items in the stack.
	 */
	public int size()
	{
		return N;
	}

	/**
	 * Add the item to the stack.
	 */
	public void push(Item item)
	{
		Node oldfirst = first;
		first = new Node();
		first.item = item;
		first.next = oldfirst;
		N++;
	}

	/**
	 * Delete and return the item most recently added to the stack.
	 * Throw an exception if no such item exists because the stack is empty.
	 */
	public Item pop()
	{
		if (isEmpty()) throw new RuntimeException("Stack underflow");
		Item item = first.item;		// save item to return
		first = first.next;			// delete first node
		N--;
		return item;				   // return the saved item
	}


	/**
	 * Return the item most recently added to the stack.
	 * Throw an exception if no such item exists because the stack is empty.
	 */
	public Item peek()
	{
		if (isEmpty()) throw new RuntimeException("Stack underflow");
		return first.item;
	}


	/**
	 * Return an iterator to the stack that iterates through the items in LIFO order.
	 */
	public Iterator<Item> iterator()
	{
		return new LIFOIterator();
	}


	private class LIFOIterator implements Iterator<Item> 
	{

		private Node current = first;

		public boolean hasNext()
		{
			return current != null;
		}
		
		
		// Not implemented since it's optional
		public void remove()
		{
			throw new UnsupportedOperationException();
		}


		public Item next()
		{
			if (!hasNext())
				throw new NoSuchElementException();
			Item item = current.item;
			current = current.next; 
			return item;
		}

	}

}

