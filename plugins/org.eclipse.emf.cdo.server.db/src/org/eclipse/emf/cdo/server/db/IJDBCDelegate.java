/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 *    Eike Stepper - maintenance
 *    Stefan Winkler - https://bugs.eclipse.org/bugs/show_bug.cgi?id=259402
 */
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.db.mapping.IAttributeMapping;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IReferenceMapping;
import org.eclipse.emf.cdo.server.internal.db.jdbc.AbstractJDBCDelegate;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * Interface for all JDBC related activities regarding revisions.
 * 
 * @author Stefan Winkler
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients. Please extend the abstract class
 *              {@link AbstractJDBCDelegate} instead.
 */
public interface IJDBCDelegate extends IDBConnectionProvider
{
  // --------------------------------------------------------------
  // General methods
  // --------------------------------------------------------------

  /**
   * Get the one omnipresent statement object of this JDBC delegate
   */
  public Statement getStatement();

  /**
   * Get a prepared statement. The caller is responsible of closing it.
   */
  public PreparedStatement getPreparedStatement(String sql);

  public void setStoreAccessor(IDBStoreAccessor storeAccessor);

  /**
   * Do any outstanding writes (e.g. execute batches). Called any number of times - but at least once immediately before
   * commit().
   * 
   * @see IStoreAccessor#write(org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext, OMMonitor)
   */
  public void flush(OMMonitor monitor);

  /**
   * Do a commit on the JDBC connection.
   */
  public void commit(OMMonitor monitor);

  /**
   * Do a rollback on the JDBC connection.
   */
  public void rollback();

  // --------------------------------------------------------------
  // Writing Revisions / Attributes
  // --------------------------------------------------------------

  /**
   * Insert an attribute row.
   */
  public void insertAttributes(InternalCDORevision revision, IClassMapping classMapping);

  /**
   * Update an attribute row.
   */
  public void updateAttributes(InternalCDORevision revision, IClassMapping classMapping);

  /**
   * Update an attribute row.
   */
  public void updateAttributes(CDOID id, int newVersion, long created,
      List<Pair<IAttributeMapping, Object>> attributeChanges, IClassMapping classMapping);

  /**
   * Update an attribute row (including containment and resource attributes).
   */
  public void updateAttributes(CDOID id, int newVersion, long created, CDOID newContainerId,
      int newContainingFeatureId, CDOID newResourceId, List<Pair<IAttributeMapping, Object>> attributeChanges,
      IClassMapping classMapping);

  /**
   * Set the revised date of a specific revision's previous version.
   */
  public void updateRevisedForReplace(InternalCDORevision revision, IClassMapping classMapping);

  /**
   * Set the revised date of all unrevised rows of cdoid
   */
  public void updateRevisedForDetach(CDOID id, long revised, IClassMapping classMapping);

  /**
   * Remove an attribute row.
   */
  public void deleteAttributes(CDOID id, IClassMapping classMapping);

  // --------------------------------------------------------------
  // Writing References
  // --------------------------------------------------------------

  /**
   * Insert a reference row.
   */
  public void insertReference(CDOID id, int version, int index, CDOID targetId, IReferenceMapping referenceMapping);

  /**
   * Insert a reference row shifting all subsequent indices one position up.
   */
  public void insertReferenceRow(CDOID id, int newVersion, int index, CDOID value, IReferenceMapping referenceMapping);

  /**
   * Move one reference row shifting all subsequent indices in between accordingly.
   */
  public void moveReferenceRow(CDOID id, int newVersion, int oldPosition, int newPosition,
      IReferenceMapping referenceMapping);

  /**
   * Remove a reference row shifting all subsequent indices one position down.
   */
  public void removeReferenceRow(CDOID id, int index, int newVersion, IReferenceMapping referenceMapping);

  /**
   * Update the value of a reference row.
   */
  public void updateReference(CDOID id, int newVersion, int index, CDOID value, IReferenceMapping referenceMapping);

  /**
   * Delete all reference rows of a cdoid.
   */
  public void deleteReferences(CDOID id, IReferenceMapping referenceMapping);

  /**
   * Update the version number of all references of a CDOID to newVersion.
   */
  public void updateReferenceVersion(CDOID id, int newVersion, IReferenceMapping referenceMapping);

  // --------------------------------------------------------------
  // Reading
  // --------------------------------------------------------------

  /**
   * Select a revision's attributes
   * 
   * @return <code>true</code> if the revision attributes have been successfully loaded.<br>
   *         <code>false</code> if the revision does not exist in the database.
   */
  public boolean selectRevisionAttributes(InternalCDORevision revision, IClassMapping classMapping, String where);

  /**
   * Select a revision's references (or a part thereof)
   */
  public void selectRevisionReferences(InternalCDORevision revision, IReferenceMapping referenceMapping,
      int referenceChunk);

  /**
   * Select a revision's reference's chunks
   */
  public void selectRevisionReferenceChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks,
      IReferenceMapping referenceMapping, String where);
}
