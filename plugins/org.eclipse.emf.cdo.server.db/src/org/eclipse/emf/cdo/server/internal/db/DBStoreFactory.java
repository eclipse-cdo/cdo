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

import org.eclipse.emf.cdo.internal.server.StoreFactory;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * @author Eike Stepper
 */
public class DBStoreFactory extends StoreFactory<DBStore>
{
  public static final String TYPE = "db";

  public DBStoreFactory()
  {
    super(TYPE);
  }

  public DBStore create(String description) throws ProductCreationException
  {
    return null;
  }

  public static DBStore get(IManagedContainer container, String description)
  {
    return (DBStore)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}
