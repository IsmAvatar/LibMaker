/*
 * Copyright (C) 2012 IsmAvatar <IsmAvatar@gmail.com>
 * Copyright (C) 2009 Quadduc <quadduc@gmail.com>
 * 
 * This file is part of LibMaker.
 * LibMaker is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.libmaker.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import javax.swing.event.EventListenerList;

public interface ObservableList<K> extends List<K>
	{
	void addListUpdateListener(ListUpdateListener l);

	void removeListUpdateListener(ListUpdateListener l);

	public static interface ListUpdateListener extends EventListener
		{
		void listUpdate(ListUpdateEvent evt);
		}

	public static class ListUpdateEvent extends EventObject
		{
		private static final long serialVersionUID = 1L;

		public enum Type
			{
			ADDED,REMOVED,CHANGED
			}

		public final ListUpdateEvent.Type type;
		public final int fromIndex, toIndex;

		public ListUpdateEvent(Object s, ListUpdateEvent.Type t, int from, int to)
			{
			super(s);
			type = t;
			fromIndex = from;
			toIndex = to;
			}
		}

	//Implementation
	public static class ActiveArrayList<E> extends ArrayList<E> implements ObservableList<E>
		{
		private static final long serialVersionUID = 1L;
		protected EventListenerList listenerList = new EventListenerList();

		public boolean add(E e)
			{
			int i = size();
			super.add(e);
			fire(ListUpdateEvent.Type.ADDED,i,i);
			return true;
			}

		public void add(int index, E element)
			{
			super.add(index,element);
			fire(ListUpdateEvent.Type.ADDED,index,index);
			}

		@Override
		public boolean addAll(Collection<? extends E> c)
			{
			int s = size();
			if (super.addAll(c))
				{
				fire(ListUpdateEvent.Type.ADDED,s,size() - 1);
				return true;
				}
			return false;
			}

		@Override
		public boolean addAll(int index, Collection<? extends E> c)
			{
			int s = size();
			if (super.addAll(index,c))
				{
				fire(ListUpdateEvent.Type.ADDED,index,index + size() - s - 1);
				return true;
				}
			return false;
			}

		@Override
		public void clear()
			{
			int s = size();
			super.clear();
			fire(ListUpdateEvent.Type.REMOVED,0,s - 1);
			}

		@Override
		public E remove(int index)
			{
			E e = super.remove(index);
			fire(ListUpdateEvent.Type.REMOVED,index,index);
			return e;
			}

		@Override
		public boolean remove(Object o)
			{
			int i = indexOf(o);
			if (i >= 0)
				{
				super.remove(i);
				fire(ListUpdateEvent.Type.REMOVED,i,i);
				return true;
				}
			return false;
			}

		@Override
		public boolean removeAll(Collection<?> c)
			{
			int s = c.size();
			if (s == 0) return false;
			if (s == 1) return remove(c.iterator().next());
			if (super.removeAll(c))
				{
				fire(ListUpdateEvent.Type.CHANGED,0,Integer.MAX_VALUE);
				return true;
				}
			return false;
			}

		@Override
		public boolean retainAll(Collection<?> c)
			{
			if (super.retainAll(c))
				{
				fire(ListUpdateEvent.Type.CHANGED,0,Integer.MAX_VALUE);
				return true;
				}
			return false;
			}

		public E set(int index, E element)
			{
			E e = super.set(index,element);
			fire(ListUpdateEvent.Type.CHANGED,index,index);
			return e;
			}

		/**
		 * Note that changes to the returned sublist may not trigger an update.<p>
		 * {@inheritDoc}
		 */
		@Override
		public List<E> subList(int fromIndex, int toIndex)
			{
			return super.subList(fromIndex,toIndex);
			}

		public void addListUpdateListener(ListUpdateListener l)
			{
			listenerList.add(ListUpdateListener.class,l);
			}

		public void removeListUpdateListener(ListUpdateListener l)
			{
			listenerList.remove(ListUpdateListener.class,l);
			}

		protected void fire(ListUpdateEvent.Type type, int from, int to)
			{
			Object[] listeners = listenerList.getListenerList();
			ListUpdateEvent e = null;

			for (int i = listeners.length - 2; i >= 0; i -= 2)
				if (listeners[i] == ListUpdateListener.class)
					{
					if (e == null) e = new ListUpdateEvent(this,type,from,to);
					((ListUpdateListener) listeners[i + 1]).listUpdate(e);
					}
			}
		}
	}