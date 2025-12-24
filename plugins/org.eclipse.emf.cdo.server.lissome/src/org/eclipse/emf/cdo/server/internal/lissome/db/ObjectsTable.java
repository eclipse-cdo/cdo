/*
 * Copyright (c) 2012, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;

/**
 * @author Eike Stepper
 */
public class ObjectsTable extends Table
{
  protected IDBField branch;

  protected IDBField time;

  protected IDBField revised;

  protected IDBField version;

  protected IDBField cid;

  protected IDBField oid;

  protected IDBField container;

  protected IDBField name;

  protected IDBField pointer;

  public ObjectsTable(Index index)
  {
    super(index, "cdo_objects");

    oid = addCDOIDField("cdo_oid");

    if (isSupportingBranches())
    {
      branch = table.addField("cdo_branch", DBType.INTEGER);
    }

    if (isSupportingAudits())
    {
      time = table.addField("cdo_time", DBType.BIGINT);
      revised = table.addField("cdo_revised", DBType.BIGINT);
      version = table.addField("cdo_version", DBType.INTEGER);
    }

    cid = table.addField("cdo_cid", DBType.INTEGER);
    container = addCDOIDField("cdo_container");
    name = table.addField("cdo_name", DBType.VARCHAR, 255);
    pointer = table.addField("cdo_pointer", DBType.BIGINT);

    table.addIndex(IDBIndex.Type.PRIMARY_KEY, oid, time, branch);
    // TODO Additional indexes to speed up queries
  }

  protected boolean addCriterion(StringBuilder builder, boolean needAnd, boolean withBranch, boolean historical)
  {
    if (withBranch)
    {
      if (needAnd)
      {
        builder.append(" AND ");
      }

      builder.append(branch);
      builder.append("=?");
      needAnd = true;
    }

    if (isSupportingAudits())
    {
      if (needAnd)
      {
        builder.append(" AND ");
      }

      if (historical)
      {
        builder.append(time);
        builder.append("<=? AND (?<=");
        builder.append(revised);
        builder.append(" OR ");
        builder.append(revised);
        builder.append("=");
        builder.append(CDOBranchPoint.UNSPECIFIED_DATE);
        builder.append(")");
      }
      else
      {
        builder.append(revised);
        builder.append("=");
        builder.append(CDOBranchPoint.UNSPECIFIED_DATE);
      }

      needAnd = true;
    }

    return needAnd;
  }

  protected String sqlQueryResources(boolean historical, boolean exactMatch)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");
    builder.append(oid);
    builder.append(" FROM ");
    builder.append(this);
    builder.append(" WHERE ");
    builder.append(container);
    builder.append("=? AND ");
    builder.append(name);
    builder.append(exactMatch ? "=?" : " LIKE ?");

    addCriterion(builder, true, isSupportingBranches(), historical);

    builder.append(" AND ");
    builder.append(version);
    builder.append(">=");
    builder.append(CDOBranchVersion.FIRST_VERSION);
    return builder.toString();
  }

  protected StringBuilder sqlReadRevision()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");
    builder.append(pointer);

    if (isSupportingAudits())
    {
      builder.append(", ");
      builder.append(revised);
    }

    builder.append(" FROM ");
    builder.append(this);
    return builder;
  }

  protected String sqlReadRevision(boolean historical)
  {
    StringBuilder builder = sqlReadRevision();
    builder.append(" WHERE ");
    builder.append(oid);
    builder.append("=?");
    addCriterion(builder, true, isSupportingBranches(), historical);
    return builder.toString();
  }

  protected String sqlReadRevisionByVersion()
  {
    StringBuilder builder = sqlReadRevision();
    builder.append(" WHERE ");
    builder.append(oid);
    builder.append("=? AND ");
    builder.append(branch);
    builder.append("=? AND ");
    builder.append(version);
    builder.append("=?");
    return builder.toString();
  }

  protected String sqlHandleRevisions(boolean withClass, boolean withBranch, boolean withTime, boolean exactTime, boolean historical)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");
    builder.append(oid);
    builder.append(", ");
    builder.append(pointer);

    if (isSupportingAudits())
    {
      builder.append(", ");
      builder.append(revised);
    }

    builder.append(" FROM ");
    builder.append(this);

    int appended = 0;
    if (withClass)
    {
      builder.append(appended++ == 0 ? " WHERE " : " AND ");
      builder.append(cid);
      builder.append("=?");
    }

    if (withBranch)
    {
      builder.append(appended++ == 0 ? " WHERE " : " AND ");
      builder.append(branch);
      builder.append("=?");
    }

    if (withTime)
    {
      if (exactTime)
      {
        if (historical)
        {
          builder.append(appended++ == 0 ? " WHERE " : " AND ");
          builder.append(time);
          builder.append("=?");
        }
      }
      else
      {
        builder.append(appended++ == 0 ? " WHERE " : " AND ");
        addCriterion(builder, false, false, historical);
      }
    }

    return builder.toString();
  }

  protected String sqlAddRevision()
  {
    StringBuilder builder = new StringBuilder();
    StringBuilder params = new StringBuilder();

    builder.append("INSERT INTO ");
    builder.append(this);
    builder.append(" (");
    builder.append(oid);
    params.append("?");

    if (isSupportingBranches())
    {
      builder.append(", ");
      builder.append(branch);
      params.append(", ?");
    }

    if (isSupportingAudits())
    {
      builder.append(", ");
      builder.append(time);
      params.append(", ?");

      builder.append(", ");
      builder.append(revised);
      params.append(", ?");

      builder.append(", ");
      builder.append(version);
      params.append(", ?");
    }

    builder.append(", ");
    builder.append(cid);
    params.append(", ?");

    builder.append(", ");
    builder.append(container);
    params.append(", ?");

    builder.append(", ");
    builder.append(name);
    params.append(", ?");

    builder.append(", ");
    builder.append(pointer);
    params.append(", ?");

    builder.append(") VALUES (");
    builder.append(params);
    builder.append(")");
    return builder.toString();
  }

  protected String sqlReviseOldRevisions()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("UPDATE ");
    builder.append(this);
    builder.append(" SET ");
    builder.append(revised);
    builder.append("=?");
    builder.append(" WHERE ");
    builder.append(oid);
    builder.append("=?");

    if (isSupportingBranches())
    {
      builder.append(" AND ");
      builder.append(branch);
      builder.append("=?");
    }

    builder.append(" AND ");
    builder.append(version);
    builder.append("=?");
    return builder.toString();
  }
}
