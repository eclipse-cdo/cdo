/*
 * Copyright (c) 2009-2013, 2015-2019, 2021, 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 213402
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil.ExtResourceSet;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitData;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.ExtendedIOUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CommitTransactionIndication extends CDOServerIndicationWithMonitoring
{
  protected InternalCommitContext commitContext;

  public CommitTransactionIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION);
  }

  protected CommitTransactionIndication(CDOServerProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  @Override
  protected InternalCDOPackageRegistry getPackageRegistry()
  {
    return commitContext.getPackageRegistry();
  }

  protected void initializeCommitContext(CDODataInput in) throws Exception
  {
    int viewID = in.readXInt();
    commitContext = getTransaction(viewID).createCommitContext();
  }

  @Override
  protected void indicating(CDODataInput in, OMMonitor monitor) throws Exception
  {
    try
    {
      monitor.begin(OMMonitor.TEN);
      indicatingRead(in, monitor.fork(OMMonitor.ONE));
      indicatingCommit(in, monitor.fork(OMMonitor.TEN - OMMonitor.ONE));
    }
    catch (IOException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      monitor.done();
    }
  }

  protected void indicatingRead(CDODataInput in, OMMonitor monitor) throws Exception
  {
    // Create commit context
    initializeCommitContext(in);
    commitContext.preWrite();

    long lastUpdateTime = in.readXLong();
    int commitNumber = in.readXInt();
    String commitComment = in.readString();
    Map<String, String> commitProperties = ExtendedIOUtil.readProperties(in);
    CDOBranchPoint commitMergeSource = CDOBranchUtil.readBranchPointOrNull(in);
    long optimisticLockingTimeout = in.readXLong();

    CDOLockState[] locksOnNewObjects = new CDOLockState[in.readXInt()];
    CDOID[] idsToUnlock = new CDOID[in.readXInt()];
    InternalCDOPackageUnit[] newPackageUnits = new InternalCDOPackageUnit[in.readXInt()];
    InternalCDORevision[] newObjects = new InternalCDORevision[in.readXInt()];
    InternalCDORevisionDelta[] dirtyObjectDeltas = new InternalCDORevisionDelta[in.readXInt()];
    CDOID[] detachedObjects = new CDOID[in.readXInt()];
    monitor
        .begin(locksOnNewObjects.length + idsToUnlock.length + newPackageUnits.length + newObjects.length + dirtyObjectDeltas.length + detachedObjects.length);

    try
    {
      // Locks on new objects
      for (int i = 0; i < locksOnNewObjects.length; i++)
      {
        locksOnNewObjects[i] = in.readCDOLockState();
        monitor.worked();
      }

      // Unlocks on changed objects
      for (int i = 0; i < idsToUnlock.length; i++)
      {
        idsToUnlock[i] = in.readCDOID();
        monitor.worked();
      }

      // New package units
      if (newPackageUnits.length != 0)
      {
        InternalCDOPackageRegistry packageRegistry = commitContext.getPackageRegistry();
        ResourceSet resourceSet = createResourceSet(packageRegistry);
        for (int i = 0; i < newPackageUnits.length; i++)
        {
          newPackageUnits[i] = (InternalCDOPackageUnit)in.readCDOPackageUnit(resourceSet);
          packageRegistry.putPackageUnit(newPackageUnits[i]); // Must happen before readCDORevision!!!
          monitor.worked();
        }

        // When all packages are deserialized and registered, resolve them
        // Note: EcoreUtil.resolveAll(resourceSet) does *not* do the trick
        EMFUtil.safeResolveAll(resourceSet);
      }

      // New objects
      boolean usingEcore = false;
      boolean usingEtypes = false;
      for (int i = 0; i < newObjects.length; i++)
      {
        newObjects[i] = (InternalCDORevision)in.readCDORevision();

        EPackage ePackage = newObjects[i].getEClass().getEPackage();
        if (ePackage == EcorePackage.eINSTANCE)
        {
          usingEcore = true;
        }
        else if (ePackage == EtypesPackage.eINSTANCE)
        {
          usingEtypes = true;
        }

        monitor.worked();
      }

      // Make the assignment of permanent IDs predictable
      Arrays.sort(newObjects, new Comparator<InternalCDORevision>()
      {
        @Override
        public int compare(InternalCDORevision r1, InternalCDORevision r2)
        {
          return r1.getID().compareTo(r2.getID());
        }
      });

      // Dirty objects
      for (int i = 0; i < dirtyObjectDeltas.length; i++)
      {
        dirtyObjectDeltas[i] = (InternalCDORevisionDelta)in.readCDORevisionDelta();
        monitor.worked();
      }

      boolean clearResourcePathCache = in.readBoolean();
      boolean auditing = getRepository().isSupportingAudits();
      boolean ensuringReferentialIntegrity = getRepository().isEnsuringReferentialIntegrity();

      Map<CDOID, EClass> detachedObjectTypes = null;
      if (auditing || ensuringReferentialIntegrity)
      {
        detachedObjectTypes = CDOIDUtil.createMap();
      }

      CDOBranchVersion[] detachedObjectVersions = null;
      if (auditing && detachedObjects.length != 0)
      {
        detachedObjectVersions = new CDOBranchVersion[detachedObjects.length];
      }

      CDOBranch transactionBranch = commitContext.getBranchPoint().getBranch();
      for (int i = 0; i < detachedObjects.length; i++)
      {
        CDOID id = in.readCDOID();
        detachedObjects[i] = id;

        if (detachedObjectTypes != null)
        {
          EClass eClass = (EClass)in.readCDOClassifierRefAndResolve();
          detachedObjectTypes.put(id, eClass);
        }

        if (detachedObjectVersions != null)
        {
          CDOBranch branch;
          int version = in.readXInt();
          if (version < 0)
          {
            version = -version;
            branch = in.readCDOBranch();
          }
          else
          {
            branch = transactionBranch;
          }

          detachedObjectVersions[i] = branch.getVersion(version);
        }

        monitor.worked();
      }

      if (detachedObjectTypes != null && detachedObjectTypes.isEmpty())
      {
        detachedObjectTypes = null;
      }

      commitContext.setCommitNumber(commitNumber);
      commitContext.setLastUpdateTime(lastUpdateTime);
      commitContext.setOptimisticLockingTimeout(optimisticLockingTimeout);
      commitContext.setClearResourcePathCache(clearResourcePathCache);
      commitContext.setUsingEcore(usingEcore);
      commitContext.setUsingEtypes(usingEtypes);
      commitContext.setNewPackageUnits(newPackageUnits);
      commitContext.setLocksOnNewObjects(locksOnNewObjects);
      commitContext.setNewObjects(newObjects);
      commitContext.setDirtyObjectDeltas(dirtyObjectDeltas);
      commitContext.setDetachedObjects(detachedObjects);
      commitContext.setDetachedObjectTypes(detachedObjectTypes);
      commitContext.setDetachedObjectVersions(detachedObjectVersions);
      commitContext.setCommitComment(commitComment);
      commitContext.setCommitProperties(commitProperties);
      commitContext.setCommitMergeSource(commitMergeSource);
      commitContext.setLobs(getIndicationStream());
      commitContext.setLocksOnNewObjects(locksOnNewObjects);
      commitContext.setIDsToUnlock(idsToUnlock);
    }
    finally
    {
      monitor.done();
    }
  }

  protected void indicatingCommit(CDODataInput in, OMMonitor monitor)
  {
    getRepository().commit(commitContext, monitor);
  }

  @Override
  protected void indicatingFailed()
  {
    if (commitContext != null)
    {
      commitContext.postCommit(false);
      commitContext = null;
    }
  }

  @Override
  protected void responding(CDODataOutput out, OMMonitor monitor) throws Exception
  {
    boolean success = false;

    try
    {
      byte rollbackReason = commitContext.getRollbackReason();
      String rollbackMessage = commitContext.getRollbackMessage();
      List<CDOIDReference> xRefs = commitContext.getXRefs();

      success = respondingException(out, rollbackReason, rollbackMessage, xRefs);
      if (success)
      {
        respondingResult(out);
        respondingMappingNewObjects(out);
        respondingLocks(out);
        respondingNewPermissions(out);
        respondingNewCommitData(out);
      }
    }
    finally
    {
      commitContext.postCommit(success);
    }
  }

  protected boolean respondingException(CDODataOutput out, byte rollbackReason, String rollbackMessage, List<CDOIDReference> xRefs) throws Exception
  {
    boolean success = rollbackMessage == null;
    out.writeBoolean(success);
    if (!success)
    {
      out.writeByte(rollbackReason);
      out.writeString(rollbackMessage);
      out.writeCDOBranchPoint(commitContext.getBranchPoint());
      out.writeXLong(commitContext.getPreviousTimeStamp());

      if (xRefs != null)
      {
        out.writeXInt(xRefs.size());
        for (CDOIDReference xRef : xRefs)
        {
          out.writeCDOIDReference(xRef);
        }
      }
      else
      {
        out.writeXInt(0);
      }
    }

    return success;
  }

  protected void respondingResult(CDODataOutput out) throws Exception
  {
    out.writeCDOBranchPoint(commitContext.getBranchPoint());
    out.writeXLong(commitContext.getPreviousTimeStamp());
    out.writeByte(commitContext.getSecurityImpact());
  }

  protected void respondingMappingNewObjects(CDODataOutput out) throws Exception
  {
    Map<CDOID, CDOID> idMappings = commitContext.getIDMappings();
    for (Map.Entry<CDOID, CDOID> entry : idMappings.entrySet())
    {
      CDOID oldID = entry.getKey();
      CDOID newID = entry.getValue();
      out.writeCDOID(oldID);
      out.writeCDOID(newID);
    }

    out.writeCDOID(CDOID.NULL);
  }

  protected void respondingLocks(CDODataOutput out) throws Exception
  {
    List<CDOLockDelta> lockDeltas = commitContext.getLockDeltas();
    out.writeCDOLockDeltas(lockDeltas, null);

    List<CDOLockState> lockStates = commitContext.getLockStates();
    out.writeCDOLockStates(lockStates, null);
  }

  protected void respondingNewPermissions(CDODataOutput out) throws Exception
  {
    InternalSession session = getSession();
    IPermissionManager permissionManager = session.getManager().getPermissionManager();
    if (permissionManager != null)
    {
      if (commitContext.getSecurityImpact() != CommitNotificationInfo.IMPACT_REALM)
      {
        out.writeBoolean(true);

        InternalCDORevision[] newObjects = commitContext.getNewObjects();
        InternalCDORevision[] dirtyObjects = commitContext.getDirtyObjects();

        out.writeXInt(newObjects.length + dirtyObjects.length);
        respondingNewPermissions(out, permissionManager, session, newObjects);
        respondingNewPermissions(out, permissionManager, session, dirtyObjects);
        return;
      }
    }

    out.writeBoolean(false);
  }

  protected void respondingNewPermissions(CDODataOutput out, IPermissionManager permissionManager, InternalSession session, InternalCDORevision[] revisions)
      throws Exception
  {
    int size = revisions.length;
    if (size != 0)
    {
      CDOBranchPoint securityContext = commitContext.getBranchPoint();
      if (!session.getManager().getRepository().isSupportingAudits())
      {
        // Compute the permissions from HEAD if auditing is not supported. See bug 578577.
        securityContext = securityContext.getBranch().getHead();
      }

      for (int i = 0; i < size; i++)
      {
        InternalCDORevision revision = revisions[i];
        CDOPermission permission = permissionManager.getPermission(revision, securityContext, session);

        out.writeCDOID(revision.getID());
        out.writeEnum(permission);
      }
    }
  }

  protected void respondingNewCommitData(CDODataOutput out) throws Exception
  {
    CommitData originalCommmitData = commitContext.getOriginalCommmitData();
    if (originalCommmitData != null)
    {
      out.writeBoolean(true);
      new CommitData(commitContext.getNewObjects(), commitContext.getDirtyObjectDeltas(), commitContext.getDetachedObjects()).write(out);
    }
    else
    {
      out.writeBoolean(false);
    }
  }

  protected InternalTransaction getTransaction(int viewID)
  {
    InternalView view = getView(viewID);
    if (view instanceof InternalTransaction)
    {
      return (InternalTransaction)view;
    }

    throw new IllegalStateException("Illegal transaction: " + view); //$NON-NLS-1$
  }

  private ResourceSet createResourceSet(InternalCDOPackageRegistry packageRegistry)
  {
    Resource.Factory resourceFactory = new EcoreResourceFactoryImpl();

    ResourceSet resourceSet = new ExtResourceSet(true, false);
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", resourceFactory); //$NON-NLS-1$
    resourceSet.setPackageRegistry(packageRegistry);
    return resourceSet;
  }
}
