/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.analyzer.CDOFetchRuleManager;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.analyzer.CDOFetchRule;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.emf.internal.cdo.CDORevisionManagerImpl;
import org.eclipse.emf.internal.cdo.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.CDOSessionPackageManagerImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
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
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, LoadRevisionRequest.class);

  private Collection<CDOID> ids;

  private int referenceChunk;

  public LoadRevisionRequest(IChannel channel, Collection<CDOID> ids, int referenceChunk)
  {
    super(channel);
    this.ids = ids;
    this.referenceChunk = referenceChunk;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_LOAD_REVISION;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing referenceChunk: {0}", referenceChunk);
    }
    out.writeInt(referenceChunk);

    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing {0} IDs", ids.size());
    }
    out.writeInt(ids.size());

    for (CDOID id : ids)
    {
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Writing ID: {0}", id);
      }
      CDOIDUtil.write(out, id);
    }

    CDOFetchRuleManager ruleManager = getSession().getRevisionManager().getRuleManager();
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
      out.writeInt(ruleManager.getLoadRevisionCollectionChunkSize());
      CDOIDUtil.write(out, contextID);

      for (CDOFetchRule fetchRule : fetchRules)
      {
        fetchRule.write(out);
      }
    }
  }

  @Override
  protected List<InternalCDORevision> confirming(ExtendedDataInputStream in) throws IOException
  {
    CDOSessionImpl session = getSession();
    CDORevisionManagerImpl revisionManager = session.getRevisionManager();
    CDOSessionPackageManagerImpl packageManager = session.getPackageManager();
    ArrayList<InternalCDORevision> revisions = new ArrayList<InternalCDORevision>(ids.size());

    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Reading {0} revisions", ids.size());
    }
    for (int i = 0; i < ids.size(); i++)
    {
      InternalCDORevision revision = (InternalCDORevision)CDORevisionUtil.read(in, revisionManager, packageManager);
      revisions.add(revision);
    }

    int size = in.readInt();
    if (size != 0)
    {
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Reading {0} additional revisions", size);
      }
      for (int i = 0; i < size; i++)
      {
        InternalCDORevision revision = (InternalCDORevision)CDORevisionUtil.read(in, revisionManager, packageManager);
        revisionManager.addRevision(revision);
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
