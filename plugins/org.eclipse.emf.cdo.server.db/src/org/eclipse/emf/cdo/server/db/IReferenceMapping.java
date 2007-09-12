/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;

import org.eclipse.net4j.db.IDBTable;

import java.util.List;

/**
 * @author Eike Stepper
 */
public interface IReferenceMapping
{
  public IValueMapping getValueMapping();

  public CDOFeature getFeature();

  public IDBTable getTable();

  public void writeReference(IDBStoreAccessor storeAccessor, CDORevisionImpl revision);

  public void readReference(IDBStoreAccessor storeAccessor, CDORevisionImpl revision, int referenceChunk);

  public void readChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks, String string);
}
