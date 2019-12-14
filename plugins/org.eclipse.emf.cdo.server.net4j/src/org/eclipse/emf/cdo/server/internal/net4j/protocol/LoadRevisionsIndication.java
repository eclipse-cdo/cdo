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
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo.Type;

import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
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

    int fetchSize = in.readXInt();
    if (fetchSize > 0)
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

      for (int i = 0; i < fetchSize; i++)
      {
        CDOFetchRule fetchRule = new CDOFetchRule(in, getRepository().getPackageRegistry());
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

    // Need to fetch the rule first.
    Set<CDOFetchRule> visitedFetchRules = new HashSet<>();
    if (!CDOIDUtil.isNull(contextID) && fetchRules.size() > 0)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Collecting more revisions based on rules"); //$NON-NLS-1$
      }

      RevisionInfo info = getRevisionInfo(contextID);
      InternalCDORevision revisionContext = info.getResult();
      collectRevisions(revisionContext, revisionIDs, additionalRevisionInfos, additionalRevisions, visitedFetchRules);
    }

    InternalCDORevisionManager revisionManager = getRepository().getRevisionManager();
    InternalCDORevision[] revisions = new InternalCDORevision[size];
    for (int i = 0; i < size; i++)
    {
      RevisionInfo info = infos[i];
      info.execute(revisionManager, referenceChunk);
      revisions[i] = info.getResult();
      if (loadRevisionCollectionChunkSize > 0)
      {
        collectRevisions(revisions[i], revisionIDs, additionalRevisionInfos, additionalRevisions, visitedFetchRules);
      }
    }

    if (prefetchDepth != 0)
    {
      prefetchRevisions(prefetchDepth > 0 ? prefetchDepth : Integer.MAX_VALUE, revisions, additionalRevisionInfos, additionalRevisions);
    }

    getRepository().notifyReadAccessHandlers(getSession(), revisions, additionalRevisions);

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

  private RevisionInfo getRevisionInfo(CDOID id)
  {
    RevisionInfo info = new RevisionInfo.Missing(id, branchPoint);
    info.execute(getRepository().getRevisionManager(), referenceChunk);
    return info;
  }

  private void collectRevisions(InternalCDORevision revision, Set<CDOID> revisions, List<RevisionInfo> additionalRevisionInfos,
      List<CDORevision> additionalRevisions, Set<CDOFetchRule> visitedFetchRules)
  {
    if (revision == null)
    {
      return;
    }

    getSession().collectContainedRevisions(revision, branchPoint, referenceChunk, revisions, additionalRevisions);

    CDOFetchRule fetchRule = fetchRules.get(revision.getEClass());
    if (fetchRule == null || visitedFetchRules.contains(fetchRule))
    {
      return;
    }

    visitedFetchRules.add(fetchRule);

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
                RevisionInfo info = getRevisionInfo(id);
                InternalCDORevision containedRevision = info.getResult();
                if (containedRevision != null)
                {
                  additionalRevisionInfos.add(info);
                  revisions.add(containedRevision.getID());
                  additionalRevisions.add(containedRevision);
                  collectRevisions(containedRevision, revisions, additionalRevisionInfos, additionalRevisions, visitedFetchRules);
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
            RevisionInfo info = getRevisionInfo(id);
            InternalCDORevision containedRevision = info.getResult();
            if (containedRevision != null)
            {
              revisions.add(containedRevision.getID());
              additionalRevisions.add(containedRevision);
              collectRevisions(containedRevision, revisions, additionalRevisionInfos, additionalRevisions, visitedFetchRules);
            }
          }
        }
      }
    }

    visitedFetchRules.remove(fetchRule);
  }

  private void prefetchRevisions(int depth, CDORevision[] revisions, List<RevisionInfo> additionalRevisionInfos, List<CDORevision> additionalRevisions)
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

    for (CDORevision revision : revisions)
    {
      prefetchRevision(depth, (InternalCDORevision)revision, additionalRevisionInfos, additionalRevisions, map);
    }
  }

  private void prefetchRevision(int depth, InternalCDORevision revision, List<RevisionInfo> additionalRevisionInfos, List<CDORevision> additionalRevisions,
      Map<CDOID, CDORevision> map)
  {
    CDOClassInfo classInfo = revision.getClassInfo();
    for (EStructuralFeature feature : classInfo.getAllPersistentFeatures())
    {
      if (feature instanceof EReference)
      {
        EReference reference = (EReference)feature;
        if (reference.isContainment())
        {
          Object value = revision.getValue(reference);
          if (value instanceof CDOID)
          {
            CDOID id = (CDOID)value;
            prefetchRevisionChild(depth, id, additionalRevisionInfos, additionalRevisions, map);
          }
          else if (value instanceof Collection<?>)
          {
            Collection<?> c = (Collection<?>)value;
            for (Object e : c)
            {
              // If this revision was loaded with referenceChunk != UNCHUNKED, then
              // some elements might be uninitialized, i.e. not instanceof CDOID.
              // (See bug 339313.)
              if (e instanceof CDOID)
              {
                CDOID id = (CDOID)e;
                prefetchRevisionChild(depth, id, additionalRevisionInfos, additionalRevisions, map);
              }
            }
          }
        }
      }
    }
  }

  private void prefetchRevisionChild(int depth, CDOID id, List<RevisionInfo> additionalRevisionInfos, List<CDORevision> additionalRevisions,
      Map<CDOID, CDORevision> map)
  {
    if (CDOIDUtil.isNull(id))
    {
      return;
    }

    CDORevision child = map.get(id);
    if (child == null)
    {
      RevisionInfo info = getRevisionInfo(id);
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
      prefetchRevision(depth - 1, (InternalCDORevision)child, additionalRevisionInfos, additionalRevisions, map);
    }
  }

}
