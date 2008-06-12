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
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public final class CDOIDAndVersionImpl implements CDOIDAndVersion
{
  private CDOID id;

  private int version;

  public CDOIDAndVersionImpl(CDOID id, int version)
  {
    this.id = id;
    this.version = version;
  }

  public CDOIDAndVersionImpl(ExtendedDataInput in, CDOIDObjectFactory factory, boolean asLegacy) throws IOException
  {
    id = CDOIDUtil.read(in, factory, asLegacy);
    version = in.readInt();
  }

  public void write(ExtendedDataOutput out, boolean asLegacy) throws IOException
  {
    CDOIDUtil.write(out, id, asLegacy);
    out.writeInt(version);
  }

  public CDOID getID()
  {
    return id;
  }

  public int getVersion()
  {
    return version;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDOIDAndVersion)
    {
      CDOIDAndVersion that = (CDOIDAndVersion)obj;
      return id.equals(that.getID()) && version == that.getVersion();
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return id.hashCode() ^ version;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}v{1}", id, version);
  }
}
