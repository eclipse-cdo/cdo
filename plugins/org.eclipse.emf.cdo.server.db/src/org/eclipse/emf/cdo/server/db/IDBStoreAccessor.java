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
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.IStoreWriter;

import java.sql.Connection;
import java.sql.Statement;

/**
 * @author Eike Stepper
 */
public interface IDBStoreAccessor extends IStoreReader, IStoreWriter
{
  public Connection getConnection();

  public Statement getStatement();

  public CDOClassRef readClassRef(int classID);
}
