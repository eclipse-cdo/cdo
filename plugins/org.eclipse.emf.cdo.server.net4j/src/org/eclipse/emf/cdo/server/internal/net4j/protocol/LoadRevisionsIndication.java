/*
 * Copyright (c) 2010-2014, 2016-2019, 2021, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.util.CDOFetchRule;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.PointerCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo.Type;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class LoadRevisionsIndication extends CDOServerReadIndication
{
  private RevisionInfo[] infos;

  private CDOBranchPoint branchPoint;

  private int referenceChunk;

  private int prefetchDepth;

  private final Map<EClass, CDOFetchRule> fetchRules = new HashMap<>();

  private CDOID contextID = CDOID.NULL;

  private int loadRevisionCollectionChunkSize;

  private Set<CDORevisionKey> validKeys;

  private InternalCDORevisionManager revisionManager;

  private CDOServerLockStatePrefetcher lockStatePrefetcher;

  public LoadRevisionsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_REVISIONS);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    InternalRepository repository = getRepository();
    revisionManager = repository.getRevisionManager();

    branchPoint = in.readCDOBranchPoint();
    referenceChunk = in.readXInt();

    boolean prefetchLockStates = in.readBoolean();
    lockStatePrefetcher = CDOServerLockStatePrefetcher.create(repository, branchPoint, prefetchLockStates);

    int size = in.readXInt();
    if (size < 0)
    {
      size = -size;

      prefetchDepth = in.readXInt();
      if (prefetchDepth != CDORevision.DEPTH_NONE)
      {
        validKeys = new HashSet<>();

        for (;;)
        {
          CDORevisionKey key = in.readCDORevisionKey();
          if (key == null)
          {
            break;
          }

          validKeys.add(key);
        }
      }
    }

    infos = new RevisionInfo[size];
    for (int i = 0; i < size; i++)
    {
      infos[i] = RevisionInfo.read(in, branchPoint);
    }

    int fetchRulesCount = in.readXInt();
    if (fetchRulesCount > 0)
    {
      loadRevisionCollectionChunkSize = in.readXInt();
      if (loadRevisionCollectionChunkSize < 1)
      {
        loadRevisionCollectionChunkSize = Integer.MAX_VALUE;
      }

      contextID = in.readCDOID();

      InternalCDOPackageRegistry packageRegistry = repository.getPackageRegistry();

      for (int i = 0; i < fetchRulesCount; i++)
      {
        CDOFetchRule fetchRule = new CDOFetchRule(in, packageRegistry);
        fetchRules.put(fetchRule.getEClass(), fetchRule);
      }
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    List<RevisionInfo> additionalInfos = new ArrayList<>();
    List<CDORevision> additionalRevisions = new ArrayList<>();
    Set<CDOID> revisionIDs = new HashSet<>();
    int size = infos.length;

    for (RevisionInfo info : infos)
    {
      revisionIDs.add(info.getID());
    }

    // Need to fetch the rule first.
    Set<CDOFetchRule> visitedFetchRules = new HashSet<>();
    if (!CDOIDUtil.isNull(contextID) && fetchRules.size() > 0)
    {
      RevisionInfo contextRevisionInfo = createRevisionInfo(contextID);
      InternalCDORevision contextRevision = contextRevisionInfo.getResult();
      collectRevisions(contextRevision, revisionIDs, additionalInfos, additionalRevisions, visitedFetchRules);
    }

    InternalCDORevision[] revisions = new InternalCDORevision[size];
    for (int i = 0; i < size; i++)
    {
      RevisionInfo info = infos[i];
      executeRevisionInfo(info);

      revisions[i] = info.getResult();

      if (loadRevisionCollectionChunkSize > 0)
      {
        collectRevisions(revisions[i], revisionIDs, additionalInfos, additionalRevisions, visitedFetchRules);
      }
    }

    if (prefetchDepth != CDORevision.DEPTH_NONE)
    {
      OMMonitor monitor = createMonitor(1, 10);

      try
      {
        prefetchRevisions(prefetchDepth > 0 ? prefetchDepth : Integer.MAX_VALUE, revisions, additionalInfos, additionalRevisions, monitor);
      }
      finally
      {
        monitor.done();
      }
    }

    // Read access handlers may modify/replace the revisions and additionalRevisions,
    // which must be reflected in the infos and additionalInfos below!
    InternalRepository repository = getRepository();
    InternalSession session = getSession();
    repository.notifyReadAccessHandlers(session, revisions, additionalRevisions);

    for (int i = 0; i < size; i++)
    {
      RevisionInfo info = infos[i];
      info.setResult(revisions[i]); // Replace result with revision from read access handlers.
      info.writeResult(out, referenceChunk, branchPoint);
    }

    // Write keys of additional revisions that are already cached on the client.
    int additionalSize = additionalRevisions.size();
    List<RevisionInfo> infosToWrite = new ArrayList<>();
    CDOBranch requestedBranch = branchPoint.getBranch();

    for (int i = 0; i < additionalSize; i++)
    {
      InternalCDORevision revision = (InternalCDORevision)additionalRevisions.get(i);
      RevisionInfo info = additionalInfos.get(i);

      if (validKeys != null && validKeys.contains(CDORevisionUtil.copyRevisionKey(revision)))
      {
        InternalCDORevision result = info.getResult();
        out.writeCDORevisionKey(result);

        if (result.getBranch() != requestedBranch)
        {
          PointerCDORevision pointer = (PointerCDORevision)info.getSynthetic();
          out.writeXLong(pointer.getRevised());
        }
      }
      else
      {
        info.setResult(revision); // Replace result with revision from read access handlers.
        infosToWrite.add(info);
      }
    }

    out.writeCDORevisionKey(null);

    // Write remaining additional infos.
    out.writeXInt(infosToWrite.size());

    for (RevisionInfo info : infosToWrite)
    {
      out.write(Type.MISSING.ordinal());
      out.writeCDOID(info.getID());
      info.writeResult(out, referenceChunk, branchPoint);
    }

    lockStatePrefetcher.writeLockStates(out);
  }

  private RevisionInfo createRevisionInfo(CDOID id)
  {
    RevisionInfo info = new RevisionInfo.Missing(id, branchPoint);
    executeRevisionInfo(info);
    return info;
  }

  private void executeRevisionInfo(RevisionInfo info)
  {
    info.execute(revisionManager, referenceChunk);

    lockStatePrefetcher.addLockStateKey(() -> {
      CDORevision revision = info.getSynthetic();
      if (revision == null)
      {
        revision = info.getResult();
      }

      if (revision != null && !(revision instanceof DetachedCDORevision))
      {
        return revision.getID();
      }

      return null;
    });
  }

  private void collectRevisions(InternalCDORevision revision, Set<CDOID> revisions, List<RevisionInfo> additionalInfos, List<CDORevision> additionalRevisions,
      Set<CDOFetchRule> visitedFetchRules)
  {
    if (revision == null)
    {
      return;
    }

    getSession().collectContainedRevisions(revision, branchPoint, referenceChunk, revisions, additionalRevisions);

    CDOFetchRule fetchRule = fetchRules.get(revision.getEClass());
    if (fetchRule == null || !visitedFetchRules.add(fetchRule))
    {
      return;
    }

    for (EStructuralFeature feature : fetchRule.getFeatures())
    {
      CDORevisionUtil.forEachValue(revision, feature, loadRevisionCollectionChunkSize, value -> {
        if (value instanceof CDOID)
        {
          CDOID id = (CDOID)value;

          if (!CDOIDUtil.isNull(id) && !revisions.contains(id))
          {
            RevisionInfo info = createRevisionInfo(id);
            InternalCDORevision containedRevision = info.getResult();
            if (containedRevision != null)
            {
              additionalInfos.add(info);
              revisions.add(containedRevision.getID());
              additionalRevisions.add(containedRevision);
              collectRevisions(containedRevision, revisions, additionalInfos, additionalRevisions, visitedFetchRules);
            }
          }
        }
      });
    }

    visitedFetchRules.remove(fetchRule);
  }

  private void prefetchRevisions(int depth, CDORevision[] revisions, List<RevisionInfo> additionalInfos, List<CDORevision> additionalRevisions,
      OMMonitor monitor)
  {
    Map<CDOID, CDORevision> map = CDOIDUtil.createMap();
    for (CDORevision revision : revisions)
    {
      map.put(revision.getID(), revision);
    }

    for (CDORevision revision : additionalRevisions)
    {
      map.put(revision.getID(), revision);
    }

    monitor.begin(revisions.length);

    for (CDORevision revision : revisions)
    {
      prefetchRevision(depth, (InternalCDORevision)revision, additionalInfos, additionalRevisions, map, monitor.fork());
    }
  }

  private void prefetchRevision(int depth, InternalCDORevision revision, List<RevisionInfo> additionalInfos, List<CDORevision> additionalRevisions,
      Map<CDOID, CDORevision> map, OMMonitor monitor)
  {
    CDOClassInfo classInfo = revision.getClassInfo();
    EStructuralFeature[] containments = classInfo.getAllPersistentContainments();
    monitor.begin(containments.length);

    for (EStructuralFeature feature : containments)
    {
      Object value = revision.getValue(feature);
      if (value instanceof CDOID)
      {
        CDOID id = (CDOID)value;
        prefetchRevisionChild(depth, id, additionalInfos, additionalRevisions, map, monitor.fork());
      }
      else if (value instanceof Collection<?>)
      {
        Collection<?> c = (Collection<?>)value;

        OMMonitor subMonitor = monitor.fork();
        subMonitor.begin(c.size());

        for (Object e : c)
        {
          // Bug 339313: If this revision was loaded with referenceChunk != UNCHUNKED, then
          // some elements might be uninitialized, i.e. not instanceof CDOID.
          if (e instanceof CDOID)
          {
            CDOID id = (CDOID)e;
            prefetchRevisionChild(depth, id, additionalInfos, additionalRevisions, map, subMonitor.fork());
          }
          else
          {
            subMonitor.worked();
          }
        }

        subMonitor.done();
      }
      else
      {
        monitor.worked();
      }
    }

    monitor.done();
  }

  private void prefetchRevisionChild(int depth, CDOID id, List<RevisionInfo> additionalInfos, List<CDORevision> additionalRevisions,
      Map<CDOID, CDORevision> map, OMMonitor monitor)
  {
    if (CDOIDUtil.isNull(id))
    {
      return;
    }

    CDORevision child = map.get(id);
    if (child == null)
    {
      RevisionInfo info = createRevisionInfo(id);
      child = info.getResult();
      if (child != null)
      {
        map.put(id, child);
        additionalRevisions.add(child);
        additionalInfos.add(info);
      }
    }

    if (child != null && depth > 0)
    {
      prefetchRevision(depth - 1, (InternalCDORevision)child, additionalInfos, additionalRevisions, map, monitor);
    }
  }
}
