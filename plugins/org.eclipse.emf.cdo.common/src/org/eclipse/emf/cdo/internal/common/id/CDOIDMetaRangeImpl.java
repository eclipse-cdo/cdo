/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.id;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOID.Type;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public final class CDOIDMetaRangeImpl implements CDOIDMetaRange
{
  private static final long serialVersionUID = 1L;

  private CDOID lowerBound;

  private int size;

  public CDOIDMetaRangeImpl(CDOID lowerBound, int size)
  {
    if (size < 0)
    {
      throw new IllegalArgumentException("size < 0");
    }

    this.lowerBound = lowerBound;
    this.size = size;
  }

  public CDOID getLowerBound()
  {
    return lowerBound;
  }

  public CDOID getUpperBound()
  {
    return size > 0 ? get(size - 1) : null;
  }

  public CDOID get(int index)
  {
    if (index < 0 || index >= size)
    {
      throw new IllegalArgumentException("ids < 0 || ids >= size");
    }

    if (isTemporary())
    {
      return new CDOIDTempMetaImpl(((CDOIDTempMetaImpl)lowerBound).getIntValue() + index);
    }

    return new CDOIDMetaImpl(((CDOIDMetaImpl)lowerBound).getLongValue() + index);
  }

  public int size()
  {
    return size;
  }

  public boolean isEmpty()
  {
    return size == 0;
  }

  public boolean contains(CDOID id)
  {
    if (isTemporary())
    {
      if (id.getType() != Type.TEMP_META)
      {
        throw new IllegalArgumentException("id.getType() != Type.TEMP_META");
      }

      int index = ((CDOIDTempMetaImpl)id).getIntValue() - ((CDOIDTempMetaImpl)lowerBound).getIntValue();
      return 0 <= index && index < size;
    }

    if (id.getType() != Type.META)
    {
      throw new IllegalArgumentException("id.getType() != Type.META");
    }

    long index = ((CDOIDMetaImpl)id).getLongValue() - ((CDOIDMetaImpl)lowerBound).getLongValue();
    return 0L <= index && index < size;
  }

  public CDOIDMetaRange increase()
  {
    return new CDOIDMetaRangeImpl(lowerBound, size + 1);
  }

  public Type getType()
  {
    return lowerBound.getType();
  }

  public boolean isTemporary()
  {
    return lowerBound.isTemporary();
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("[{0}:{1}]", lowerBound, getUpperBound());
  }
}
