/*
 * Copyright (c) 2008, 2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStoreChunkReader;
import org.eclipse.emf.cdo.spi.server.StoreChunkReader;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class HibernateStoreChunkReader extends StoreChunkReader implements IHibernateStoreChunkReader
{
  public HibernateStoreChunkReader(HibernateStoreAccessor accessor, CDORevision revision, EStructuralFeature feature)
  {
    super(accessor, revision, feature);
  }

  @Override
  public HibernateStoreAccessor getAccessor()
  {
    return (HibernateStoreAccessor)super.getAccessor();
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
