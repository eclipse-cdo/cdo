/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Ibrahim Sallam - code refactoring for CDO 3.0
 */
package org.eclipse.emf.cdo.server.internal.objectivity;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreFactory;

import org.w3c.dom.Element;

import java.util.Map;

public class ObjectivityStoreFactory implements IStoreFactory
{

  public ObjectivityStoreFactory()
  {
  }

  public IStore createStore(String repositoryName, Map<String, String> repositoryProperties, Element storeConfig)
  {
    // System.out.println(">>> OSF.createStore()");
    // TODO - we might want to initialize Objy with the
    // FD name here!!!
    ObjectivityStoreConfig objyStoreConfig = new ObjectivityStoreConfig(storeConfig);

    // open the connection to Objy...
    // if (Connection.current() == null)
    // {
    // try
    // {
    // // Connection.setUserClassLoader(ObjectivityStoreFactory.class.getClassLoader());
    // Connection.open(objyStoreConfig.getFdName(), oo.openReadWrite);
    // }
    // catch (DatabaseOpenException e)
    // {
    // e.printStackTrace();
    // }
    // catch (DatabaseNotFoundException e)
    // {
    // e.printStackTrace();
    // }
    // }

    ObjectivityStore store = new ObjectivityStore(objyStoreConfig);
    return store;
  }

  public String getStoreType()
  {
    return ObjectivityStore.TYPE;
  }

}
