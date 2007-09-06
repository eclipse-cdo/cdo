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
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.server.StoreChunkReader;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IDBStoreChunkReader;
import org.eclipse.emf.cdo.server.db.IMapping;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.IReferenceMapping;

import org.eclipse.net4j.util.ImplementationError;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class DBStoreChunkReader extends StoreChunkReader implements IDBStoreChunkReader
{
  private IReferenceMapping referenceMapping;

  private StringBuilder builder = new StringBuilder();

  public DBStoreChunkReader(IDBStoreAccessor storeAccessor, CDORevision revision, CDOFeature feature)
  {
    super(storeAccessor, revision, feature);
    IMappingStrategy mappingStrategy = storeAccessor.getStore().getMappingStrategy();
    CDOClass cdoClass = revision.getCDOClass();
    IMapping mapping = mappingStrategy.getMapping(cdoClass);
    if (mapping instanceof ValueMapping)
    {
      referenceMapping = ((ValueMapping)mapping).getReferenceMapping(feature);
    }
    else
    {
      throw new ImplementationError();
    }
  }

  @Override
  public IDBStoreAccessor getStoreAccessor()
  {
    return (IDBStoreAccessor)super.getStoreAccessor();
  }

  @Override
  public void addSimpleChunk(int index)
  {
    super.addSimpleChunk(index);
    builder.append(" AND ");
    builder.append(ReferenceMapping.FIELD_NAME_IDX);
    builder.append("=");
    builder.append(index);
  }

  @Override
  public void addRangedChunk(int fromIndex, int toIndex)
  {
    super.addRangedChunk(fromIndex, toIndex);
    builder.append(" AND ");
    builder.append(ReferenceMapping.FIELD_NAME_IDX);
    builder.append(" BETWEEN ");
    builder.append(fromIndex);
    builder.append(" AND ");
    builder.append(toIndex);
  }

  public List<Chunk> executeRead()
  {
    List<Chunk> chunks = getChunks();
    referenceMapping.readChunks(this, chunks, builder.toString());
    return chunks;
  }
}
