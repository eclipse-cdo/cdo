/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreReader;

import java.util.List;

/**
 * @author Simon McDuff
 */
public class MEMStoreChunkReader extends StoreChunkReader
{
  public MEMStoreChunkReader(IStoreReader storeReader, CDORevision revision, CDOFeature feature)
  {
    super(storeReader, revision, feature);
  }

  public List<Chunk> executeRead()
  {
    MEMStore store = getStoreReader().getStore();
    for (Chunk chunk : getChunks())
    {
      int startIndex = chunk.getStartIndex();
      int size = chunk.size();
      CDORevisionImpl revisionMEM = (CDORevisionImpl)store.getRevision(getRevision().getID());

      for (int i = 0; i < size; i++)
      {
        Object object = revisionMEM.get(getFeature(), startIndex + i);
        chunk.addID(i, (CDOID)object);
      }
    }

    return this.getChunks();
  }

  @Override
  public MEMStoreAccessor getStoreReader()
  {
    return (MEMStoreAccessor)super.getStoreReader();
  }
}
