package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;

import org.eclipse.emf.ecore.EClassifier;

/**
 * The built-in identity-equality list that tracks partially loaded values.
 *
 *  @author Eike Stepper
 */
public class CDOPCLListImpl extends CDOListImpl
{
  private static final long serialVersionUID = 1L;

  public static final CDOListFactory FACTORY = new CDOListFactory()
  {
    @Override
    public CDOList createList(int initialCapacity, int size, int initialChunk)
    {
      return new CDOPCLListImpl(initialCapacity, size, initialChunk);
    }
  };

  private int unloadedCount;

  public CDOPCLListImpl(int initialCapacity, int size, int initialChunk)
  {
    super(initialCapacity, 0);

    for (int i = 0; i < initialChunk; i++)
    {
      add(CDORevisionUtil.UNLOADED);
    }

    for (int i = initialChunk; i < size; i++)
    {
      add(new CDOElementProxyImpl(i));
    }
  }

  public CDOPCLListImpl(int initialCapacity, int size)
  {
    super(initialCapacity, size);
  }

  @Override
  protected void didSet(int index, Object newValue, Object oldValue)
  {
    update(isUnloaded(oldValue), isUnloaded(newValue));
  }

  @Override
  protected void didAdd(int index, Object value)
  {
    update(false, isUnloaded(value));
  }

  @Override
  protected void didRemove(int index, Object value)
  {
    update(isUnloaded(value), false);
  }

  @Override
  protected void didClear(int size, Object[] oldData)
  {
    int count = 0;

    for (int i = 0; i < size; i++)
    {
      if (isUnloaded(oldData[i]))
      {
        count++;
      }
    }

    update(count, 0);
  }

  private void update(boolean oldUnloaded, boolean newUnloaded)
  {
    update(oldUnloaded ? 1 : 0, newUnloaded ? 1 : 0);
  }

  private void update(int oldCount, int newCount)
  {
    int delta = newCount - oldCount;
    unloadedCount += delta;

    if (delta != 0 && getOwner() != null)
    {
      getOwner().unloadedCountChanged(delta);
    }
  }

  public int getUnloadedCount()
  {
    return unloadedCount;
  }

  @Override
  public boolean isFullyLoaded()
  {
    return unloadedCount == 0;
  }

  @Override
  public int getServerIndexAt(int accessIndex)
  {
    Object value = get(accessIndex);
    return value instanceof CDOElementProxy ? ((CDOElementProxy)value).getIndex() : accessIndex;
  }

  @Override
  protected boolean acceptsPCL()
  {
    return true;
  }

  @Override
  public void setData(int size, Object[] data)
  {
    int oldCount = unloadedCount;
    int newCount = 0;

    for (int i = 0; i < size; i++)
    {
      if (isUnloaded(data[i]))
      {
        newCount++;
      }
    }

    super.setData(size, data);
    update(oldCount, newCount);
  }

  @Override
  public void setOwner(InternalCDOList.Owner owner)
  {
    InternalCDOList.Owner oldOwner = getOwner();
    if (oldOwner == owner)
    {
      return;
    }

    if (owner != null && oldOwner != null)
    {
      throw new IllegalStateException("A CDO list can only have one owner");
    }

    super.setOwner(owner);

    if (oldOwner != null && unloadedCount != 0)
    {
      oldOwner.unloadedCountChanged(-unloadedCount);
    }
    else if (owner != null && unloadedCount != 0)
    {
      owner.unloadedCountChanged(unloadedCount);
    }
  }

  @Override
  public InternalCDOList clone(EClassifier classifier)
  {
    int size = size();

    CDOType type = CDOModelUtil.getType(classifier);
    CDOPCLListImpl result = useEquals() //
        ? new CDOPCLListWithEqualsImpl(size, 0, 0) //
        : new CDOPCLListImpl(size, 0, 0);

    for (int i = 0; i < size; i++)
    {
      Object value = get(i);

      result.add(value instanceof CDOElementProxy //
          ? new CDOElementProxyImpl(((CDOElementProxy)value).getIndex()) //
          : type.copyValue(value));
    }

    return result;
  }
}
