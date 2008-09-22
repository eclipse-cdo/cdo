/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.revision;

import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.internal.common.revision.CDOListImpl;
import org.eclipse.emf.cdo.spi.common.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

/**
 * @author Simon McDuff
 */
public class CDOListReferenceProxyImpl extends CDOListImpl
{

  public static final CDOListFactory FACTORY = new CDOListFactory()
  {
    public CDOList createList(int initialCapacity, int size, int initialChunk)
    {
      return new CDOListReferenceProxyImpl(initialCapacity, size, initialChunk);
    }
  };

  private static final long serialVersionUID = 1L;

  public CDOListReferenceProxyImpl(int initialCapacity, int size, int initialChunk)
  {
    super(initialCapacity, initialChunk);
    for (int j = initialChunk; j < size; j++)
    {
      this.add(new CDOReferenceProxyImpl(j));
    }
  }

  @Override
  public Object get(int index, boolean resolve)
  {
    if (resolve == true)
    {
      return get(index);
    }

    Object element = super.get(index);

    return element instanceof CDOReferenceProxy ? InternalCDORevision.UNINITIALIZED : element;
  }

  @Override
  protected void handleAdjustReference(int index, Object element)
  {
    if (element instanceof CDOReferenceProxy)
    {
      ((CDOReferenceProxyImpl)element).setIndex(index);
    }
  }

  @Override
  public InternalCDOList clone(CDOType type)
  {
    int size = size();
    InternalCDOList list = new CDOListReferenceProxyImpl(size, 0, 0);
    for (int j = 0; j < size; j++)
    {
      Object value = this.get(j);

      if (value instanceof CDOReferenceProxy)
      {
        list.add(j, new CDOReferenceProxyImpl(((CDOReferenceProxy)value).getIndex()));
      }
      else
      {
        list.add(j, type.copyValue(value));
      }
    }

    return list;
  }
}
