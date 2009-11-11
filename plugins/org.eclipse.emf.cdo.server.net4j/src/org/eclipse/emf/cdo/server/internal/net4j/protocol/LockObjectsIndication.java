/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class LockObjectsIndication extends AbstractSyncRevisionsIndication
{
  private LockType lockType;

  private List<CDOID> ids = new ArrayList<CDOID>();

  private List<CDOIDAndVersion> idAndVersions = new ArrayList<CDOIDAndVersion>();

  private IView view;

  public LockObjectsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_OBJECTS);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    super.indicating(in);

    int viewID = in.readInt();
    lockType = in.readCDOLockType();
    long timeout = in.readLong();

    try
    {
      view = getSession().getView(viewID);
      getRepository().getLockManager().lock(lockType, view, ids, timeout);
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    for (CDOIDAndVersion idAndVersion : idAndVersions)
    {
      udpateObjectList(idAndVersion.getID(), idAndVersion.getVersion());
    }

    if (!detachedObjects.isEmpty())
    {
      getRepository().getLockManager().unlock(lockType, view, ids);
      throw new IllegalArgumentException(detachedObjects.size() + " objects are not persistent anymore"); //$NON-NLS-1$
    }

    super.responding(out);
  }

  @Override
  protected void process(CDOID id, int version)
  {
    ids.add(id);
    idAndVersions.add(CDOIDUtil.createIDAndVersion(id, version));
  }
}
