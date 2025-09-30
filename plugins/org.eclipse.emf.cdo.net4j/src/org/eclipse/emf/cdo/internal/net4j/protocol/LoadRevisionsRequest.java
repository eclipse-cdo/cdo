/*
 * Copyright (c) 2010-2012, 2014-2017, 2019, 2021, 2023, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.util.CDOFetchRule;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.spi.common.revision.PointerCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.concurrent.Worker.Terminate;
import org.eclipse.net4j.util.io.IORuntimeException;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class LoadRevisionsRequest extends CDOClientRequest<List<RevisionInfo>>
{
  private final List<RevisionInfo> infos;

  private final CDOBranchPoint branchPoint;

  private final int referenceChunk;

  private final int prefetchDepth;

  private final boolean prefetchLockStates;

  /**
   * Remembers strong references to valid revisions to prevent garbage collection.
   */
  private Map<CDORevisionKey, CDORevision> rememberedRevisions;

  public LoadRevisionsRequest(CDOClientProtocol protocol, List<RevisionInfo> infos, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth,
      boolean prefetchLockStates)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_REVISIONS);
    this.infos = infos;
    this.branchPoint = branchPoint;
    this.referenceChunk = referenceChunk;
    this.prefetchDepth = prefetchDepth;
    this.prefetchLockStates = prefetchLockStates;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeCDOBranchPoint(branchPoint);
    out.writeXInt(referenceChunk);
    out.writeBoolean(prefetchLockStates);

    InternalCDOSession session = getSession();
    int size = infos.size();

    if (prefetchDepth == CDORevision.DEPTH_NONE)
    {
      out.writeXInt(size);
    }
    else
    {
      out.writeXInt(-size);
      out.writeXInt(prefetchDepth);

      InternalCDORevisionCache cache = session.getRevisionManager().getCache();
      boolean branching = session.getRepositoryInfo().isSupportingBranches();
      rememberedRevisions = new HashMap<>();

      int maxRevisionKeys = session.options().getPrefetchSendMaxRevisionKeys();
      if (maxRevisionKeys != 0)
      {
        try
        {
          int[] revisionKeys = { 0 };

          cache.forEachValidRevision(branchPoint, branching, r -> {
            if (++revisionKeys[0] > maxRevisionKeys)
            {
              throw new Terminate();
            }

            try
            {
              CDORevisionKey key = CDORevisionUtil.copyRevisionKey(r);

              // Remember a strong reference prevents garbage collection.
              rememberedRevisions.put(key, r);

              out.writeCDORevisionKey(key);
            }
            catch (IOException ex)
            {
              throw new IORuntimeException(ex);
            }
          });
        }
        catch (Terminate ex)
        {
          // Just go on.
          // $FALL-THROUGH$
        }
        catch (IORuntimeException ex)
        {
          ex.rethrow();
        }
      }

      out.writeCDORevisionKey(null);
    }

    Collection<CDOID> ids = new ArrayList<>(size);
    for (RevisionInfo info : infos)
    {
      info.write(out);
      ids.add(info.getID());
    }

    CDOFetchRuleManager ruleManager = session.getFetchRuleManager();
    List<CDOFetchRule> fetchRules = ruleManager.getFetchRules(ids);

    int fetchRulesCount = ObjectUtil.size(fetchRules);
    out.writeXInt(fetchRulesCount);

    if (fetchRulesCount > 0)
    {
      CDOCollectionLoadingPolicy collectionLoadingPolicy = ruleManager.getCollectionLoadingPolicy();
      out.writeXInt(collectionLoadingPolicy != null ? collectionLoadingPolicy.getInitialChunkSize() : CDORevision.UNCHUNKED);

      CDOID contextID = ruleManager.getContext();
      out.writeCDOID(contextID);

      for (CDOFetchRule fetchRule : fetchRules)
      {
        fetchRule.write(out);
      }
    }
  }

  @Override
  protected List<RevisionInfo> confirming(CDODataInput in) throws IOException
  {
    for (RevisionInfo info : infos)
    {
      info.readResult(in);
    }

    List<RevisionInfo> additionalInfos = null;
    CDOBranch requestedBranch = branchPoint.getBranch();

    // Read keys of additional revisions that are already cached locally.
    for (;;)
    {
      CDORevisionKey key = in.readCDORevisionKey();
      if (key == null)
      {
        break;
      }

      if (additionalInfos == null)
      {
        additionalInfos = new ArrayList<>();
      }

      CDORevision revision = rememberedRevisions.get(key);

      RevisionInfo info = new RememberedRevisionInfo(revision);
      info.setResult((InternalCDORevision)revision);

      if (revision.getBranch() != requestedBranch)
      {
        info.setSynthetic(new PointerCDORevision(revision.getEClass(), revision.getID(), requestedBranch, in.readXLong(), revision));
      }

      additionalInfos.add(info);
    }

    // Read remaining additional infos.
    int additionalSize = in.readXInt();
    if (additionalSize != 0)
    {
      if (additionalInfos == null)
      {
        additionalInfos = new ArrayList<>(additionalSize);
      }

      for (int i = 0; i < additionalSize; i++)
      {
        RevisionInfo info = RevisionInfo.read(in, branchPoint);
        info.readResult(in);
        additionalInfos.add(info);
      }
    }

    InternalCDOSession session = getSession();
    CDOClientProtocol.readAndCacheLockStates(in, session, requestedBranch);

    if (rememberedRevisions != null)
    {
      rememberedRevisions.clear(); // Help the garbage collector.
    }

    return additionalInfos;
  }

  @Override
  protected String getAdditionalInfo()
  {
    return MessageFormat.format("infos={0}, branchPoint={1}, referenceChunk={2}, prefetchDepth={3}, prefetchLockStates={4}", //
        infos, branchPoint, referenceChunk, prefetchDepth, prefetchLockStates);
  }

  /**
   * @author Eike Stepper
   */
  private static final class RememberedRevisionInfo extends RevisionInfo.Available
  {
    public RememberedRevisionInfo(CDORevision revision)
    {
      super(revision.getID(), null, revision);
    }

    @Override
    public Type getType()
    {
      return null;
    }
  }
}
