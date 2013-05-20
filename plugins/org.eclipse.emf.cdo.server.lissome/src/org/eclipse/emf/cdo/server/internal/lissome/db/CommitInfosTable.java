/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.lissome.db;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;

/**
 * @author Eike Stepper
 */
public class CommitInfosTable extends Table
{
  protected IDBField time;

  protected IDBField branch;

  protected IDBField pointer;

  public CommitInfosTable(Index index)
  {
    super(index, "cdo_commits");

    time = table.addField("cdo_time", DBType.BIGINT);
    if (isSupportingBranches())
    {
      branch = table.addField("cdo_branch", DBType.INTEGER);
    }

    pointer = table.addField("cdo_pointer", DBType.BIGINT);

    table.addIndex(IDBIndex.Type.PRIMARY_KEY, time);
    if (isSupportingBranches())
    {
      table.addIndex(IDBIndex.Type.NON_UNIQUE, branch);
    }
  }

  protected String sqlAddCommitInfo()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("INSERT INTO ");
    builder.append(this);
    builder.append(" (");
    builder.append(time);
    if (isSupportingBranches())
    {
      builder.append(", ");
      builder.append(branch);
    }

    builder.append(", ");
    builder.append(pointer);
    builder.append(") VALUES (?, ?");
    if (isSupportingBranches())
    {
      builder.append(", ?");
    }

    builder.append(")");
    return builder.toString();
  }

  protected String sqlLoadCommitInfos(boolean withBranch, boolean withStartTime, boolean withEndTime)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");
    builder.append(pointer);
    builder.append(" FROM ");
    builder.append(this);
    boolean where = false;

    if (withBranch)
    {
      builder.append(where ? " AND " : " WHERE ");
      builder.append(branch);
      builder.append("=?");
      where = true;
    }

    if (withStartTime)
    {
      builder.append(where ? " AND " : " WHERE ");
      builder.append(time);
      builder.append(">=?");
      where = true;
    }

    if (withEndTime)
    {
      builder.append(where ? " AND " : " WHERE ");
      builder.append(time);
      builder.append("<=?");
      where = true;
    }

    builder.append(" ORDER BY ");
    builder.append(time);
    return builder.toString();
  }
}
