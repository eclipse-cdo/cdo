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
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.IStoreReader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class StoreChunkReader implements IStoreChunkReader
{
  private IStoreReader storeReader;

  private CDORevision revision;

  private CDOFeature feature;

  private List<Chunk> chunks = new ArrayList<Chunk>(0);

  public StoreChunkReader(IStoreReader storeReader, CDORevision revision, CDOFeature feature)
  {
    this.storeReader = storeReader;
    this.revision = revision;
    this.feature = feature;
  }

  public IStoreReader getStoreReader()
  {
    return storeReader;
  }

  public CDORevision getRevision()
  {
    return revision;
  }

  public CDOFeature getFeature()
  {
    return feature;
  }

  public List<Chunk> getChunks()
  {
    return chunks;
  }

  public void addSimpleChunk(int index)
  {
    chunks.add(new Chunk(index));
  }

  public void addRangedChunk(int fromIndex, int toIndex)
  {
    chunks.add(new Chunk(fromIndex, toIndex - fromIndex));
  }
}
