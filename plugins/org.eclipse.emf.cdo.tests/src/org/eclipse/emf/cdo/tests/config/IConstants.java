/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.config;

import org.eclipse.emf.cdo.tests.config.impl.Config;
import org.eclipse.emf.cdo.tests.config.impl.ContainerConfig;
import org.eclipse.emf.cdo.tests.config.impl.ModelConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;

/**
 * @author Eike Stepper
 */
public interface IConstants
{
  public static final Config[][] CONFIGS = { //
  ContainerConfig.CONFIGS, //
      RepositoryConfig.CONFIGS, //
      SessionConfig.CONFIGS, //
      ModelConfig.CONFIGS };

  public static final ContainerConfig COMBINED = ContainerConfig.Combined.INSTANCE;

  public static final ContainerConfig SEPARATED = ContainerConfig.Separated.INSTANCE;

  public static final RepositoryConfig MEM = RepositoryConfig.MEM.INSTANCE;

  public static final RepositoryConfig DB_HSQL_HORIZONTAL = RepositoryConfig.DB.Hsqldb.Stmt.INSTANCE;

  public static final RepositoryConfig DB_HSQL_HORIZONTAL_NONAUDIT = RepositoryConfig.DB.Hsqldb.StmtNonAudit.INSTANCE;

  public static final RepositoryConfig DB_HSQL_HORIZONTAL_PREPSTMT = RepositoryConfig.DB.Hsqldb.PrepStmt.INSTANCE;

  public static final RepositoryConfig DB_HSQL_HORIZONTAL_PREPSTMT_NONAUDIT = RepositoryConfig.DB.Hsqldb.PrepStmtNonAudit.INSTANCE;

  public static final RepositoryConfig DB_DERBY_HORIZONTAL = RepositoryConfig.DB.Derby.Stmt.INSTANCE;

  public static final RepositoryConfig DB_DERBY_HORIZONTAL_PREPSTMT = RepositoryConfig.DB.Derby.PrepStmt.INSTANCE;

  public static final RepositoryConfig DB_MYSQL_HORIZONTAL = RepositoryConfig.DB.Mysql.Stmt.INSTANCE;

  public static final RepositoryConfig DB_MYSQL_HORIZONTAL_PREPSTMT = RepositoryConfig.DB.Mysql.PrepStmt.INSTANCE;

  public static final SessionConfig JVM = SessionConfig.JVM.INSTANCE;

  public static final SessionConfig TCP = SessionConfig.TCP.INSTANCE;

  public static final ModelConfig NATIVE = ModelConfig.Native.INSTANCE;
}
