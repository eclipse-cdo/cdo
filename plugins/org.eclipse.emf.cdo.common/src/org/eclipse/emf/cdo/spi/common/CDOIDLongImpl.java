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
package org.eclipse.emf.cdo.spi.common;

import org.eclipse.emf.cdo.common.id.CDOIDObject;
import org.eclipse.emf.cdo.common.model.CDOClassRef;

/**
 * @author Eike Stepper
 */
public class CDOIDLongImpl extends AbstractCDOIDLong implements CDOIDObject
{
  private static final long serialVersionUID = 1L;

  public CDOIDLongImpl()
  {
  }

  public CDOIDLongImpl(long value)
  {
    super(value);
  }

  public Type getType()
  {
    return Type.OBJECT;
  }

  public CDOClassRef getClassRef()
  {
    return null;
  }

  public Legacy asLegacy(CDOClassRef classRef)
  {
    return new Legacy(getLongValue(), classRef);
  }

  @Override
  public String toString()
  {
    return "OID" + getLongValue();
  }

  /**
   * @author Eike Stepper
   */
  public static final class Legacy extends CDOIDLongImpl
  {
    private static final long serialVersionUID = 1L;

    private CDOClassRef classRef;

    public Legacy()
    {
    }

    public Legacy(long value, CDOClassRef classRef)
    {
      super(value);
      if (classRef == null)
      {
        throw new IllegalArgumentException("classRef == null");
      }

      this.classRef = classRef;
    }

    @Override
    public Type getType()
    {
      return Type.LEGACY_OBJECT;
    }

    @Override
    public CDOClassRef getClassRef()
    {
      return classRef;
    }

    public void setClassRef(CDOClassRef classRef)
    {
      this.classRef = classRef;
    }

    @Override
    public Legacy asLegacy(CDOClassRef classRef)
    {
      return this;
    }

    @Override
    public String toString()
    {
      return super.toString() + "(" + classRef + ")";
    }
  }
}
