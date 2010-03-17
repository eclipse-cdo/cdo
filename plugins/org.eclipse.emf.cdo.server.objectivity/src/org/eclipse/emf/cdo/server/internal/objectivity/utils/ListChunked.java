/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Ibrahim Sallam - code refactoring for CDO 3.0
 */
package org.eclipse.emf.cdo.server.internal.objectivity.utils;

import org.eclipse.net4j.util.collection.MoveableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public abstract class ListChunked<E> implements MoveableList<E>
{
  private int size;

  private ArrayList<Chunk> chunks = new ArrayList<Chunk>();

  protected abstract Object resolveElement(int index);

  protected ListChunked(int realSize)
  {
    size = realSize;
    chunks.add(createChunk(0, size));
  }

  private Chunk createChunk(int startOrigin, int size)
  {
    return new ChunkProxyImpl(startOrigin, startOrigin, size);
  }

  private Chunk createChunkData(int start, int startOrigin, int size)
  {
    return new ChunkProxyImpl(start, startOrigin, size);
  }

  private int indexOfChunk(int index)
  {
    int low = 0;
    int high = chunks.size() - 1;
    while (low <= high)
    {
      int mid = (low + high) / 2;
      Chunk middleChunk = chunks.get(mid);
      if (middleChunk.getStart() > index)
      {
        high = mid - 1;
      }
      else if (middleChunk.getEnd() <= index)
      {
        low = mid + 1;
      }
      else
      {
        return mid; // found
      }
    }
    throw new ArrayIndexOutOfBoundsException(index);
  }

  public E move(int targetIndex, int sourceIndex)
  {
    int size = size();
    if (sourceIndex >= size)
    {
      throw new IndexOutOfBoundsException("sourceIndex=" + sourceIndex + ", size=" + size);
    }

    if (targetIndex >= size)
    {
      throw new IndexOutOfBoundsException("targetIndex=" + targetIndex + ", size=" + size);
    }

    E object = get(sourceIndex);
    if (targetIndex == sourceIndex)
    {
      return object;
    }
    if (targetIndex < sourceIndex)
    {
      moveUp1(targetIndex, sourceIndex - targetIndex);
    }
    else
    {
      moveDown1(targetIndex, targetIndex - sourceIndex);
    }

    set(targetIndex, object);

    return object;
  }

  private void moveUp1(int index, int count)
  {
    for (int i = count; i > 0; i--)
    {
      set(index + i, get(index + i - 1));
    }
  }

  private void moveDown1(int index, int count)
  {
    for (int i = count; i > 0; i--)
    {
      set(index - i, get(index - i + 1));
    }
  }

  public boolean add(E e)
  {
    add(size, e);
    return true;
  }

  public void validateList()
  {
    for (Chunk chunk : chunks)
    {
      if (chunk.getSize() == 0 || ((ChunkProxyImpl)chunk).getList() != null
          && ((ChunkProxyImpl)chunk).getList().size() == 0 || ((ChunkProxyImpl)chunk).getList() != null
          && ((ChunkProxyImpl)chunk).getList().size() != chunk.getSize())
      {
        System.out.println("Erreur for chunck" + chunk.getStart());
      }
    }
  }

  public void add(int index, E element)
  {
    int indexOfChunk = chunks.size() - 1;
    if (index != size)
    {
      indexOfChunk = indexOfChunk(index);
    }

    Chunk lookupChunk = chunks.get(indexOfChunk);

    // We can only add in a new environment
    if (!lookupChunk.isNew())
    {
      if (index == lookupChunk.getStart())
      {
        // We do not need to split.
        // Look at the previous
        int previousIndexChunk = indexOfChunk - 1;
        if (previousIndexChunk >= 0 && chunks.get(previousIndexChunk).isNew())
        {
          lookupChunk = chunks.get(--indexOfChunk);
        }
        else
        {
          lookupChunk = createChunkData(index, -1, 0);
          chunks.add(indexOfChunk, lookupChunk);
        }
      }
      else if (index == lookupChunk.getEnd())
      {
        int nextIndexChunk = indexOfChunk + 1;
        if (nextIndexChunk < chunks.size() && chunks.get(nextIndexChunk).isNew())
        {
          lookupChunk = chunks.get(++indexOfChunk);
        }
        else
        {
          lookupChunk = createChunkData(index, -1, 0);
          chunks.add(++indexOfChunk, lookupChunk);
        }
      }
      else
      // Middle Case
      {

        // Add the end of the chunk
        lookupChunk = lookupChunk.split(index);
        chunks.add(indexOfChunk + 1, lookupChunk);

        // Add the middle of it
        lookupChunk = createChunkData(index, -1, 0);
        chunks.add(++indexOfChunk, lookupChunk);
      }
    }
    size++;

    int localIndex = index - lookupChunk.getStart();

    lookupChunk.add(localIndex, element);

    for (int i = indexOfChunk + 1; i < chunks.size(); i++)
    {
      Chunk adjustChunk = chunks.get(i);
      adjustChunk.setStart(adjustChunk.getStart() + 1);
    }
  }

  public E set(int index, E element)
  {
    int indexOfChunk = indexOfChunk(index);
    Chunk lookupChunk = chunks.get(indexOfChunk);
    int localIndex = index - lookupChunk.getStart();
    if (lookupChunk.isResolved())
    {
      return (E)lookupChunk.set(localIndex, element);
    }

    int originIndex = lookupChunk.getStartOrigin() + localIndex;
    E oldElement = (E)resolveElement(lookupChunk.getStartOrigin() + index - lookupChunk.getStart());

    if (index == lookupChunk.getStart())
    {
      int previousIndexChunk = indexOfChunk - 1;
      if (previousIndexChunk >= 0 && chunks.get(previousIndexChunk).isResolved())
      {
        adjustStart(indexOfChunk, lookupChunk, 1);
        lookupChunk = chunks.get(--indexOfChunk);
        adjustSize(indexOfChunk, lookupChunk, 1);
      }
      else
      {
        Chunk newChunk = split(lookupChunk, index + 1);
        if (newChunk != null)
        {
          chunks.add(indexOfChunk + 1, newChunk);
        }
      }
    }
    else if (index == lookupChunk.getEnd())
    {
      int nextIndexChunk = indexOfChunk + 1;
      if (nextIndexChunk < chunks.size() && chunks.get(nextIndexChunk).isResolved())
      {
        if (adjustSize(indexOfChunk, lookupChunk, -1))
        {
          ++indexOfChunk;
        }
        lookupChunk = chunks.get(indexOfChunk);
        adjustStart(indexOfChunk, lookupChunk, -1);
      }
      else
      {
        Chunk newChunk = split(lookupChunk, index);
        if (newChunk != null)
        {
          chunks.add(indexOfChunk + 1, newChunk);
          lookupChunk = newChunk;
        }
      }
    }
    else
    // Middle Case
    {
      lookupChunk = split(lookupChunk, index);
      Chunk endChunk = split(lookupChunk, index + 1);

      chunks.add(indexOfChunk + 1, lookupChunk);
      if (endChunk != null)
      {
        chunks.add(indexOfChunk + 2, endChunk);
      }
    }

    lookupChunk.set(index - lookupChunk.getStart(), element);

    return oldElement;
  }

  private Chunk split(Chunk chunk, int index)
  {
    if (chunk.getSize() == 1)
    {
      return null;
    }

    return chunk.split(index);
  }

  private boolean adjustStart(int indexOfChunk, Chunk chunk, int inc)
  {
    if (chunk.getSize() - inc <= 0)
    {
      chunks.remove(indexOfChunk);
      return false;
    }
    ((ChunkProxyImpl)chunk).startAdjust(inc);
    return true;
  }

  private boolean adjustSize(int indexOfChunk, Chunk chunk, int inc)
  {
    if (chunk.getSize() + inc <= 0)
    {
      chunks.remove(indexOfChunk);
      return false;
    }
    ((ChunkProxyImpl)chunk).sizeAdjust(inc);
    return true;
  }

  public boolean addAll(Collection<? extends E> c)
  {
    return false;
  }

  public boolean addAll(int index, Collection<? extends E> c)
  {
    return false;
  }

  public void clear()
  {
  }

  public boolean contains(Object o)
  {
    return false;
  }

  public boolean containsAll(Collection<?> c)
  {
    return false;
  }

  public E get(int index)
  {
    return get(index, true);
  }

  public E get(int index, boolean resolve)
  {
    int indexOfChunk = indexOfChunk(index);

    Chunk chunk = chunks.get(indexOfChunk);

    if (!chunk.isResolved())
    {

      return resolve ? (E)resolveElement(chunk.getStartOrigin() + index - chunk.getStart()) : null;
    }

    return (E)chunk.get(index - chunk.getStart());
  }

  @Override
  public String toString()
  {
    StringBuffer string = new StringBuffer();
    for (int i = 0; i < chunks.size(); i++)
    {
      Chunk adjustChunk = chunks.get(i);
      string.append("[" + adjustChunk.getStart() + "-" + adjustChunk.getEnd() + (adjustChunk.isResolved() ? "*" : "")
          + "]");
    }
    return string.toString();
  }

  public int indexOf(Object o)
  {
    return 0;
  }

  public boolean isEmpty()
  {
    return size == 0;
  }

  public Iterator<E> iterator()
  {
    return null;
  }

  public int lastIndexOf(Object o)
  {
    return 0;
  }

  public ListIterator<E> listIterator()
  {
    return null;
  }

  public ListIterator<E> listIterator(int index)
  {
    return null;
  }

  public boolean remove(Object o)
  {
    return false;
  }

  public E remove(int index)
  {
    int indexOfChunk = indexOfChunk(index);
    Chunk chunk = chunks.get(indexOfChunk);

    Object elementToReturn = null;

    size--;

    if (chunk.isResolved())
    {
      elementToReturn = chunk.remove(index - chunk.getStart());
    }
    else
    {
      elementToReturn = resolveElement(chunk.getStartOrigin() + index - chunk.getStart());
      if (chunk.getEnd() - 1 != index)
      {
        chunks.add(indexOfChunk + 1, chunk.split(index + 1));
      }

      chunk.setSize(chunk.getSize() - 1);

    }

    if (chunk.getSize() == 0)
    {
      chunks.remove(indexOfChunk);
      indexOfChunk--;
    }

    for (int i = indexOfChunk + 1; i < chunks.size(); i++)
    {
      Chunk adjustChunk = chunks.get(i);
      adjustChunk.setStart(adjustChunk.getStart() - 1);
    }

    return (E)elementToReturn;
  }

  public boolean removeAll(Collection<?> c)
  {
    return false;
  }

  public boolean retainAll(Collection<?> c)
  {
    return false;
  }

  public int size()
  {
    return size;
  }

  public List<E> subList(int fromIndex, int toIndex)
  {
    return null;
  }

  public Object[] toArray()
  {
    return null;
  }

  public <T> T[] toArray(T[] a)
  {
    return null;
  }

  /**
   * @author Simon McDuff
   */
  static class ChunkProxyImpl implements Chunk
  {
    private Object list;

    private int startOrigin;

    private int start;

    private int size;

    ChunkProxyImpl(int start, int startOrigin, int size)
    {
      this.startOrigin = startOrigin;
      this.start = start;
      this.size = size;
    }

    public List<Object> getList()
    {
      return list instanceof List ? (List<Object>)list : null;
    }

    public void setList(List<Object> list)
    {
      this.list = list;
    }

    public boolean isResolved()
    {
      return list != null;
    }

    public boolean isNew()
    {
      return startOrigin == -1;
    }

    public int getStartOrigin()
    {
      return startOrigin;
    }

    public void setStartOrigin(int startOrigin)
    {
      this.startOrigin = startOrigin;
    }

    public int getSize()
    {
      return size;
    }

    public void setSize(int size)
    {
      this.size = size;
    }

    public int getStart()
    {
      return start;
    }

    public int getEnd()
    {
      return start + size;
    }

    public int getEndOrigin()
    {
      return startOrigin + size;
    }

    public void setStart(int start)
    {
      this.start = start;
    }

    public void add(int index, Object element)
    {
      if (size == 0)
      {
        list = element;
      }
      else
      {
        List<Object> listToAdd = getList();
        if (listToAdd == null)
        {
          listToAdd = new ArrayList<Object>();
          if (list != null)
          {
            listToAdd.add(list);
          }
          list = listToAdd;
        }
        listToAdd.add(index, element);
      }
      size++;
    }

    public Object set(int index, Object element)
    {
      if (getList() == null)
      {
        Object oldElement = element;
        list = element;
        return oldElement;
      }
      if (index == getList().size())
      {
        getList().add(index, element);
        return null;
      }
      return getList().set(index, element);
    }

    public Object get(int index)
    {
      if (getList() == null)
      {
        return list;
      }
      return getList().get(index);
    }

    public void startAdjust(int inc)
    {
      start += inc;
      if (startOrigin != -1)
      {
        startOrigin += inc;
      }
      if (list != null)
      {
        if (inc > 0)
        {
          remove(0);
        }
        else if (inc < 0)
        {
          add(0, null);
        }
      }
      else
      {
        size -= inc;
      }
    }

    public void sizeAdjust(int inc)
    {
      if (list != null)
      {
        if (inc > 0)
        {
          add(size, null);
        }
        else if (inc < 0)
        {
          remove(size - 1);
        }
      }
      else
      {
        size += inc;
      }
    }

    public Chunk split(int globalIndex)
    {
      int diff = startOrigin - start;
      int newStart = globalIndex;
      int newStartOrigin = isNew() ? -1 : globalIndex + diff;
      int newSize = getEnd() - globalIndex;
      int currentSize = globalIndex - start;
      if (size == 1)
      {
        System.out.println(this);
      }
      ChunkProxyImpl newChunk = new ChunkProxyImpl(newStart, newStartOrigin, newSize);
      if (list != null)
      {
        List<Object> newList = new ArrayList<Object>();
        for (int i = 0; i < newSize; i++)
        {
          Object element = getList().remove(currentSize);
          newList.add(element);
        }
        newChunk.setList(newList);
      }
      if (currentSize == 0 || newSize == 0)
      {
        System.out.println(this);
      }

      setSize(currentSize);
      return newChunk;
    }

    public Object remove(int index)
    {
      size--;
      if (size == 0 && getList() == null)
      {
        Object oldElement = list;
        list = null;
        return oldElement;
      }
      return getList().remove(index);
    }

  };

  /**
   * @author Simon McDuff
   */
  static interface Chunk
  {
    boolean isResolved();

    boolean isNew();

    int getStartOrigin();

    void setStartOrigin(int start);

    int getStart();

    int getEnd();

    int getEndOrigin();

    int getSize();

    void setStart(int start);

    void setSize(int start);

    Chunk split(int index);

    Object get(int index);

    Object set(int index, Object element);

    void add(int index, Object element);

    Object remove(int index);

  }
}
