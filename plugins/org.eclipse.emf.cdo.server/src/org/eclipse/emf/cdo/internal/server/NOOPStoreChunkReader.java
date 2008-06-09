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

import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreReader;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class NOOPStoreChunkReader extends StoreChunkReader
{
  public NOOPStoreChunkReader(IStoreReader storeReader, CDORevision revision, CDOFeature feature)
  {
    super(storeReader, revision, feature);
  }

  @Override
  public void addSimpleChunk(int index)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addRangedChunk(int fromIndex, int toIndex)
  {
    throw new UnsupportedOperationException();
  }

  public List<Chunk> executeRead()
  {
    throw new UnsupportedOperationException();
  }
}
