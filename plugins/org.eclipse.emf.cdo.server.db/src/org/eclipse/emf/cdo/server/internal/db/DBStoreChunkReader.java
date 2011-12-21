/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - Bug 283998: [DB] Chunk reading for multiple chunks fails
 *    Victor Roldan Betancort - Bug 283998: [DB] Chunk reading for multiple chunks fails
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.db.IDBStoreChunkReader;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.BasicAbstractListTableMapping;
import org.eclipse.emf.cdo.spi.server.StoreChunkReader;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class DBStoreChunkReader extends StoreChunkReader implements IDBStoreChunkReader
{
  private IListMapping referenceMapping;

  private StringBuilder builder = new StringBuilder();

  public DBStoreChunkReader(DBStoreAccessor accessor, CDORevision revision, EStructuralFeature feature)
  {
    super(accessor, revision, feature);
    IMappingStrategy mappingStrategy = accessor.getStore().getMappingStrategy();
    IClassMapping mapping = mappingStrategy.getClassMapping(revision.getEClass());
    referenceMapping = mapping.getListMapping(feature);
  }

  @Override
  public DBStoreAccessor getAccessor()
  {
    return (DBStoreAccessor)super.getAccessor();
  }

  @Override
  public void addSimpleChunk(int index)
  {
    super.addSimpleChunk(index);
    prepareAddition();

    ((BasicAbstractListTableMapping)referenceMapping).addSimpleChunkWhere(getAccessor(), getRevision().getID(),
        builder, index);
  }

  @Override
  public void addRangedChunk(int fromIndex, int toIndex)
  {
    super.addRangedChunk(fromIndex, toIndex);
    prepareAddition();

    ((BasicAbstractListTableMapping)referenceMapping).addRangedChunkWhere(getAccessor(), getRevision().getID(),
        builder, fromIndex, toIndex);
  }

  public List<Chunk> executeRead()
  {
    List<Chunk> chunks = getChunks();
    if (chunks.size() > 1)
    {
      builder.insert(0, '(');
      builder.append(')');
    }

    referenceMapping.readChunks(this, chunks, builder.toString());
    return chunks;
  }

  private void prepareAddition()
  {
    // If not empty, a chunk has been already added, and the next condition needs to be OR-ed
    if (builder.length() > 0)
    {
      builder.append(" OR "); //$NON-NLS-1$
    }
  }
}
