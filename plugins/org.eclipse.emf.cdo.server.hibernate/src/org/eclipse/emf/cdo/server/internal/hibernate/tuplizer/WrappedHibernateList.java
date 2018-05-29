/*
 * Copyright (c) 2008-2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial api
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStoreAccessor;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStoreChunkReader;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateThreadContext;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.proxy.HibernateProxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Wraps a moveable list so that hibernate always sees an object view while cdo always sees a cdoid view. The same for
 * EEnum: cdo wants to see an int (the ordinal), hibernate the real eenum value. This to support querying with EENum
 * parameters.
 *
 * @author Martin Taal
 */
public class WrappedHibernateList implements InternalCDOList
{
  private List<Object> delegate;

  private boolean frozen;

  private int cachedSize = -1;

  private final EStructuralFeature eFeature;

  private final InternalCDORevision owner;

  private Chunk cachedChunk;

  private int currentListChunk = -1;

  private boolean resolveCDOID;

  public WrappedHibernateList(InternalCDORevision owner, EStructuralFeature eFeature)
  {
    this.owner = owner;
    this.eFeature = eFeature;
    final HibernateStoreAccessor accessor = HibernateThreadContext.getCurrentStoreAccessor();
    if (accessor != null)
    {
      currentListChunk = accessor.getCurrentListChunk();
    }
    resolveCDOID = !HibernateUtil.getInstance().isCDOResourceContents(eFeature) && eFeature instanceof EReference;
  }

  public void move(int newPosition, Object object)
  {
    checkFrozen();
    move(newPosition, indexOf(object));
  }

  public Object move(int targetIndex, int sourceIndex)
  {
    checkFrozen();
    int size = size();
    if (sourceIndex >= size)
    {
      throw new IndexOutOfBoundsException("sourceIndex=" + sourceIndex + ", size=" + size); //$NON-NLS-1$ //$NON-NLS-2$
    }

    if (targetIndex >= size)
    {
      throw new IndexOutOfBoundsException("targetIndex=" + targetIndex + ", size=" + size); //$NON-NLS-1$ //$NON-NLS-2$
    }

    Object object = get(sourceIndex);
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

  /**
   * There's a duplicate of this method in CDOListImpl!!!
   */
  public boolean adjustReferences(CDOReferenceAdjuster adjuster, EStructuralFeature feature)
  {
    boolean changed = false;

    CDOType type = CDOModelUtil.getType(feature);
    int size = size();
    for (int i = 0; i < size; i++)
    {
      Object element = get(i);
      Object newID = type.adjustReferences(adjuster, element, feature, i);
      if (newID != element) // Just an optimization for NOOP adjusters
      {
        set(i, newID);
        changed = true;
      }
    }

    return changed;
  }

  /**
   * Not loaded and not loadable anymore because the collection is disconnected
   */
  public boolean isUninitializedCollection()
  {
    // note the getDelegate checks if the underlying persistentcollection
    // is loaded or connected
    final Object theDelegate = getDelegate();
    if (theDelegate instanceof UninitializedCollection)
    {
      return true;
    }

    if (theDelegate instanceof WrappedHibernateList)
    {
      return ((WrappedHibernateList)theDelegate).isUninitializedCollection();
    }
    return false;
  }

  public InternalCDOList clone(EClassifier classifier)
  {
    CDOType type = CDOModelUtil.getType(classifier);
    int size = size();
    InternalCDOList list = (InternalCDOList)CDOListFactory.DEFAULT.createList(size, 0, 0);
    for (int i = 0; i < size; i++)
    {
      list.add(type.copyValue(get(i)));
    }
    return list;
  }

  /**
   * @return the delegate
   */
  public List<Object> getDelegate()
  {
    // if we got disconnected then internally use a new autoexpanding list
    if (delegate instanceof AbstractPersistentCollection && !((AbstractPersistentCollection)delegate).wasInitialized() && !isConnectedToSession())
    {
      delegate = new UninitializedCollection<Object>()
      {
        private static final long serialVersionUID = 1L;

        @Override
        public Object set(int index, Object element)
        {
          ensureSize(index);
          return super.set(index, element);
        }

        @Override
        public Object get(int index)
        {
          ensureSize(index);
          final Object o = super.get(index);
          if (o == null)
          {
            return CDORevisionUtil.UNINITIALIZED;
          }
          return o;
        }

        private void ensureSize(int index)
        {
          if (index >= size())
          {
            for (int i = size() - 1; i <= index; i++)
            {
              add(null);
            }
          }
        }

      };
    }

    return delegate;
  }

  protected boolean isConnectedToSession()
  {
    final AbstractPersistentCollection persistentCollection = (AbstractPersistentCollection)delegate;
    final SessionImplementor session = persistentCollection.getSession();
    return session != null && session.isOpen() && session.getPersistenceContext().containsCollection(persistentCollection);
  }

  /**
   * @param delegate
   *          the delegate to set
   */
  public void setDelegate(List<Object> delegate)
  {
    this.delegate = delegate;
  }

  private static Object convertToCDO(Object value)
  {
    if (value == null)
    {
      return null;
    }

    if (value instanceof CDORevision || value instanceof HibernateProxy)
    {
      return HibernateUtil.getInstance().getCDOID(value);
    }

    if (value instanceof EEnumLiteral)
    {
      return ((EEnumLiteral)value).getValue();
    }

    return value;
  }

  private static List<Object> convertToCDO(List<?> ids)
  {
    List<Object> result = new ArrayList<Object>();
    for (Object o : ids)
    {
      result.add(convertToCDO(o));
    }

    return result;
  }

  protected Object getHibernateValue(Object o)
  {
    if (o instanceof CDOIDExternal)
    {
      return o;
    }

    if (o instanceof CDOID && resolveCDOID)
    {
      return HibernateUtil.getInstance().getCDORevision((CDOID)o);
    }

    return o;
  }

  protected List<Object> getHibernateValues(Collection<?> c)
  {
    List<Object> newC = new ArrayList<Object>();
    for (Object o : c)
    {
      newC.add(getHibernateValue(o));
    }

    return newC;
  }

  public void add(int index, Object element)
  {
    checkFrozen();
    getDelegate().add(index, getHibernateValue(element));
  }

  public boolean add(Object o)
  {
    checkFrozen();
    return getDelegate().add(getHibernateValue(o));
  }

  public boolean addAll(Collection<? extends Object> c)
  {
    checkFrozen();
    return getDelegate().addAll(getHibernateValues(c));
  }

  public boolean addAll(int index, Collection<? extends Object> c)
  {
    checkFrozen();
    return getDelegate().addAll(index, getHibernateValues(c));
  }

  public void clear()
  {
    checkFrozen();
    getDelegate().clear();
  }

  public boolean contains(Object o)
  {
    return getDelegate().contains(getHibernateValue(o));
  }

  public boolean containsAll(Collection<?> c)
  {
    return getDelegate().containsAll(getHibernateValues(c));
  }

  public Object get(int index)
  {
    Object delegateValue = getDelegate().get(index);

    // not loaded, force the load
    if (delegateValue == CDORevisionUtil.UNINITIALIZED)
    {
      delegateValue = getChunkedValue(index);
    }

    if (delegateValue instanceof CDOID)
    {
      return delegateValue;
    }

    return convertToCDO(delegateValue);
  }

  public Object get(int index, boolean resolve)
  {
    Object delegateValue = getDelegate().get(index);

    // if resolve==false then the caller can handle uninitialized objects.
    if (!resolve && delegateValue == CDORevisionUtil.UNINITIALIZED)
    {
      return CDORevisionUtil.UNINITIALIZED;
    }

    // else force the load
    return get(index);
  }

  private Object getChunkedValue(int index)
  {
    readChunk(index);
    if (cachedChunk != null)
    {
      // note index must be within the range as the chunk
      // is read again if index is too large.
      return cachedChunk.get(index - cachedChunk.getStartIndex());
    }
    return null;
  }

  private void readChunk(int index)
  {
    if (cachedChunk != null)
    {
      if (cachedChunk.getStartIndex() <= index && index < cachedChunk.getStartIndex() + cachedChunk.size())
      {
        // a valid chunk
        return;
      }
      // a not valid chunk reread it
      // TODO: cache chunks also
      cachedChunk = null;
    }
    final HibernateStoreAccessor accessor = HibernateThreadContext.getCurrentStoreAccessor();
    if (accessor == null)
    {
      return;
    }

    // read in batches always
    // if the currentListChunk is not set then read a sizeable chunk
    int chunkSize = Math.max(100, currentListChunk);
    final HibernateStoreChunkReader chunkReader = accessor.createChunkReader(owner, eFeature);
    chunkReader.addRangedChunk(index, index + chunkSize);
    cachedChunk = chunkReader.executeRead().get(0);
  }

  public int indexOf(Object o)
  {
    return getDelegate().indexOf(getHibernateValue(o));
  }

  public boolean isEmpty()
  {
    return getDelegate().isEmpty();
  }

  public Iterator<Object> iterator()
  {
    return new CDOHibernateIterator(getDelegate().iterator());
  }

  public int lastIndexOf(Object o)
  {
    return getDelegate().lastIndexOf(getHibernateValue(o));
  }

  public ListIterator<Object> listIterator()
  {
    return new CDOHibernateListIterator(this, getDelegate().listIterator());
  }

  public ListIterator<Object> listIterator(int index)
  {
    return new CDOHibernateListIterator(this, getDelegate().listIterator(index));
  }

  public Object remove(int index)
  {
    checkFrozen();
    return getDelegate().remove(index);
  }

  public boolean remove(Object o)
  {
    checkFrozen();
    return getDelegate().remove(getHibernateValue(o));
  }

  public boolean removeAll(Collection<?> c)
  {
    checkFrozen();
    return getDelegate().removeAll(getHibernateValues(c));
  }

  public boolean retainAll(Collection<?> c)
  {
    return getDelegate().retainAll(getHibernateValues(c));
  }

  public Object set(int index, Object element)
  {
    checkFrozen();

    if (element == CDORevisionUtil.UNINITIALIZED)
    {
      return null;
    }

    return getDelegate().set(index, getHibernateValue(element));
  }

  public int size()
  {
    if (cachedSize != -1)
    {
      return cachedSize;
    }
    if (getDelegate() instanceof AbstractPersistentCollection)
    {
      final AbstractPersistentCollection collection = (AbstractPersistentCollection)getDelegate();
      if (collection.wasInitialized())
      {
        cachedSize = -1;
        return getDelegate().size();
      }
      final SessionImplementor session = collection.getSession();
      CollectionEntry entry = session.getPersistenceContext().getCollectionEntry(collection);
      CollectionPersister persister = entry.getLoadedPersister();
      if (collection.hasQueuedOperations())
      {
        session.flush();
      }
      cachedSize = persister.getSize(entry.getLoadedKey(), session);
      return cachedSize;
    }

    return getDelegate().size();
  }

  public List<Object> subList(int fromIndex, int toIndex)
  {
    return convertToCDO(getDelegate().subList(fromIndex, toIndex));
  }

  public Object[] toArray()
  {
    Object[] result = new Object[size()];
    int i = 0;
    for (Object o : this)
    {
      result[i++] = o;
    }

    return result;
  }

  @SuppressWarnings("unchecked")
  public <T> T[] toArray(T[] a)
  {
    int i = 0;
    for (Object o : this)
    {
      a[i++] = (T)o;
    }

    return a;
  }

  private static final class CDOHibernateIterator implements Iterator<Object>
  {
    private final Iterator<?> delegate;

    public CDOHibernateIterator(Iterator<?> delegate)
    {
      this.delegate = delegate;
    }

    public boolean hasNext()
    {
      return delegate.hasNext();
    }

    public Object next()
    {
      Object value = delegate.next();
      return convertToCDO(value);
    }

    public void remove()
    {
      delegate.remove();
    }
  }

  private static final class CDOHibernateListIterator implements ListIterator<Object>
  {
    private final ListIterator<Object> delegate;

    private final WrappedHibernateList owner;

    public CDOHibernateListIterator(WrappedHibernateList owner, ListIterator<Object> delegate)
    {
      this.delegate = delegate;
      this.owner = owner;
    }

    public void add(Object o)
    {
      owner.checkFrozen();

      delegate.add(HibernateUtil.getInstance().getCDOID(o));
    }

    public boolean hasNext()
    {
      return delegate.hasNext();
    }

    public boolean hasPrevious()
    {
      return delegate.hasPrevious();
    }

    public Object next()
    {
      Object value = delegate.next();
      return convertToCDO(value);
    }

    public int nextIndex()
    {
      return delegate.nextIndex();
    }

    public Object previous()
    {
      Object value = delegate.previous();
      return convertToCDO(value);
    }

    public int previousIndex()
    {
      return delegate.previousIndex();
    }

    public void remove()
    {
      owner.checkFrozen();
      delegate.remove();
    }

    public void set(Object o)
    {
      owner.checkFrozen();
      delegate.set(HibernateUtil.getInstance().getCDOID(o));
    }
  }

  public void freeze()
  {
    frozen = true;
  }

  private void checkFrozen()
  {
    // a frozen check always implies a modification
    cachedSize = -1;
    if (frozen)
    {
      throw new IllegalStateException("Cannot modify a frozen list");
    }
  }

  public void setWithoutFrozenCheck(int i, Object value)
  {
    getDelegate().set(i, getHibernateValue(value));
  }

  CDORevision getOwner()
  {
    return owner;
  }

  // tagging interface
  private class UninitializedCollection<E> extends ArrayList<E>
  {
    private static final long serialVersionUID = 1L;
  }
}
