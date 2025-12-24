/*
 * Copyright (c) 2009, 2011-2014, 2016, 2017, 2019, 2024, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.protocol;

import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * The communications protocol associated with a CDO {@link CDOCommonSession session}.
 *
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOProtocol extends CDOProtocolConstants
{
  public CDOCommonSession getSession();

  /**
   * A data transfer object for commit notifications.
   *
   * @author Eike Stepper
   * @since 4.3
   */
  public static final class CommitNotificationInfo
  {
    public static final byte IMPACT_NONE = 0;

    public static final byte IMPACT_PERMISSIONS = 1;

    public static final byte IMPACT_REALM = 2;

    private int senderID;

    private CDOCommonSession sender;

    private CDORevisionProvider revisionProvider;

    private CDOCommitInfo commitInfo;

    private Map<CDOID, CDOPermission> newPermissions;

    private Set<? extends Object> impactedRules;

    private byte securityImpact = IMPACT_NONE;

    private boolean clearResourcePathCache;

    private boolean modifiedByServer;

    private CDOLockChangeInfo lockChangeInfo;

    public CommitNotificationInfo()
    {
    }

    public CommitNotificationInfo(CDODataInput in) throws IOException
    {
      senderID = in.readXInt();
      commitInfo = in.readCDOCommitInfo();
      clearResourcePathCache = in.readBoolean();
      modifiedByServer = in.readBoolean();
      securityImpact = in.readByte();

      int size = in.readXInt();
      if (size != 0)
      {
        newPermissions = CDOIDUtil.createMap();
        for (int i = 0; i < size; i++)
        {
          CDOID id = in.readCDOID();
          byte bits = in.readByte();

          CDOPermission permission = CDOPermission.get(bits);
          newPermissions.put(id, permission);
        }
      }

      if (in.readBoolean())
      {
        lockChangeInfo = in.readCDOLockChangeInfo();
      }
    }

    public void write(CDODataOutput out) throws IOException
    {
      out.writeXInt(senderID);
      out.writeCDOCommitInfo(commitInfo);
      out.writeBoolean(clearResourcePathCache);
      out.writeBoolean(modifiedByServer);
      out.writeByte(securityImpact); // Must come after writeCDOCommitInfo()

      // Must come after writeCDOCommitInfo()
      if (newPermissions == null)
      {
        out.writeXInt(0);
      }
      else
      {
        int size = newPermissions.size();
        out.writeXInt(size);

        for (Map.Entry<CDOID, CDOPermission> entry : newPermissions.entrySet())
        {
          CDOID id = entry.getKey();
          byte bits = entry.getValue().getBits();

          out.writeCDOID(id);
          out.writeByte(bits);
        }
      }

      if (lockChangeInfo != null)
      {
        out.writeBoolean(true);
        out.writeCDOLockChangeInfo(lockChangeInfo);
      }
      else
      {
        out.writeBoolean(false);
      }
    }

    public int getSenderID()
    {
      return senderID;
    }

    public CDOCommonSession getSender()
    {
      return sender;
    }

    public void setSender(CDOCommonSession sender)
    {
      this.sender = sender;
      senderID = sender.getSessionID();
    }

    public CDORevisionProvider getRevisionProvider()
    {
      return revisionProvider;
    }

    public void setRevisionProvider(CDORevisionProvider revisionProvider)
    {
      this.revisionProvider = revisionProvider;
    }

    public CDOCommitInfo getCommitInfo()
    {
      return commitInfo;
    }

    public void setCommitInfo(CDOCommitInfo commitInfo)
    {
      this.commitInfo = commitInfo;
    }

    public Map<CDOID, CDOPermission> getNewPermissions()
    {
      return newPermissions;
    }

    public void setNewPermissions(Map<CDOID, CDOPermission> newPermissions)
    {
      this.newPermissions = newPermissions;
    }

    public Set<? extends Object> getImpactedRules()
    {
      return impactedRules;
    }

    public void setImpactedRules(Set<? extends Object> impactedRules)
    {
      this.impactedRules = impactedRules;
    }

    public byte getSecurityImpact()
    {
      return securityImpact;
    }

    public void setSecurityImpact(byte securityImpact)
    {
      this.securityImpact = securityImpact;
    }

    public boolean isClearResourcePathCache()
    {
      return clearResourcePathCache;
    }

    public void setClearResourcePathCache(boolean clearResourcePathCache)
    {
      this.clearResourcePathCache = clearResourcePathCache;
    }

    /**
     * @since 4.8
     */
    public boolean isModifiedByServer()
    {
      return modifiedByServer;
    }

    /**
     * @since 4.8
     */
    public void setModifiedByServer(boolean modifiedByServer)
    {
      this.modifiedByServer = modifiedByServer;
    }

    /**
     * @since 4.6
     */
    public CDOLockChangeInfo getLockChangeInfo()
    {
      return lockChangeInfo;
    }

    /**
     * @since 4.6
     */
    public void setLockChangeInfo(CDOLockChangeInfo lockChangeInfo)
    {
      this.lockChangeInfo = lockChangeInfo;
    }
  }

  /**
   * A data transfer object for the essential commit data.
   *
   * @author Eike Stepper
   * @since 4.8
   */
  public static final class CommitData
  {
    private final InternalCDORevision[] newObjects;

    private final InternalCDORevisionDelta[] dirtyObjectDeltas;

    private final CDOID[] detachedObjects;

    public CommitData(InternalCDORevision[] newObjects, InternalCDORevisionDelta[] dirtyObjectDeltas, CDOID[] detachedObjects)
    {
      this.newObjects = newObjects;
      this.dirtyObjectDeltas = dirtyObjectDeltas;
      this.detachedObjects = detachedObjects;
    }

    public CommitData(CDODataInput in) throws IOException
    {
      newObjects = readNewObjects(in);
      dirtyObjectDeltas = readDirtyObjectDeltas(in);
      detachedObjects = readDetachedObjects(in);
    }

    private InternalCDORevision[] readNewObjects(CDODataInput in) throws IOException
    {
      InternalCDORevision[] result = null;

      int n = in.readXInt();
      if (n > 0)
      {
        result = new InternalCDORevision[n];
        for (int i = 0; i < n; i++)
        {
          result[i] = (InternalCDORevision)in.readCDORevision();
        }
      }

      return result;
    }

    private InternalCDORevisionDelta[] readDirtyObjectDeltas(CDODataInput in) throws IOException
    {
      InternalCDORevisionDelta[] result = null;

      int n = in.readXInt();
      if (n > 0)
      {
        result = new InternalCDORevisionDelta[n];
        for (int i = 0; i < n; i++)
        {
          result[i] = (InternalCDORevisionDelta)in.readCDORevisionDelta();
        }
      }

      return result;
    }

    private CDOID[] readDetachedObjects(CDODataInput in) throws IOException
    {
      CDOID[] result = null;

      int n = in.readXInt();
      if (n > 0)
      {
        result = new CDOID[n];
        for (int i = 0; i < n; i++)
        {
          result[i] = in.readCDOID();
        }
      }

      return result;
    }

    public void write(CDODataOutput out) throws IOException
    {
      if (newObjects != null)
      {
        out.writeXInt(newObjects.length);
        for (int i = 0; i < newObjects.length; i++)
        {
          out.writeCDORevision(newObjects[i], CDORevision.UNCHUNKED);
        }
      }
      else
      {
        out.writeXInt(0);
      }

      if (dirtyObjectDeltas != null)
      {
        out.writeXInt(dirtyObjectDeltas.length);
        for (int i = 0; i < dirtyObjectDeltas.length; i++)
        {
          out.writeCDORevisionDelta(dirtyObjectDeltas[i]);
        }
      }
      else
      {
        out.writeXInt(0);
      }

      if (detachedObjects != null)
      {
        out.writeXInt(detachedObjects.length);
        for (int i = 0; i < detachedObjects.length; i++)
        {
          out.writeCDOID(detachedObjects[i]);
        }
      }
      else
      {
        out.writeXInt(0);
      }
    }

    /**
     * @since 4.26
     */
    public boolean isNewObject(CDOID id)
    {
      if (newObjects != null)
      {
        for (int i = 0; i < newObjects.length; i++)
        {
          if (newObjects[i].getID() == id)
          {
            return true;
          }
        }
      }

      return false;
    }

    public InternalCDORevision[] getNewObjects()
    {
      return newObjects;
    }

    public InternalCDORevisionDelta[] getDirtyObjectDeltas()
    {
      return dirtyObjectDeltas;
    }

    public CDOID[] getDetachedObjects()
    {
      return detachedObjects;
    }
  }
}
