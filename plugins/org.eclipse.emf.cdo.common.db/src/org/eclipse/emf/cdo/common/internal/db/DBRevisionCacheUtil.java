/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.common.internal.db;

import org.eclipse.emf.cdo.common.model.CDOModelConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.util.CheckUtil;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;

/**
 * @author Andre Dietisheim
 */
public class DBRevisionCacheUtil
{
  public static void mandatoryInsertUpdate(PreparedStatement preparedStatement) throws SQLException
  {
    insertUpdate(preparedStatement);
    if (preparedStatement.getUpdateCount() == 0)
    {
      rollback(preparedStatement.getConnection());
      throw new SQLException(MessageFormat.format("No row inserted by statement \"{0}\"", preparedStatement));
    }
  }

  public static void insertUpdate(PreparedStatement preparedStatement) throws SQLException
  {
    if (preparedStatement.execute())
    {
      rollback(preparedStatement.getConnection());
      throw new SQLException("No result set expected");
    }

    commit(preparedStatement.getConnection());
  }

  public static void rollback(Connection connection) throws SQLException
  {
    CheckUtil.checkArg(connection, "connection");
    connection.rollback();
  }

  public static void commit(Connection connection) throws SQLException
  {
    CheckUtil.checkArg(connection, "connection");
    connection.commit();
  }

  /**
   * Asserts the given {@link CDORevision} is <tt>null</tt>.
   * 
   * @param cdoRevision
   *          the cdo revision
   * @param message
   *          the message to use when throwing the {@link DBException}
   * @throws DBException
   *           if the given CDORevision's not <tt>null</tt>
   */
  public static void assertIsNull(CDORevision cdoRevision, String message) throws SQLException
  {
    if (cdoRevision != null)
    {
      throw new SQLException(message);
    }
  }

  /**
   * Gets the name of a revision of a CDOResourceNode.
   * 
   * @param revision
   *          the revision
   * @return the resource node name
   * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=279817
   */
  // TODO: this should be refactored and put in a place, that's more generic
  // than this class. The same snippet's used in LRURevisionCache and
  // MemRevisionCache
  public static String getResourceNodeName(CDORevision revision)
  {
    CheckUtil.checkArg(revision.isResourceNode(), "The revision is not a resource node!");
    EStructuralFeature feature = revision.getEClass().getEStructuralFeature(
        CDOModelConstants.RESOURCE_NODE_NAME_ATTRIBUTE);
    return (String)((InternalCDORevision)revision).getValue(feature);
  }
}
