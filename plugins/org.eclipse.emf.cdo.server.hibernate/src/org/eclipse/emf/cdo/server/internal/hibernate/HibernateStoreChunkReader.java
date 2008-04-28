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
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.internal.server.StoreChunkReader;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStoreChunkReader;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStoreReader;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class HibernateStoreChunkReader extends StoreChunkReader implements IHibernateStoreChunkReader
{
  public HibernateStoreChunkReader(IHibernateStoreReader storeReader, CDORevision revision, CDOFeature feature)
  {
    super(storeReader, revision, feature);
  }

  @Override
  public HibernateStoreReader getStoreReader()
  {
    return (HibernateStoreReader)super.getStoreReader();
  }

  @Override
  public void addSimpleChunk(int index)
  {
    // super.addSimpleChunk(index);
    throw new UnsupportedOperationException(); // TODO Implement me
  }

  @Override
  public void addRangedChunk(int fromIndex, int toIndex)
  {
    // super.addRangedChunk(fromIndex, toIndex);
    throw new UnsupportedOperationException(); // TODO Implement me
  }

  public List<Chunk> executeRead()
  {
    throw new UnsupportedOperationException(); // TODO Implement me
  }
}
