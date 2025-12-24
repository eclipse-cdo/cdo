/*
 * Copyright (c) 2011, 2012, 2017, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings
 *    Stefan Winkler - 249610: [DB] Support external references (Implementation)
 */
package org.eclipse.emf.cdo.server.mongodb;

import org.eclipse.emf.cdo.server.internal.mongodb.MongoDBStore;
import org.eclipse.emf.cdo.server.internal.mongodb.bundle.OM;

import org.eclipse.net4j.util.container.IManagedContainer;

import com.mongodb.MongoURI;

/**
 * Various static methods that may help in setting up and dealing with {@link IMongoDBStore MongoDB stores}.
 *
 * @author Eike Stepper
 */
@SuppressWarnings("deprecation")
public final class CDOMongoDBUtil
{
  private CDOMongoDBUtil()
  {
  }

  public static void prepareContainer(IManagedContainer container)
  {
    OM.BUNDLE.prepareContainer(container);
  }

  public static IMongoDBStore createStore(String uri, String dbName)
  {
    MongoURI mongoURI = new MongoURI(uri);
    MongoDBStore store = new MongoDBStore();
    store.setMongoURI(mongoURI);
    store.setDBName(dbName);
    return store;
  }
}
