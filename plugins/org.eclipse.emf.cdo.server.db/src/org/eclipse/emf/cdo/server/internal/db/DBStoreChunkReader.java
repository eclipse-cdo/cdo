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
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.server.StoreChunkReader;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStoreChunkReader;
import org.eclipse.emf.cdo.server.db.IDBStoreReader;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.IReferenceMapping;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class DBStoreChunkReader extends StoreChunkReader implements IDBStoreChunkReader
{
  private IReferenceMapping referenceMapping;

  private StringBuilder builder = new StringBuilder();

  public DBStoreChunkReader(IDBStoreReader storeReader, CDORevision revision, CDOFeature feature)
  {
    super(storeReader, revision, feature);
    IMappingStrategy mappingStrategy = storeReader.getStore().getMappingStrategy();
    IClassMapping mapping = mappingStrategy.getClassMapping(revision.getCDOClass());
    referenceMapping = mapping.getReferenceMapping(feature);
  }

  @Override
  public DBStoreReader getStoreReader()
  {
    return (DBStoreReader)super.getStoreReader();
  }

  @Override
  public void addSimpleChunk(int index)
  {
    super.addSimpleChunk(index);
    builder.append(" AND ");
    builder.append(CDODBSchema.REFERENCES_IDX);
    builder.append("=");
    builder.append(index);
  }

  @Override
  public void addRangedChunk(int fromIndex, int toIndex)
  {
    super.addRangedChunk(fromIndex, toIndex);
    builder.append(" AND ");
    builder.append(CDODBSchema.REFERENCES_IDX);
    builder.append(" BETWEEN ");
    builder.append(fromIndex);
    builder.append(" AND ");
    builder.append(toIndex - 1);
  }

  public List<Chunk> executeRead()
  {
    List<Chunk> chunks = getChunks();
    referenceMapping.readChunks(this, chunks, builder.toString());
    return chunks;
  }
}
