/*
 * Copyright (c) 2011-2013, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Simon McDuff
 */
public class CDOListWithElementProxiesImpl extends CDOPCLListImpl
{
  public static final CDOListFactory FACTORY = new CDOListFactory()
  {
    @Override
    public CDOList createList(int initialCapacity, int size, int initialChunk)
    {
      return new CDOListWithElementProxiesImpl(initialCapacity, size, initialChunk);
    }

    @Override
    public CDOList createList(EStructuralFeature feature, int initialCapacity, int size, int initialChunk)
    {
      return feature instanceof EAttribute //
          ? new CDOListWithElementProxiesWithEqualsImpl(initialCapacity, size, initialChunk) //
          : new CDOListWithElementProxiesImpl(initialCapacity, size, initialChunk);
    }
  };

  private static final long serialVersionUID = 1L;

  public CDOListWithElementProxiesImpl(int initialCapacity, int size, int initialChunk)
  {
    super(initialCapacity, size, initialChunk);
  }

  @Override
  public Object get(int index, boolean resolve)
  {
    if (resolve == true)
    {
      return get(index);
    }

    Object element = super.get(index);
    return element instanceof CDOElementProxy ? CDORevisionUtil.UNLOADED : element;
  }

  @Override
  protected void handleAdjustReference(int index, Object element)
  {
    if (element instanceof CDOElementProxy)
    {
      ((CDOElementProxyImpl)element).setIndex(index);
    }
  }

  @Override
  public InternalCDOList clone(EClassifier classifier)
  {
    CDOType type = CDOModelUtil.getType(classifier);
    int size = size();

    CDOListWithElementProxiesImpl list = useEquals() //
        ? new CDOListWithElementProxiesWithEqualsImpl(size, 0, 0) //
        : new CDOListWithElementProxiesImpl(size, 0, 0);

    for (int j = 0; j < size; j++)
    {
      Object value = this.get(j);

      if (value instanceof CDOElementProxy)
      {
        list.add(j, new CDOElementProxyImpl(((CDOElementProxy)value).getIndex()));
      }
      else
      {
        list.add(j, type.copyValue(value));
      }
    }

    return list;
  }
}
