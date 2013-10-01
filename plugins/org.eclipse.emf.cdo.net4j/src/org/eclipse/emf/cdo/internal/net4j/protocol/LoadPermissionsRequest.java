/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class LoadPermissionsRequest extends CDOClientRequest<Map<InternalCDORevision, CDOPermission>>
{
  private InternalCDORevision[] revisions;

  public LoadPermissionsRequest(CDOClientProtocol protocol, InternalCDORevision[] revisions)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_PERMISSIONS);
    this.revisions = revisions;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    int length = revisions.length;
    out.writeInt(length);

    for (int i = 0; i < length; i++)
    {
      InternalCDORevision revision = revisions[i];
      CDOID id = revision.getID();
      out.writeCDOID(id);

      byte bits = revision.getPermission().getBits();
      out.writeByte(bits);
    }

    int referenceChunk = getSession().options().getCollectionLoadingPolicy().getInitialChunkSize();
    out.writeInt(referenceChunk);
  }

  @Override
  protected Map<InternalCDORevision, CDOPermission> confirming(CDODataInput in) throws IOException
  {
    Map<InternalCDORevision, CDOPermission> oldPermissions = new HashMap<InternalCDORevision, CDOPermission>();

    int length = revisions.length;
    for (int i = 0; i < length; i++)
    {
      InternalCDORevision revision = revisions[i];
      CDOPermission oldPermission = revision.getPermission();

      byte bits = in.readByte();
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
          else if (newPermission == CDOPermission.NONE)
          {
            revision.clearValues();
          }
        }
        finally
        {
          revision.bypassPermissionChecks(bypassPermissionChecks);
        }

        revision.setPermission(newPermission);
        oldPermissions.put(revision, oldPermission);
      }
    }

    return oldPermissions;
  }
}
