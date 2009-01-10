/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;

import org.eclipse.net4j.db.ddl.IDBTable;

import java.util.List;

/**
 * @author Eike Stepper
 */
public interface IReferenceMapping extends IFeatureMapping
{
  public IDBTable getTable();

  /**
   * @since 2.0
   */
  public boolean isWithFeature();

  /**
   * @since 2.0
   */
  public void writeReference(IDBStoreAccessor accessor, CDORevision revision);

  /**
   * @since 2.0
   */
  public void readReference(IDBStoreAccessor accessor, CDORevision revision, int referenceChunk);

  public void readChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks, String string);

  /**
   * @since 2.0
   */
  public void deleteReference(IDBStoreAccessor accessor, CDOID id);
}
