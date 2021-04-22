/*
 * Copyright (c) 2013, 2017 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import java.io.IOException;

/**
 * @author Caspar De Groot
 */
public class LoadPermissionsIndication extends CDOServerReadIndication
{
  private CDOID[] ids;

  private CDOPermission[] oldPermissions;

  private int referenceChunk;

  public LoadPermissionsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_PERMISSIONS);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int length = in.readXInt();
    ids = new CDOID[length];
    oldPermissions = new CDOPermission[length];

    for (int i = 0; i < length; i++)
    {
      ids[i] = in.readCDOID();
      oldPermissions[i] = CDOPermission.get(in.readByte());
    }

    referenceChunk = in.readXInt();
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    InternalSession session = getSession();
    InternalRepository repository = getRepository();
    InternalCDORevisionManager revisionManager = repository.getRevisionManager();
    IPermissionManager permissionManager = repository.getSessionManager().getPermissionManager();
    CDOBranchPoint head = repository.getBranchManager().getMainBranch().getHead();

    int length = ids.length;
    for (int i = 0; i < length; i++)
    {
      CDOID id = ids[i];
      CDOPermission oldPermission = oldPermissions[i];

      InternalCDORevision revision = revisionManager.getRevision(id, head, 0, CDORevision.DEPTH_NONE, true);
      if (revision == null)
      {
        out.writeByte(CDOProtocolConstants.REVISION_DOES_NOT_EXIST);
        continue;
      }

      CDOPermission newPermission = permissionManager.getPermission(revision, head, session);
      out.writeByte(newPermission.getBits());

      if (oldPermission == CDOPermission.NONE && newPermission != CDOPermission.NONE)
      {
        revision.writeValues(out, referenceChunk);
      }
    }
  }
}
