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

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.internal.db.DBStoreManager;

import javax.sql.DataSource;

import java.sql.Connection;

/**
 * @author Eike Stepper
 */
public class CDODBStoreManager extends DBStoreManager<CDODBTransaction>
{
  public CDODBStoreManager(IDBAdapter dbAdapter, DataSource dataSource)
  {
    super(CDODBSchema.INSTANCE, dbAdapter, dataSource);
  }

  @Override
  protected CDODBTransaction createTransaction(Connection connection)
  {
    return new CDODBTransaction(this, connection);
  }
}
