/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.id;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDObject;

/**
 * @author Eike Stepper
 * @since 2.0
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

  @Override
  public String toString()
  {
    return "OID" + getLongValue();
  }

  @Override
  protected int doCompareTo(CDOID o) throws ClassCastException
  {
    return new Long(getLongValue()).compareTo(((CDOIDLongImpl)o).getLongValue());
  }
}
