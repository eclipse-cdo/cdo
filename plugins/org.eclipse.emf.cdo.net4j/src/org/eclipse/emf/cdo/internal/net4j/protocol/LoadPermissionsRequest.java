/*
 * Copyright (c) 2013, 2017, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class LoadPermissionsRequest extends CDOClientRequest<Map<CDORevision, CDOPermission>>
{
  private Map<CDOBranchPoint, Set<InternalCDORevision>> revisionsBySecurityContext;

  private List<CDOBranchPoint> securityContexts;

  public LoadPermissionsRequest(CDOClientProtocol protocol, Map<CDOBranchPoint, Set<InternalCDORevision>> revisionsBySecurityContext)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_PERMISSIONS);
    this.revisionsBySecurityContext = revisionsBySecurityContext;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    int mapSize = revisionsBySecurityContext.size();
    out.writeXInt(mapSize);
    securityContexts = new ArrayList<>(mapSize);

    for (Map.Entry<CDOBranchPoint, Set<InternalCDORevision>> entry : revisionsBySecurityContext.entrySet())
    {
      CDOBranchPoint securityContext = entry.getKey();
      out.writeCDOBranchPoint(securityContext);
      securityContexts.add(securityContext);

      Set<InternalCDORevision> revisions = entry.getValue();
      out.writeXInt(revisions.size());

      for (InternalCDORevision revision : revisions)
      {
        CDOID id = revision.getID();
        out.writeCDOID(id);

        byte bits = revision.getPermission().getBits();
        out.writeByte(bits);
      }
    }

    int referenceChunk = getSession().options().getCollectionLoadingPolicy().getInitialChunkSize();
    out.writeXInt(referenceChunk);
  }

  @Override
  protected Map<CDORevision, CDOPermission> confirming(CDODataInput in) throws IOException
  {
    Map<CDORevision, CDOPermission> oldPermissions = new HashMap<>();
    InternalCDOSession session = getSession();

    try
    {
      session.syncExec(() -> {
        for (CDOBranchPoint securityContext : securityContexts)
        {
          Set<InternalCDORevision> set = revisionsBySecurityContext.get(securityContext);

          for (InternalCDORevision revision : set)
          {
            byte bits = in.readByte();
            if (bits == CDOProtocolConstants.REVISION_DOES_NOT_EXIST)
            {
              continue;
            }

            CDOPermission oldPermission = revision.getPermission();
            CDOPermission newPermission = CDOPermission.get(bits);

            if (oldPermission != newPermission)
            {
              boolean bypassPermissionChecks = revision.bypassPermissionChecks(true);

              try
              {
                if (oldPermission == CDOPermission.NONE)
                {
                  revision.readValues(in);
                }

                revision.setPermission(newPermission);
              }
              finally
              {
                revision.bypassPermissionChecks(bypassPermissionChecks);
              }

              oldPermissions.put(revision, oldPermission);
            }
          }
        }
      });
    }
    catch (IOException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new IOException(ex);
    }

    return oldPermissions;
  }
}
