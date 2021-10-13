/*
 * Copyright (c) 2010-2014, 2016-2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.util.CDOFetchRule;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo.Type;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

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
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, LoadRevisionsIndication.class);

  private RevisionInfo[] infos;

  private CDOBranchPoint branchPoint;

  private int referenceChunk;

  private int prefetchDepth;

  private Map<EClass, CDOFetchRule> fetchRules = new HashMap<>();

  private CDOID contextID = CDOID.NULL;

  private int loadRevisionCollectionChunkSize;

  public LoadRevisionsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_REVISIONS);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    branchPoint = in.readCDOBranchPoint();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read branchPoint: {0}", branchPoint); //$NON-NLS-1$
    }

    referenceChunk = in.readXInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read referenceChunk: {0}", referenceChunk); //$NON-NLS-1$
    }

    int size = in.readXInt();
    if (size < 0)
    {
      size = -size;
      prefetchDepth = in.readXInt();
      if (TRACER.isEnabled())
      {
        TRACER.format("Read prefetchDepth: {0}", prefetchDepth); //$NON-NLS-1$
      }
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Reading {0} infos", size); //$NON-NLS-1$
    }

    infos = new RevisionInfo[size];
    for (int i = 0; i < size; i++)
    {
      RevisionInfo info = RevisionInfo.read(in, branchPoint);
      if (TRACER.isEnabled())
      {
        TRACER.format("Read info: {0}", info); //$NON-NLS-1$
      }

      infos[i] = info;
    }

    int fetchRulesCount = in.readXInt();
    if (fetchRulesCount > 0)
    {
      loadRevisionCollectionChunkSize = in.readXInt();
      if (loadRevisionCollectionChunkSize < 1)
      {
        loadRevisionCollectionChunkSize = 1;
      }

      contextID = in.readCDOID();
      if (TRACER.isEnabled())
      {
        TRACER.format("Reading fetch rules for context {0}", contextID); //$NON-NLS-1$
      }

      InternalCDOPackageRegistry packageRegistry = getRepository().getPackageRegistry();

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
    List<CDORevision> additionalRevisions = new ArrayList<>();
    List<RevisionInfo> additionalRevisionInfos = new ArrayList<>();
    Set<CDOID> revisionIDs = new HashSet<>();
    int size = infos.length;
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} results", size); //$NON-NLS-1$
    }

    for (RevisionInfo info : infos)
    {
      revisionIDs.add(info.getID());
    }

    InternalRepository repository = getRepository();
    InternalCDORevisionManager revisionManager = repository.getRevisionManager();

    // Need to fetch the rule first.
    Set<CDOFetchRule> visitedFetchRules = new HashSet<>();
    if (!CDOIDUtil.isNull(contextID) && fetchRules.size() > 0)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Collecting more revisions based on rules"); //$NON-NLS-1$
      }

      RevisionInfo info = getRevisionInfo(revisionManager, contextID);
      InternalCDORevision revisionContext = info.getResult();
      collectRevisions(revisionManager, revisionContext, revisionIDs, additionalRevisionInfos, additionalRevisions, visitedFetchRules);
    }

    InternalCDORevision[] revisions = new InternalCDORevision[size];
    for (int i = 0; i < size; i++)
    {
      RevisionInfo info = infos[i];
      info.execute(revisionManager, referenceChunk);
      revisions[i] = info.getResult();
      if (loadRevisionCollectionChunkSize > 0)
      {
        collectRevisions(revisionManager, revisions[i], revisionIDs, additionalRevisionInfos, additionalRevisions, visitedFetchRules);
      }
    }

    if (prefetchDepth != 0)
    {
      OMMonitor monitor = createMonitor(1, 10);
      // OMMonitor monitor = new Monitor();

      try
      {
        prefetchRevisions(revisionManager, prefetchDepth > 0 ? prefetchDepth : Integer.MAX_VALUE, revisions, additionalRevisionInfos, additionalRevisions,
            monitor);
      }
      finally
      {
        monitor.done();
      }
    }

    repository.notifyReadAccessHandlers(getSession(), revisions, additionalRevisions);

    for (int i = 0; i < size; i++)
    {
      RevisionInfo info = infos[i];
      info.setResult(revisions[i]);
      info.writeResult(out, referenceChunk, branchPoint); // Exposes revision to client side
    }

    int additionalSize = additionalRevisionInfos.size();
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} additional revision infos", additionalSize); //$NON-NLS-1$
    }

    out.writeXInt(additionalSize);
    for (int i = 0; i < additionalSize; i++)
    {
      InternalCDORevision revision = (InternalCDORevision)additionalRevisions.get(i);
      RevisionInfo info = additionalRevisionInfos.get(i);
      info.setResult(revision);
      out.write(Type.MISSING.ordinal());
      out.writeCDOID(info.getID());
      info.writeResult(out, referenceChunk, branchPoint);
    }
  }

  private RevisionInfo getRevisionInfo(InternalCDORevisionManager revisionManager, CDOID id)
  {
    RevisionInfo info = new RevisionInfo.Missing(id, branchPoint);
    info.execute(revisionManager, referenceChunk);
    return info;
  }

  private void collectRevisions(InternalCDORevisionManager revisionManager, InternalCDORevision revision, Set<CDOID> revisions,
      List<RevisionInfo> additionalRevisionInfos, List<CDORevision> additionalRevisions, Set<CDOFetchRule> visitedFetchRules)
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
      if (feature.isMany())
      {
        MoveableList<Object> list = revision.getListOrNull(feature);
        if (list != null)
        {
          int toIndex = Math.min(loadRevisionCollectionChunkSize, list.size()) - 1;
          for (int i = 0; i <= toIndex; i++)
          {
            Object value = list.get(i);
            if (value instanceof CDOID)
            {
              CDOID id = (CDOID)value;
              if (!CDOIDUtil.isNull(id) && !revisions.contains(id))
              {
                RevisionInfo info = getRevisionInfo(revisionManager, id);
                InternalCDORevision containedRevision = info.getResult();
                if (containedRevision != null)
                {
                  additionalRevisionInfos.add(info);
                  revisions.add(containedRevision.getID());
                  additionalRevisions.add(containedRevision);
                  collectRevisions(revisionManager, containedRevision, revisions, additionalRevisionInfos, additionalRevisions, visitedFetchRules);
                }
              }
            }
          }
        }
      }
      else
      {
        Object value = revision.getValue(feature);
        if (value instanceof CDOID)
        {
          CDOID id = (CDOID)value;
          if (!id.isNull() && !revisions.contains(id))
          {
            RevisionInfo info = getRevisionInfo(revisionManager, id);
            InternalCDORevision containedRevision = info.getResult();
            if (containedRevision != null)
            {
              revisions.add(containedRevision.getID());
              additionalRevisions.add(containedRevision);
              collectRevisions(revisionManager, containedRevision, revisions, additionalRevisionInfos, additionalRevisions, visitedFetchRules);
            }
          }
        }
      }
    }

    visitedFetchRules.remove(fetchRule);
  }

  private void prefetchRevisions(InternalCDORevisionManager revisionManager, int depth, CDORevision[] revisions, List<RevisionInfo> additionalRevisionInfos,
      List<CDORevision> additionalRevisions, OMMonitor monitor)
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
      prefetchRevision(revisionManager, depth, (InternalCDORevision)revision, additionalRevisionInfos, additionalRevisions, map, monitor.fork());
    }
  }

  private void prefetchRevision(InternalCDORevisionManager revisionManager, int depth, InternalCDORevision revision, List<RevisionInfo> additionalRevisionInfos,
      List<CDORevision> additionalRevisions, Map<CDOID, CDORevision> map, OMMonitor monitor)
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
        prefetchRevisionChild(revisionManager, depth, id, additionalRevisionInfos, additionalRevisions, map, monitor.fork());
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
            prefetchRevisionChild(revisionManager, depth, id, additionalRevisionInfos, additionalRevisions, map, subMonitor.fork());
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

  private void prefetchRevisionChild(InternalCDORevisionManager revisionManager, int depth, CDOID id, List<RevisionInfo> additionalRevisionInfos,
      List<CDORevision> additionalRevisions, Map<CDOID, CDORevision> map, OMMonitor monitor)
  {
    if (CDOIDUtil.isNull(id))
    {
      return;
    }

    CDORevision child = map.get(id);
    if (child == null)
    {
      RevisionInfo info = getRevisionInfo(revisionManager, id);
      child = info.getResult();
      if (child != null)
      {
        map.put(id, child);
        additionalRevisions.add(child);
        additionalRevisionInfos.add(info);
      }
    }

    if (child != null && depth > 0)
    {
      prefetchRevision(revisionManager, depth - 1, (InternalCDORevision)child, additionalRevisionInfos, additionalRevisions, map, monitor);
    }
  }
}
