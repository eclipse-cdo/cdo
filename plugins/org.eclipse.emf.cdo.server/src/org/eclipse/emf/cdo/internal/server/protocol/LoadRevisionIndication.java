/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.CDOIDNull;
import org.eclipse.emf.cdo.internal.protocol.analyzer.CDOFetchRule;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl.MoveableList;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class LoadRevisionIndication extends CDOReadIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, LoadRevisionIndication.class);

  protected CDOID[] ids;

  protected int referenceChunk;

  protected Map<CDOClass, CDOFetchRule> fetchRules = new HashMap<CDOClass, CDOFetchRule>();

  protected CDOID contextID = CDOIDNull.NULL;

  public LoadRevisionIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_LOAD_REVISION;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    referenceChunk = in.readInt();
    if (PROTOCOL.isEnabled()) PROTOCOL.format("Read referenceChunk: {0}", referenceChunk);

    int size = in.readInt();
    if (PROTOCOL.isEnabled()) PROTOCOL.format("Reading {0} IDs", size);

    ids = new CDOID[size];
    for (int i = 0; i < size; i++)
    {
      CDOID id = CDOIDImpl.read(in);
      if (PROTOCOL.isEnabled()) PROTOCOL.format("Read ID: {0}", id);
      ids[i] = id;
    }

    int fetchSize = in.readInt();
    if (fetchSize > 0)
    {
      contextID = CDOIDImpl.read(in);
      for (int i = 0; i < fetchSize; i++)
      {
        CDOFetchRule fetchRule = new CDOFetchRule(in, getPackageManager());
        fetchRules.put(fetchRule.getCDOClass(), fetchRule);
      }
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    Session session = getSession();
    Map<CDOID, CDORevisionImpl> containedRevisions = new HashMap<CDOID, CDORevisionImpl>(0);

    if (PROTOCOL.isEnabled()) PROTOCOL.format("Writing {0} revisions", ids.length);
    for (CDOID id : ids)
    {
      CDORevisionImpl revision = getRevision(id);
      revision.write(out, session, referenceChunk);
      session.collectContainedRevisions(revision, referenceChunk, containedRevisions);
    }

    if (!contextID.isNull() && fetchRules.size() > 0)
    {
      // TODO What is this good for? Why 1000?
      int newReferenceChunk = Math.max(referenceChunk, 1000);
      CDORevisionImpl revisionContext = getRevision(contextID);
      collectRevisionsByRule(revisionContext, newReferenceChunk, containedRevisions, new HashSet<CDOFetchRule>());
    }

    out.writeInt(containedRevisions.size());
    if (PROTOCOL.isEnabled()) PROTOCOL.format("Writing {0} additional revisions", containedRevisions.size());
    for (CDORevisionImpl revision : containedRevisions.values())
    {
      revision.write(out, session, referenceChunk);
    }
  }

  protected CDORevisionImpl getRevision(CDOID id)
  {
    return getRevisionManager().getRevision(id, referenceChunk);
  }

  private void collectRevisionsByRule(CDORevisionImpl revision, int referenceChunk,
      Map<CDOID, CDORevisionImpl> containedRevisions, Set<CDOFetchRule> workingFetchRules)
  {
    CDOFetchRule fetchRule = fetchRules.get(revision.getCDOClass());
    if (fetchRule == null || workingFetchRules.contains(fetchRule))
    {
      return;
    }

    workingFetchRules.add(fetchRule);
    RevisionManager revisionManager = getSessionManager().getRepository().getRevisionManager();
    for (CDOFeature feature : fetchRule.getFeatures())
    {
      if (feature.isMany())
      {
        MoveableList list = revision.getList(feature);
        int toIndex = Math.min(referenceChunk, list.size()) - 1;
        for (int i = 0; i <= toIndex; i++)
        {
          Object value = list.get(i);
          if (value instanceof CDOID)
          {
            CDOID id = (CDOID)value;
            // TODO Missing here? if (!id.isNull() &&
            // !containedRevisions.containsKey(id))
            CDORevisionImpl containedRevision = revisionManager.getRevision(id, referenceChunk);
            containedRevisions.put(containedRevision.getID(), containedRevision);
            collectRevisionsByRule(containedRevision, referenceChunk, containedRevisions, workingFetchRules);
          }
        }
      }
      else
      {
        Object value = revision.getValue(feature);
        if (value instanceof CDOID)
        {
          CDOID id = (CDOID)value;
          if (!id.isNull() && !containedRevisions.containsKey(id))
          {
            CDORevisionImpl containedRevision = revisionManager.getRevision(id, referenceChunk);
            containedRevisions.put(containedRevision.getID(), containedRevision);
            collectRevisionsByRule(containedRevision, referenceChunk, containedRevisions, workingFetchRules);
          }
        }
      }
    }

    workingFetchRules.remove(fetchRule);
  }
}
