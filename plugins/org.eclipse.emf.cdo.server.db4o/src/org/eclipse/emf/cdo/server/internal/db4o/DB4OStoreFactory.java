/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db4o;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreFactory;
import org.eclipse.emf.cdo.server.db4o.IDB4OStore;
import org.eclipse.emf.cdo.server.internal.db4o.bundle.OM;
import org.eclipse.emf.cdo.spi.server.RepositoryConfigurator;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @author Victor Roldan Betancort
 */
public class DB4OStoreFactory implements IStoreFactory
{
  private static final String PROPERTY_PORT = "port";

  private static final String PROPERTY_PATH = "path";

  public DB4OStoreFactory()
  {
  }

  public String getStoreType()
  {
    return IDB4OStore.TYPE;
  }

  public IStore createStore(String repositoryName, Map<String, String> repositoryProperties, Element storeConfig)
  {
    try
    {
      Map<String, String> properties = RepositoryConfigurator.getProperties(storeConfig, 1);
      String dataFilePath = properties.get(PROPERTY_PATH);
      String portString = properties.get(PROPERTY_PORT);
      int port = portString != null ? Integer.parseInt(portString) : 1677;
      return new DB4OStore(dataFilePath, port);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }

    return null;
  }
}
