/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.common.id;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOIDString;
import org.eclipse.emf.cdo.spi.common.id.InternalCDOIDObject;

/**
 * @author Martin Taal
 * @since 3.0
 */
public class CDOIDObjectStringImpl extends AbstractCDOIDString implements InternalCDOIDObject
{
  private static final long serialVersionUID = 1L;

  public CDOIDObjectStringImpl()
  {
  }

  public CDOIDObjectStringImpl(String value)
  {
    super(value);
  }

  public Type getType()
  {
    return Type.OBJECT;
  }

  public SubType getSubType()
  {
    return SubType.STRING;
  }

  @Override
  public String toString()
  {
    return "OID" + getStringValue(); //$NON-NLS-1$
  }

  @Override
  protected int doCompareTo(CDOID o) throws ClassCastException
  {
    return getStringValue().compareTo(((CDOIDObjectStringImpl)o).getStringValue());
  }
}
