/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.CDOFetchRule;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class LoadRevisionIndication extends CDOReadIndication
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL,
      LoadRevisionIndication.class);

  protected CDOID[] ids;

  protected int referenceChunk;

  protected Map<CDOClass, CDOFetchRule> fetchRules = new HashMap<CDOClass, CDOFetchRule>();

  protected CDOID contextID = CDOID.NULL;

  protected int loadRevisionCollectionChunkSize;

  public LoadRevisionIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_REVISION);
  }

  public LoadRevisionIndication(CDOServerProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    referenceChunk = in.readInt();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Read referenceChunk: {0}", referenceChunk);
    }

    int size = in.readInt();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Reading {0} IDs", size);
    }

    ids = new CDOID[size];
    for (int i = 0; i < size; i++)
    {
      CDOID id = in.readCDOID();
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Read ID: {0}", id);
      }

      ids[i] = id;
    }

    int fetchSize = in.readInt();
    if (fetchSize > 0)
    {
      loadRevisionCollectionChunkSize = in.readInt();
      if (loadRevisionCollectionChunkSize < 1)
      {
        loadRevisionCollectionChunkSize = 1;
      }

      contextID = in.readCDOID();
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Reading fetch rules for context {0}", contextID);
      }

      for (int i = 0; i < fetchSize; i++)
      {
        CDOFetchRule fetchRule = new CDOFetchRule(in, getPackageManager());
        fetchRules.put(fetchRule.getCDOClass(), fetchRule);
      }
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    List<CDORevision> additionalRevisions = new ArrayList<CDORevision>();
    Set<CDOID> revisionIDs = new HashSet<CDOID>();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Writing {0} revisions", ids.length);
    }

    for (CDOID id : ids)
    {
      revisionIDs.add(id);
    }

    // Need to fetch the rule first.
    Set<CDOFetchRule> visitedFetchRules = new HashSet<CDOFetchRule>();
    if (!CDOIDUtil.isNull(contextID) && fetchRules.size() > 0)
    {
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Collecting more revisions based on rules");
      }

      InternalCDORevision revisionContext = getRevision(contextID);
      collectRevisions(revisionContext, revisionIDs, additionalRevisions, visitedFetchRules);
    }

    CDORevision[] revisions = new CDORevision[ids.length];
    for (int i = 0; i < ids.length; i++)
    {
      CDOID id = ids[i];
      InternalCDORevision revision = getRevision(id);
      revisions[i] = revision;
      if (loadRevisionCollectionChunkSize > 0)
      {
        collectRevisions(revision, revisionIDs, additionalRevisions, visitedFetchRules);
      }
    }

    getRepository().notifyReadAccessHandlers(getSession(), revisions, additionalRevisions);
    for (CDORevision revision : revisions)
    {
      out.writeCDORevision(revision, referenceChunk);
    }

    int additionalSize = additionalRevisions.size();
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.format("Writing {0} additional revisions", additionalSize);
    }

    out.writeInt(additionalSize);
    for (CDORevision revision : additionalRevisions)
    {
      out.writeCDORevision(revision, referenceChunk);
    }
  }

  protected InternalCDORevision getRevision(CDOID id)
  {
    RevisionManager revisionManager = getRevisionManager();
    return revisionManager.getRevision(id, referenceChunk);
  }

  private void collectRevisions(InternalCDORevision revision, Set<CDOID> revisions,
      List<CDORevision> additionalRevisions, Set<CDOFetchRule> visitedFetchRules)
  {
    getSession().collectContainedRevisions(revision, referenceChunk, revisions, additionalRevisions);
    CDOFetchRule fetchRule = fetchRules.get(revision.getCDOClass());
    if (fetchRule == null || visitedFetchRules.contains(fetchRule))
    {
      return;
    }

    visitedFetchRules.add(fetchRule);

    RevisionManager revisionManager = (RevisionManager)getSessionManager().getRepository().getRevisionManager();
    for (CDOFeature feature : fetchRule.getFeatures())
    {
      if (feature.isMany())
      {
        MoveableList<Object> list = revision.getList(feature);
        int toIndex = Math.min(loadRevisionCollectionChunkSize, list.size()) - 1;
        for (int i = 0; i <= toIndex; i++)
        {
          Object value = list.get(i);
          if (value instanceof CDOID)
          {
            CDOID id = (CDOID)value;
            if (!CDOIDUtil.isNull(id) && !revisions.contains(id))
            {
              InternalCDORevision containedRevision = revisionManager.getRevision(id, referenceChunk);
              revisions.add(containedRevision.getID());
              additionalRevisions.add(containedRevision);
              collectRevisions(containedRevision, revisions, additionalRevisions, visitedFetchRules);
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
            InternalCDORevision containedRevision = revisionManager.getRevision(id, referenceChunk);
            revisions.add(containedRevision.getID());
            additionalRevisions.add(containedRevision);
            collectRevisions(containedRevision, revisions, additionalRevisions, visitedFetchRules);
          }
        }
      }
    }

    visitedFetchRules.remove(fetchRule);
  }
}
