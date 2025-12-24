/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 *    Caspar De Groot - maintenance
 */
package org.eclipse.emf.cdo.server.internal.lissome;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreFactory;
import org.eclipse.emf.cdo.spi.server.RepositoryConfigurator;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class LissomeStoreFactory implements IStoreFactory
{
  public LissomeStoreFactory()
  {
  }

  @Override
  public String getStoreType()
  {
    return LissomeStore.TYPE;
  }

  @Override
  public IStore createStore(String repositoryName, Map<String, String> repositoryProperties, Element storeConfig)
  {
    LissomeStore store = new LissomeStore();

    Map<String, String> storeProperties = RepositoryConfigurator.getProperties(storeConfig, 1);
    store.setProperties(storeProperties);

    return store;
  }
}
