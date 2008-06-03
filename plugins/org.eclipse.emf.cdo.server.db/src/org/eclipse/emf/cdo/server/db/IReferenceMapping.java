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
package org.eclipse.emf.cdo.server.db;

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

  public void writeReference(IDBStoreWriter storeWriter, CDORevision revision);

  public void readReference(IDBStoreReader storeReader, CDORevision revision, int referenceChunk);

  public void readChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks, String string);
}
