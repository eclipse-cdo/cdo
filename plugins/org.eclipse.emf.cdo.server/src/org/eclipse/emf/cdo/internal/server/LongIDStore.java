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
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDLibraryDescriptor;
import org.eclipse.emf.cdo.common.id.CDOIDLibraryProvider;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.spi.common.id.CDOIDLongFactoryImpl;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class LongIDStore extends Store
{
  protected static final long CRASHED = -1L;

  private static final CDOIDLongFactoryImpl CDOID_OBJECT_FACTORY = new CDOIDLongFactoryImpl();

  @ExcludeFromDump
  private transient long lastObjectID;

  /**
   * @since 2.0
   */
  public LongIDStore(String type, Set<ChangeFormat> supportedChangeFormats,
      Set<RevisionTemporality> supportedRevisionTemporalities, Set<RevisionParallelism> supportedRevisionParallelisms)
  {
    super(type, supportedChangeFormats, supportedRevisionTemporalities, supportedRevisionParallelisms);
  }

  public CDOIDObjectFactory getCDOIDObjectFactory()
  {
    return CDOID_OBJECT_FACTORY;
  }

  public CDOIDLibraryDescriptor getCDOIDLibraryDescriptor()
  {
    return CDOID_OBJECT_FACTORY.getLibraryHandler();
  }

  public CDOIDLibraryProvider getCDOIDLibraryProvider()
  {
    return CDOID_OBJECT_FACTORY.getLibraryHandler();
  }

  public long getLastObjectID()
  {
    return lastObjectID;
  }

  public void setLastObjectID(long lastObjectID)
  {
    this.lastObjectID = lastObjectID;
  }

  public synchronized CDOID getNextCDOID()
  {
    return CDOIDUtil.createLong(++lastObjectID);
  }

  public boolean wasCrashed()
  {
    return lastObjectID == CRASHED;
  }
}
