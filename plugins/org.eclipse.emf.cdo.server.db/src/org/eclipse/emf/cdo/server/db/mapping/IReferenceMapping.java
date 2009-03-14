/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - https://bugs.eclipse.org/bugs/show_bug.cgi?id=259402
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IDBStoreChunkReader;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

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
   * Write a list of references completely
   * 
   * @since 2.0
   */
  public void writeReference(IDBStoreAccessor accessor, InternalCDORevision revision);

  /**
   * Write one element of the list of references
   * 
   * @since 2.0
   */
  public void writeReferenceEntry(IDBStoreAccessor accessor, CDOID id, int version, int idx, CDOID targetId);

  /**
   * Insert a single reference (entry) and move all subsequent entries of the list upwards
   * 
   * @since 2.0
   */
  public void insertReferenceEntry(IDBStoreAccessor accessor, CDOID id, int newVersion, int index, CDOID value);

  /**
   * Moves a single reference (entry) from <code>oldPosition</code> to <code>newPosition</code> and update the list
   * indexes of the entries in between.
   * 
   * @since 2.0
   */
  public void moveReferenceEntry(IDBStoreAccessor accessor, CDOID id, int newVersion, int oldPosition, int newPosition);

  /**
   * Remove a single reference (entry) and move all subsequent entries of the list downwards to fill the gap.
   * 
   * @since 2.0
   */
  public void removeReferenceEntry(IDBStoreAccessor accessor, CDOID id, int index, int newVersion);

  /**
   * Updates the value and version of a single reference (entry).
   * 
   * @since 2.0
   */
  public void updateReference(IDBStoreAccessor accessor, CDOID id, int newVersion, int index, CDOID value);

  /**
   * Updates the version of all entries of a reference (list) to <code>newVersion</code>.
   * 
   * @since 2.0
   */
  public void updateReferenceVersion(IDBStoreAccessor accessor, CDOID id, int newVersion);

  /**
   * Clears the list of references for the revision with ID <code>id</code>.
   * 
   * @since 2.0
   */
  public void deleteReference(IDBStoreAccessor accessor, CDOID id);

  /**
   * @since 2.0
   */
  public void readReference(IDBStoreAccessor accessor, InternalCDORevision revision, int referenceChunk);

  public void readChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks, String string);
}
