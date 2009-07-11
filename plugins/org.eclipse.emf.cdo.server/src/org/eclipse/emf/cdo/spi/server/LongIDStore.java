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
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;

import java.util.Set;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class LongIDStore extends Store
{
  @ExcludeFromDump
  private transient long lastObjectID;

  @ExcludeFromDump
  private transient Object lastObjectIDLock = new Object();

  public LongIDStore(String type, Set<ChangeFormat> supportedChangeFormats,
      Set<RevisionTemporality> supportedRevisionTemporalities, Set<RevisionParallelism> supportedRevisionParallelisms)
  {
    super(type, supportedChangeFormats, supportedRevisionTemporalities, supportedRevisionParallelisms);
  }

  public long getLastObjectID()
  {
    synchronized (lastObjectIDLock)
    {
      return lastObjectID;
    }
  }

  public void setLastObjectID(long lastObjectID)
  {
    synchronized (lastObjectIDLock)
    {
      this.lastObjectID = lastObjectID;
    }
  }

  public CDOID getNextCDOID()
  {
    synchronized (lastObjectIDLock)
    {
      return CDOIDUtil.createLong(++lastObjectID);
    }
  }
}
