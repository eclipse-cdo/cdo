/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - https://bugs.eclipse.org/210868
 **************************************************************************/
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDORevision;

import java.util.List;

/**
 * @author Eike Stepper
 */
public interface IStoreChunkReader
{
  public IStoreReader getStoreReader();

  public CDORevision getRevision();

  public CDOFeature getFeature();

  public void addSimpleChunk(int index);

  public void addRangedChunk(int fromIndex, int toIndex);

  public List<Chunk> executeRead();

  /**
   * @author Eike Stepper
   */
  public class Chunk
  {
    private int startIndex;

    private Object ids;

    public Chunk(int startIndex)
    {
      this.startIndex = startIndex;
    }

    public Chunk(int startIndex, int size)
    {
      this(startIndex);
      ids = new CDOID[size];
    }

    public int getStartIndex()
    {
      return startIndex;
    }

    public int size()
    {
      if (ids instanceof CDOID || ids == null)
      {
        return 1;
      }

      return ((CDOID[])ids).length;
    }

    public CDOID getID(int indexInChunk)
    {
      if (ids instanceof CDOID || ids == null)
      {
        if (indexInChunk == 0)
        {
          return (CDOID)ids;
        }

        throw new ArrayIndexOutOfBoundsException(indexInChunk);
      }

      return ((CDOID[])ids)[indexInChunk];
    }

    public void addID(int indexInChunk, CDOID id)
    {
      if (ids instanceof CDOID || ids == null)
      {
        if (indexInChunk == 0)
        {
          ids = id;
          return;
        }

        throw new ArrayIndexOutOfBoundsException(indexInChunk);
      }

      ((CDOID[])ids)[indexInChunk] = id;
    }
  }
}
