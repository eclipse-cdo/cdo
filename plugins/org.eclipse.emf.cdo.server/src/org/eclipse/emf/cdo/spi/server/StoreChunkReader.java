/*
 * Copyright (c) 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreChunkReader;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class StoreChunkReader implements IStoreChunkReader
{
  private IStoreAccessor accessor;

  private CDORevision revision;

  private EStructuralFeature feature;

  private List<Chunk> chunks = new ArrayList<Chunk>(0);

  public StoreChunkReader(IStoreAccessor accessor, CDORevision revision, EStructuralFeature feature)
  {
    this.accessor = accessor;
    this.revision = revision;
    this.feature = feature;
  }

  @Override
  public IStoreAccessor getAccessor()
  {
    return accessor;
  }

  @Override
  public CDORevision getRevision()
  {
    return revision;
  }

  @Override
  public EStructuralFeature getFeature()
  {
    return feature;
  }

  public List<Chunk> getChunks()
  {
    return chunks;
  }

  @Override
  public void addSimpleChunk(int index)
  {
    chunks.add(new Chunk(index));
  }

  @Override
  public void addRangedChunk(int fromIndex, int toIndex)
  {
    chunks.add(new Chunk(fromIndex, toIndex - fromIndex));
  }
}
