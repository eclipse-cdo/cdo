/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
public class BranchesTable extends Table
{
  protected IDBField id;

  protected IDBField name;

  protected IDBField baseBranch;

  protected IDBField baseTime;

  public BranchesTable(Index index)
  {
    super(index, "cdo_branches");

    id = table.addField("i", DBType.INTEGER);
    name = table.addField("n", DBType.VARCHAR, 255);
    baseBranch = table.addField("b", DBType.INTEGER);
    baseTime = table.addField("t", DBType.BIGINT);

    table.addIndex(IDBIndex.Type.PRIMARY_KEY, id);
    // TODO Additional indexes to speed up queries
  }

  protected String sqlLoadSubBranches()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");
    builder.append(id);
    builder.append(", ");
    builder.append(name);
    builder.append(", ");
    builder.append(baseTime);
    builder.append(" FROM ");
    builder.append(this);
    builder.append(" WHERE ");
    builder.append(baseBranch);
    builder.append("=?");
    return builder.toString();
  }

  protected String sqlLoadBranches()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");
    builder.append(id);
    builder.append(", ");
    builder.append(name);
    builder.append(", ");
    builder.append(baseBranch);
    builder.append(", ");
    builder.append(baseTime);
    builder.append(" FROM ");
    builder.append(this);
    builder.append(" WHERE ");
    builder.append(baseBranch);
    builder.append(" BETWEEN ? AND ? ORDER BY ");
    builder.append(baseBranch);
    return builder.toString();
  }

  protected String sqlCreateBranch()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("INSERT INTO ");
    builder.append(this);
    builder.append(" (");
    builder.append(id);
    builder.append(", ");
    builder.append(name);
    builder.append(", ");
    builder.append(baseBranch);
    builder.append(", ");
    builder.append(baseTime);
    builder.append(") VALUES (?, ?, ?, ?)");
    return builder.toString();
  }
}
