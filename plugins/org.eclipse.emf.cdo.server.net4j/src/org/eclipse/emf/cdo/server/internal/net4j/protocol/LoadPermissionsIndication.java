/*
 * Copyright (c) 2013, 2017, 2021 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.collection.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Caspar De Groot
 */
public class LoadPermissionsIndication extends CDOServerReadIndication
{
  private Map<CDOBranchPoint, Pair<CDOID[], CDOPermission[]>> permissionsBySecurityContext;

  private int referenceChunk;

  public LoadPermissionsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_PERMISSIONS);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int mapSize = in.readXInt();
    permissionsBySecurityContext = new HashMap<>(mapSize);

    for (int m = 0; m < mapSize; m++)
    {
      CDOBranchPoint securityContext = in.readCDOBranchPoint();
      int listSize = in.readXInt();

      CDOID[] ids = new CDOID[listSize];
      CDOPermission[] oldPermissions = new CDOPermission[listSize];

      permissionsBySecurityContext.put(securityContext, Pair.create(ids, oldPermissions));

      for (int i = 0; i < listSize; i++)
      {
        ids[i] = in.readCDOID();
        oldPermissions[i] = CDOPermission.get(in.readByte());
      }
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

    for (Map.Entry<CDOBranchPoint, Pair<CDOID[], CDOPermission[]>> entry : permissionsBySecurityContext.entrySet())
    {
      CDOBranchPoint securityContext = entry.getKey();

      Pair<CDOID[], CDOPermission[]> value = entry.getValue();
      CDOID[] ids = value.getElement1();
      CDOPermission[] oldPermissions = value.getElement2();

      int listSize = ids.length;
      for (int i = 0; i < listSize; i++)
      {
        CDOID id = ids[i];
        CDOPermission oldPermission = oldPermissions[i];

        InternalCDORevision revision = revisionManager.getRevision(id, securityContext, 0, CDORevision.DEPTH_NONE, true);
        if (revision == null)
        {
          out.writeByte(CDOProtocolConstants.REVISION_DOES_NOT_EXIST);
          continue;
        }

        CDOPermission newPermission = permissionManager.getPermission(revision, securityContext, session);
        out.writeByte(newPermission.getBits());

        if (oldPermission == CDOPermission.NONE && newPermission != CDOPermission.NONE)
        {
          revision.writeValues(out, referenceChunk);
        }
      }
    }
  }
}
