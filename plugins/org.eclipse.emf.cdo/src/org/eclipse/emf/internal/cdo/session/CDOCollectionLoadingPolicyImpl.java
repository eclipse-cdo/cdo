/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.collection.MoveableList;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOCollectionLoadingPolicyImpl implements CDOCollectionLoadingPolicy
{
  private CDOSession session;

  private int initialChunkSize;

  private int resolveChunkSize;

  public CDOCollectionLoadingPolicyImpl(int initialChunkSize, int resolveChunkSize)
  {
    this.resolveChunkSize = resolveChunkSize <= 0 ? CDORevision.UNCHUNKED : resolveChunkSize;
    this.initialChunkSize = initialChunkSize < 0 ? this.resolveChunkSize : initialChunkSize;
  }

  @Override
  public CDOSession getSession()
  {
    return session;
  }

  @Override
  public void setSession(CDOSession session)
  {
    this.session = session;
  }

  @Override
  public int getInitialChunkSize()
  {
    return initialChunkSize;
  }

  @Override
  public int getResolveChunkSize()
  {
    return resolveChunkSize;
  }

  @Override
  public void resolveAllProxies(CDORevision revision, EStructuralFeature feature)
  {
    doResolveProxy(revision, feature, 0, 0, Integer.MAX_VALUE);
  }

  @Override
  public Object resolveProxy(CDORevision rev, EStructuralFeature feature, int accessIndex, int serverIndex)
  {
    int chunkSize = resolveChunkSize;
    if (chunkSize == CDORevision.UNCHUNKED)
    {
      // Can happen if CDOSession.setReferenceChunkSize() was called meanwhile
      chunkSize = Integer.MAX_VALUE;
    }

    return doResolveProxy(rev, feature, accessIndex, serverIndex, chunkSize);
  }

  private Object doResolveProxy(CDORevision rev, EStructuralFeature feature, int accessIndex, int serverIndex, int chunkSize)
  {
    // Get proxy values
    InternalCDORevision revision = (InternalCDORevision)rev;
    int fetchIndex = serverIndex;

    MoveableList<Object> list = revision.getListOrNull(feature);
    if (list == null)
    {
      return null;
    }

    int size = list.size();
    int fromIndex = accessIndex;
    int toIndex = accessIndex;
    boolean minReached = false;
    boolean maxReached = false;
    boolean alternation = false;
    for (int i = 0; i < chunkSize; i++)
    {
      if (alternation)
      {
        if (!maxReached && toIndex < size - 1 && list.get(toIndex + 1) instanceof CDOElementProxy)
        {
          ++toIndex;
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
        if (!minReached && fromIndex > 0 && list.get(fromIndex - 1) instanceof CDOElementProxy)
        {
          --fromIndex;
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
    return protocol.loadChunk(revision, feature, accessIndex, fetchIndex, fromIndex, toIndex);
  }
}
