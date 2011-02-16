/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.mongodb;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreFactory;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.w3c.dom.Element;

/**
 * @author Eike Stepper
 */
public class MongoDBStoreFactory implements IStoreFactory
{
  public static final String TYPE = "mongodb";

  public MongoDBStoreFactory()
  {
  }

  public String getStoreType()
  {
    return TYPE;
  }

  public IStore createStore(InternalRepository repository, Element storeConfig)
  {
    // TODO: implement MongoDBStoreFactory.createStore(repository, storeConfig)
    throw new UnsupportedOperationException();
  }
}
