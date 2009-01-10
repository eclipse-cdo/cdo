/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.CDORevisionManager;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.internal.cdo.revision.CDOElementProxy;

import org.eclipse.net4j.util.collection.MoveableList;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOCollectionLoadingPolicyImpl implements CDOCollectionLoadingPolicy
{
  private int initialChunkSize;

  private int resolveChunkSize;

  public CDOCollectionLoadingPolicyImpl(int initialChunkSize, int resolveChunkSize)
  {
    this.resolveChunkSize = resolveChunkSize <= 0 ? CDORevision.UNCHUNKED : resolveChunkSize;
    this.initialChunkSize = initialChunkSize < 0 ? resolveChunkSize : initialChunkSize;
  }

  public int getInitialChunkSize()
  {
    return initialChunkSize;
  }

  public int getResolveChunkSize()
  {
    return resolveChunkSize;
  }

  public Object resolveProxy(CDORevisionManager revisionManager, CDORevision rev, CDOFeature feature, int accessIndex,
      int serverIndex)
  {
    // Get proxy values
    InternalCDORevision revision = (InternalCDORevision)rev;
    int fetchIndex = serverIndex;

    int chunkSize = resolveChunkSize;
    if (chunkSize == CDORevision.UNCHUNKED)
    {
      // Can happen if CDOSession.setReferenceChunkSize() was called meanwhile
      chunkSize = Integer.MAX_VALUE;
    }

    MoveableList<Object> list = revision.getList(feature);
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

    return revisionManager.loadChunkByRange(revision, feature, accessIndex, fetchIndex, fromIndex, toIndex);
  }
}
