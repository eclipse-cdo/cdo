/*
 * Copyright (c) 2008-2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0.
 */
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Collection;

/**
 * The built-in identity-equality CDO list, which rejects PCL values.
 *
 * @author Eike Stepper
 */
public class CDOListImpl extends BasicEList<Object> implements InternalCDOList
{
  private static final long serialVersionUID = 1L;

  private static final Object CONSTRUCTION = new Object();

  public static final CDOListFactory FACTORY = new CDOListFactory()
  {
    @Override
    public CDOList createList(int initialCapacity, int size, int initialChunk)
    {
      return new CDOListImpl(initialCapacity, size);
    }

    @Override
    public CDOList createList(EStructuralFeature feature, int initialCapacity, int size, int initialChunk)
    {
      boolean partial = initialChunk != CDORevision.UNCHUNKED && initialChunk < size;
      if (partial)
      {
        return feature != null && feature instanceof EAttribute //
            ? new CDOPCLListWithEqualsImpl(initialCapacity, size) //
            : new CDOPCLListImpl(initialCapacity, size);
      }

      return feature != null && feature instanceof EAttribute //
          ? new CDOListWithEqualsImpl(initialCapacity, size) //
          : new CDOListImpl(initialCapacity, size);
    }
  };

  private transient InternalCDOList.Owner owner;

  private boolean constructing = true;

  public CDOListImpl(int initialCapacity, int size)
  {
    super(initialCapacity);

    for (int j = 0; j < size; j++)
    {
      add(CONSTRUCTION);
    }

    constructing = false;
  }

  @Override
  protected boolean useEquals()
  {
    return false;
  }

  @Override
  public Object get(int index, boolean resolve)
  {
    return get(index);
  }

  @Override
  protected Object validate(int index, Object value)
  {
    if (!constructing && !acceptsPCL() && isUnloaded(value))
    {
      throw new IllegalArgumentException("PCL values are not supported by this list");
    }

    return super.validate(index, value);
  }

  protected boolean acceptsPCL()
  {
    return false;
  }

  protected static boolean isUnloaded(Object value)
  {
    return value == CDORevisionUtil.UNLOADED || value instanceof CDOElementProxy;
  }

  /**
   * Hook for list implementations that maintain element metadata while references are adjusted.
   */
  protected void handleAdjustReference(int index, Object element)
  {
    // Do nothing.
  }

  @Override
  public boolean isLoadedAt(int index)
  {
    return !isUnloaded(get(index));
  }

  @Override
  public boolean isFullyLoaded()
  {
    return true;
  }

  @Override
  public void setOwner(InternalCDOList.Owner owner)
  {
    InternalCDOList.Owner oldOwner = this.owner;
    if (oldOwner == owner)
    {
      return;
    }

    if (owner != null && oldOwner != null)
    {
      throw new IllegalStateException("A CDO list can only have one owner");
    }

    this.owner = owner;
  }

  protected InternalCDOList.Owner getOwner()
  {
    return owner;
  }

  @Override
  public void loadValue(int index, Object value)
  {
    if (isLoadedAt(index) || isUnloaded(value))
    {
      throw new IllegalArgumentException("Invalid list materialization");
    }

    setWithoutFrozenCheck(index, value);
  }

  @Override
  public void finishConstruction(boolean partial)
  {
    for (int i = 0, size = size(); i < size; i++)
    {
      if (get(i) == CONSTRUCTION)
      {
        if (!partial)
        {
          throw new IllegalStateException("A completed CDO list contains a construction slot");
        }

        setWithoutFrozenCheck(i, CDORevisionUtil.UNLOADED);
      }
    }
  }

  @Override
  public void freeze()
  {
  }

  @Override
  public void setWithoutFrozenCheck(int index, Object value)
  {
    super.set(index, value);
  }

  @Override
  public void setData(int size, Object[] data)
  {
    if (!acceptsPCL())
    {
      for (int i = 0; i < size; i++)
      {
        if (isUnloaded(data[i]))
        {
          throw new IllegalArgumentException("PCL values are not supported by this list");
        }
      }
    }

    super.setData(size, data);
  }

  private void checkFrozen()
  {
    if (owner != null && owner.isFrozen())
    {
      throw new IllegalStateException("Cannot modify a frozen list");
    }
  }

  @Override
  public boolean add(Object value)
  {
    checkFrozen();
    return super.add(value);
  }

  @Override
  public void add(int index, Object value)
  {
    checkFrozen();
    super.add(index, value);
  }

  @Override
  public boolean addAll(Collection<? extends Object> c)
  {
    checkFrozen();
    return super.addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends Object> c)
  {
    checkFrozen();
    return super.addAll(index, c);
  }

  @Override
  public Object remove(int index)
  {
    checkFrozen();
    return super.remove(index);
  }

  @Override
  public boolean remove(Object value)
  {
    checkFrozen();
    return super.remove(value);
  }

  @Override
  public boolean removeAll(Collection<?> c)
  {
    checkFrozen();
    return super.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c)
  {
    checkFrozen();
    return super.retainAll(c);
  }

  @Override
  public void clear()
  {
    checkFrozen();
    super.clear();
  }

  @Override
  public Object set(int index, Object value)
  {
    checkFrozen();
    return super.set(index, value);
  }

  @Override
  public boolean adjustReferences(CDOReferenceAdjuster adjuster, EStructuralFeature feature)
  {
    boolean changed = false;
    CDOType type = CDOModelUtil.getType(feature);

    for (int i = 0, size = size(); i < size; i++)
    {
      Object element = super.get(i);
      handleAdjustReference(i, element);

      Object newID = type.adjustReferences(adjuster, element, feature, i);
      if (newID != element)
      {
        setWithoutFrozenCheck(i, newID);
        changed = true;
      }
    }

    return changed;
  }

  @Override
  public InternalCDOList clone(EClassifier classifier)
  {
    int size = size();

    CDOType type = CDOModelUtil.getType(classifier);
    CDOListImpl result = useEquals() ? new CDOListWithEqualsImpl(size, 0) : new CDOListImpl(size, 0);

    for (int i = 0; i < size; i++)
    {
      Object value = type.copyValue(get(i));
      result.add(value);
    }

    return result;
  }

  /**
   * An index exception with a compact message.
   *
   * @author Eike Stepper
   */
  public static class IndexOutOfBoundsException extends BasicIndexOutOfBoundsException
  {
    private static final long serialVersionUID = 1L;

    public IndexOutOfBoundsException(int index, int size)
    {
      super(index, size);
    }
  }
}
