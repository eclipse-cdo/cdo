/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.tests.RevisionDeltaWithoutDeltaSupportTest;
import org.eclipse.emf.cdo.tests.StoreRepositoryProvider;

import java.util.Map;

/**
 * THIS TEST DOES NOT WORK, HIBERNATE DOES NOT SUPPORT REVISIONDELTAS
 * 
 * @author Martin Taal
 */
public class HbRevisionDeltaTest extends RevisionDeltaWithoutDeltaSupportTest
{
  public HbRevisionDeltaTest()
  {
    StoreRepositoryProvider.setInstance(LocalHbStoreRepositoryProvider.getInstance());
  }

  @Override
  protected Repository createRepository()
  {
    LocalHbStoreRepositoryProvider provider = new LocalHbStoreRepositoryProvider();
    return (Repository)provider.createRepository(REPOSITORY_NAME, getTestProperties());
  }

  private class LocalHbStoreRepositoryProvider extends HbStoreRepositoryProvider
  {
    @Override
    protected IRepository createRepository(String name, IStore store, Map<String, String> props)
    {
      Repository repository = new Repository()
      {
        @Override
        protected RevisionManager createRevisionManager()
        {
          return new TestRevisionManager(this);
        }
      };

      repository.setName(name);
      repository.setProperties(props);
      repository.setStore(store);
      return repository;
    }
  }
}
