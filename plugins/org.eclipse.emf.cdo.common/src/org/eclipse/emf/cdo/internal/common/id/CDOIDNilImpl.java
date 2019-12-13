/*
 * Copyright (c) 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.id;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDObject;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOID;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * @author Eike Stepper
 */
public final class CDOIDNilImpl extends AbstractCDOID implements CDOIDTemp, CDOIDObject
{
  private static final long serialVersionUID = 1L;

  public static final CDOIDNilImpl INSTANCE = new CDOIDNilImpl();

  private CDOIDNilImpl()
  {
  }

  public int getIntValue()
  {
    return 0;
  }

  public long getLongValue()
  {
    return 0L;
  }

  @Override
  public void write(CDODataOutput out) throws IOException
  {
    // Do nothing
  }

  @Override
  public String toURIFragment()
  {
    return "NIL"; //$NON-NLS-1$
  }

  @Override
  public Type getType()
  {
    return Type.NIL;
  }

  @Override
  public boolean isExternal()
  {
    return false;
  }

  @Override
  public boolean isNull()
  {
    return false;
  }

  @Override
  public boolean isNil()
  {
    return true;
  }

  @Override
  public boolean isObject()
  {
    return false;
  }

  @Override
  public boolean isTemporary()
  {
    return false;
  }

  @Override
  public int hashCode()
  {
    return 0;
  }

  @Override
  public String toString()
  {
    return "NULL"; //$NON-NLS-1$
  }

  @Override
  protected int doCompareTo(CDOID o) throws ClassCastException
  {
    return 0; // NULL == NULL
  }

  private Object readResolve() throws ObjectStreamException
  {
    return INSTANCE;
  }
}
