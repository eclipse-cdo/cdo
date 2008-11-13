/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.internal.db.jdbc.AbstractJDBCDelegate;

import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.sql.Connection;
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
public interface IJDBCDelegate
{
  /**
   * Insert a reference row. Note: this is likely to be replaced by an implementation that supports storing multiple
   * references in one batch.
   */
  public void insertReference(CDORevision sourceRevision, int index, CDOID targetId, IReferenceMapping referenceMapping);

  /**
   * Insert an attribute row.
   */
  public void insertAttributes(CDORevision revision, IClassMapping classMapping);

  /**
   * Set the revised date of a specific revision's previous version.
   */
  public void updateRevised(CDORevision revision, IClassMapping classMapping);

  /**
   * Set the revised date of all unrevised rows of cdoid
   */
  public void updateRevised(CDOID cdoid, long revised, IClassMapping classMapping);

  /**
   * Select a revision's attributes
   */
  public void selectRevisionAttributes(CDORevision revision, IClassMapping classMapping, String where);

  /**
   * Select a revision's references (or a part thereof)
   */
  public void selectRevisionReferences(CDORevision revision, IReferenceMapping referenceMapping, int referenceChunk);

  /**
   * Select a revision's reference's chunks
   */
  public void selectRevisionReferenceChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks,
      IReferenceMapping referenceMapping, String where);

  /**
   * Get the connection object of this JDBC delegate
   */
  public Connection getConnection();

  /**
   * Get the one omnipresent statement object of this JDBC delegate
   */
  public Statement getStatement();

  /**
   * Do a commit on the JDBC connection.
   */
  public void commit(OMMonitor monitor);

  /**
   * Do a rollback on the JDBC connection.
   */
  public void rollback();

  /**
   * Get a prepared statement. The caller is responsible of closing it.
   */
  public PreparedStatement getPreparedStatement(String sql);

  /**
   * Initialize the connection. This must be called once and only once immediately after creating.
   * 
   * @param connectionProvider
   *          a provider for the DB connection
   * @param readOnly
   *          if this is true, all accessors of this instance must not call directly or indirectly any writing
   *          operations. Setting readOnly to true leads to the connection's autoCommit property set to true.
   */
  public void initConnection(IDBConnectionProvider connectionProvider, boolean readOnly);

  /**
   * Release the DB resources. This has to be called when the delegate is no longer to be used.
   */
  public void release();
}
