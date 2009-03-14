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
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.common.CDOFetchRule;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.session.CDORevisionManagerImpl;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class LoadRevisionRequest extends CDOClientRequest<List<InternalCDORevision>>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, LoadRevisionRequest.class);

  private Collection<CDOID> ids;

  private int referenceChunk;

  public LoadRevisionRequest(CDOClientProtocol protocol, Collection<CDOID> ids, int referenceChunk)
  {
    this(protocol, CDOProtocolConstants.SIGNAL_LOAD_REVISION, ids, referenceChunk);
  }

  public LoadRevisionRequest(CDOClientProtocol protocol, short signalID, Collection<CDOID> ids, int referenceChunk)
  {
    super(protocol, signalID);
    this.ids = ids;
    this.referenceChunk = referenceChunk;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing referenceChunk: {0}", referenceChunk);
    }

    out.writeInt(referenceChunk);
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} IDs", ids.size());
    }

    out.writeInt(ids.size());
    for (CDOID id : ids)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing ID: {0}", id);
      }

      out.writeCDOID(id);
    }

    CDORevisionManagerImpl revisionManager = (CDORevisionManagerImpl)getSession().getRevisionManager();
    CDOFetchRuleManager ruleManager = revisionManager.getRuleManager();
    List<CDOFetchRule> fetchRules = ruleManager.getFetchRules(ids);
    if (fetchRules == null || fetchRules.size() <= 0)
    {
      out.writeInt(0);
    }
    else
    {
      // At this point, fetch size is more than one.
      int fetchSize = fetchRules.size();
      CDOID contextID = ruleManager.getContext();

      out.writeInt(fetchSize);
      out.writeInt(ruleManager.getCollectionLoadingPolicy().getInitialChunkSize());
      out.writeCDOID(contextID);

      for (CDOFetchRule fetchRule : fetchRules)
      {
        fetchRule.write(out);
      }
    }
  }

  @Override
  protected List<InternalCDORevision> confirming(CDODataInput in) throws IOException
  {
    int idSize = ids.size();
    ArrayList<InternalCDORevision> revisions = new ArrayList<InternalCDORevision>(idSize);
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading {0} revisions", idSize);
    }

    for (int i = 0; i < idSize; i++)
    {
      InternalCDORevision revision = (InternalCDORevision)in.readCDORevision();
      revisions.add(revision);
    }

    int additionalSize = in.readInt();
    if (additionalSize != 0)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Reading {0} additional revisions", additionalSize);
      }

      CDORevisionManagerImpl revisionManager = (CDORevisionManagerImpl)getSession().getRevisionManager();
      for (int i = 0; i < additionalSize; i++)
      {
        InternalCDORevision revision = (InternalCDORevision)in.readCDORevision();
        if (revision != null)
        {
          revisionManager.addCachedRevision(revision);
        }
      }
    }

    return revisions;
  }

  public Collection<CDOID> getIds()
  {
    return ids;
  }

  public int getReferenceChunk()
  {
    return referenceChunk;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}(ids={1}, referenceChunk={2})", getClass().getSimpleName(), ids, referenceChunk);
  }
}
