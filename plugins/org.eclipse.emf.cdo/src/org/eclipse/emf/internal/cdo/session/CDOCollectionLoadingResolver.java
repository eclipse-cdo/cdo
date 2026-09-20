/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.revision.CDOCollectionLoadingConfig.ChunkConfig;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListResolver;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Executes collection loading requirements for a session.
 *
 * @author Eike Stepper
 */
public final class CDOCollectionLoadingResolver implements CDOListResolver
{
  private final CDOSession session;

  public CDOCollectionLoadingResolver(CDOSession session)
  {
    this.session = session;
  }

  @Override
  public Object resolveProxy(CDORevision revision, EStructuralFeature feature, int accessIndex, int serverIndex)
  {
    CDOSessionImpl internalSession = (CDOSessionImpl)session;
    if (session.options().getCollectionLoadingConfig() != null)
    {
      ChunkConfig effective = internalSession.resolveCollectionLoadingConfig(feature);
      if (effective == null || effective.getResolveChunkSize() == ChunkConfig.INHERIT)
      {
        throw new IllegalStateException("Modern collection-loading configuration is not fully resolved for feature " + feature.getName());
      }

      return loadModernChunk((InternalCDORevision)revision, feature, accessIndex, effective.getResolveChunkSize());
    }

    int chunkSize = internalSession.getEffectiveLegacyCollectionLoadingStrategy().getResolveChunkSize(revision, feature);
    if (chunkSize == CDORevision.UNCHUNKED)
    {
      chunkSize = Integer.MAX_VALUE;
    }

    return loadChunk((InternalCDORevision)revision, feature, accessIndex, serverIndex, chunkSize);
  }

  private Object loadModernChunk(InternalCDORevision revision, EStructuralFeature feature, int accessIndex, int chunkSize)
  {
    CDOList list = revision.getListOrNull(feature);
    if (list == null)
    {
      return null;
    }

    InternalCDOList internalList = (InternalCDOList)list;
    int size = list.size();
    int fromIndex = accessIndex;
    int toIndex = accessIndex;
    int anchorServerIndex = internalList.getServerIndexAt(accessIndex);
    int fromServerIndex = anchorServerIndex;
    int toServerIndex = anchorServerIndex;

    if (chunkSize != ChunkConfig.NONE)
    {
      boolean all = chunkSize == ChunkConfig.ALL;
      boolean leftBlocked = false;
      boolean rightBlocked = false;
      boolean leftFirst = true;
      int count = 1;

      while (all || count < chunkSize)
      {
        if (leftFirst && !leftBlocked)
        {
          if (fromIndex > 0 && !list.isLoadedAt(fromIndex - 1) && internalList.getServerIndexAt(fromIndex - 1) == fromServerIndex - 1)
          {
            --fromIndex;
            fromServerIndex = internalList.getServerIndexAt(fromIndex);
            ++count;
          }
          else
          {
            leftBlocked = true;
          }
        }

        if (!rightBlocked && (leftBlocked || !leftFirst))
        {
          if (toIndex < size - 1 && !list.isLoadedAt(toIndex + 1) && internalList.getServerIndexAt(toIndex + 1) == toServerIndex + 1)
          {
            ++toIndex;
            toServerIndex = internalList.getServerIndexAt(toIndex);
            ++count;
          }
          else
          {
            rightBlocked = true;
          }
        }

        if (leftBlocked && rightBlocked)
        {
          break;
        }

        if (count >= chunkSize && !all)
        {
          break;
        }

        leftFirst = !leftFirst;
      }
    }

    return loadExactChunk(revision, feature, accessIndex, anchorServerIndex, fromIndex, toIndex);
  }

  private Object loadExactChunk(InternalCDORevision revision, EStructuralFeature feature, int accessIndex, int fetchIndex, int fromIndex, int toIndex)
  {
    CDOSessionProtocol protocol = ((InternalCDOSession)session).getSessionProtocol();
    Object result = protocol.loadChunk(revision, feature, accessIndex, fetchIndex, fromIndex, toIndex);

    CDOList list = revision.getListOrNull(feature);
    if (list != null && !list.isLoadedAt(accessIndex))
    {
      throw new IllegalStateException("Collection loading did not load the requested index " + accessIndex);
    }

    return result;
  }

  @Override
  public void resolveAllProxies(CDORevision revision, EStructuralFeature feature)
  {
    InternalCDORevision internalRevision = (InternalCDORevision)revision;

    CDOList list = internalRevision.getListOrNull(feature);
    if (list == null || list.isFullyLoaded())
    {
      return;
    }

    List<CDOSessionProtocol.ChunkRange> ranges = new ArrayList<>();
    InternalCDOList internalList = (InternalCDOList)list;

    for (int start = 0; start < list.size();)
    {
      if (list.isLoadedAt(start))
      {
        ++start;
        continue;
      }

      int accessStart = start;
      int serverStart = internalList.getServerIndexAt(start);
      int previousServerIndex = serverStart;
      int end = start;

      while (end + 1 < list.size() && !list.isLoadedAt(end + 1))
      {
        int nextServerIndex = internalList.getServerIndexAt(end + 1);
        if (nextServerIndex != previousServerIndex + 1)
        {
          break;
        }

        ++end;
        previousServerIndex = nextServerIndex;
      }

      ranges.add(new CDOSessionProtocol.ChunkRange(accessStart, serverStart, accessStart, end));
      start = end + 1;
    }

    if (!ranges.isEmpty())
    {
      CDOSessionProtocol protocol = ((InternalCDOSession)session).getSessionProtocol();
      protocol.loadChunk(internalRevision, feature, ranges);
    }

    if (!list.isFullyLoaded())
    {
      throw new IllegalStateException("Collection loading did not fully load feature " + feature.getName());
    }
  }

  private Object loadChunk(InternalCDORevision revision, EStructuralFeature feature, int accessIndex, int serverIndex, int chunkSize)
  {
    CDOList list = revision.getListOrNull(feature);
    if (list == null)
    {
      return null;
    }

    InternalCDOList internalList = (InternalCDOList)list;
    serverIndex = internalList.getServerIndexAt(accessIndex);

    int size = list.size();
    int fromIndex = accessIndex;
    int toIndex = accessIndex;
    int fromServerIndex = serverIndex;
    int toServerIndex = serverIndex;
    boolean minReached = false;
    boolean maxReached = false;
    boolean alternation = false;

    for (int i = 0; i < chunkSize; i++)
    {
      if (alternation)
      {
        if (!maxReached && toIndex < size - 1 && !list.isLoadedAt(toIndex + 1))
        {
          int nextServerIndex = internalList.getServerIndexAt(toIndex + 1);
          if (nextServerIndex == toServerIndex + 1)
          {
            ++toIndex;
            toServerIndex = nextServerIndex;
          }
          else
          {
            maxReached = true;
          }
        }
        else
        {
          maxReached = true;
        }

        if (!minReached)
        {
          alternation = false;
        }
      }
      else
      {
        if (!minReached && fromIndex > 0 && !list.isLoadedAt(fromIndex - 1))
        {
          int previousServerIndex = internalList.getServerIndexAt(fromIndex - 1);
          if (previousServerIndex == fromServerIndex - 1)
          {
            --fromIndex;
            fromServerIndex = previousServerIndex;
          }
          else
          {
            minReached = true;
          }
        }
        else
        {
          minReached = true;
        }

        if (!maxReached)
        {
          alternation = true;
        }
      }

      if (minReached && maxReached)
      {
        break;
      }
    }

    CDOSessionProtocol protocol = ((InternalCDOSession)session).getSessionProtocol();
    Object result = protocol.loadChunk(revision, feature, accessIndex, serverIndex, fromIndex, toIndex);

    if (!list.isLoadedAt(accessIndex))
    {
      throw new IllegalStateException("Collection loading did not load the requested index " + accessIndex);
    }

    return result;
  }
}
