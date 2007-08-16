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

import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.db.IDBSchema;
import org.eclipse.net4j.db.IDBTable;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public interface IMappingStrategy
{
  public String getType();

  public Properties getProperties();

  public void setProperties(Properties properties);

  public IDBSchema createSchema(IRepository repository);

  public IDBTable[] map(IDBSchema schema, CDOPackage cdoPackage);
}
