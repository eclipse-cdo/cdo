/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/213402
 */
package org.eclipse.emf.cdo.internal.common.id;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTempMeta;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOIDInteger;

/**
 * @author Eike Stepper
 */
public class CDOIDTempMetaImpl extends AbstractCDOIDInteger implements CDOIDTempMeta
{
  private static final long serialVersionUID = 1L;

  public CDOIDTempMetaImpl(int value)
  {
    super(value);
  }

  public Type getType()
  {
    return Type.TEMP_META;
  }

  @Override
  public String toString()
  {
    return "mid" + getIntValue();
  }

  @Override
  protected int doCompareTo(CDOID o) throws ClassCastException
  {
    return new Integer(getIntValue()).compareTo(((CDOIDTempMetaImpl)o).getIntValue());
  }
}
