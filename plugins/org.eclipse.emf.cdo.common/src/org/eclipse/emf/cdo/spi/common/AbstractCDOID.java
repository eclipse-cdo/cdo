/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/213402
 **************************************************************************/
package org.eclipse.emf.cdo.spi.common;

import org.eclipse.emf.cdo.common.id.CDOID;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class AbstractCDOID implements CDOID
{
  private static final long serialVersionUID = 1L;

  public AbstractCDOID()
  {
  }

  public boolean isNull()
  {
    switch (getType())
    {
    case NULL:
      return true;

    default:
      return false;
    }
  }

  public boolean isObject()
  {
    switch (getType())
    {
    case OBJECT:
    case TEMP_OBJECT:
      return true;

    default:
      return false;
    }
  }

  public boolean isMeta()
  {
    switch (getType())
    {
    case META:
    case TEMP_META:
      return true;

    default:
      return false;
    }
  }

  public boolean isTemporary()
  {
    switch (getType())
    {
    case TEMP_OBJECT:
    case TEMP_META:
    case EXTERNAL_TEMP_OBJECT:
      return true;

    default:
      return false;
    }
  }

  /**
   * @since 2.0
   */
  public boolean isExternal()
  {
    switch (getType())
    {
    case EXTERNAL_TEMP_OBJECT:
    case EXTERNAL_OBJECT:
      return true;

    default:
      return false;
    }
  }

  /**
   * <b>Note:</b> {@link CDOID#toURIFragment()} and {@link AbstractCDOID#read(String)} need to match.
   * 
   * @since 2.0
   */
  public abstract void read(String fragmentPart);

  public abstract void read(ExtendedDataInput in) throws IOException;

  public abstract void write(ExtendedDataOutput out) throws IOException;
}
