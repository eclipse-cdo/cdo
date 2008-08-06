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
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;

import java.util.HashMap;
import java.util.Map;

/**
 * @see http://bugs.eclipse.org/201266
 * @author Simon McDuff
 */
public class RevisionDeltaWithDeltaSupportTest extends RevisionDeltaTest
{
  public RevisionDeltaWithDeltaSupportTest()
  {
  }

  @Override
  protected Repository createRepository()
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(IRepository.Props.PROP_SUPPORTING_REVISION_DELTAS, "true");

    IStore store = createStore();
    Repository repository = new Repository()
    {
      @Override
      protected RevisionManager createRevisionManager()
      {
        return new TestRevisionManager(this);
      }
    };

    repository.setName(REPOSITORY_NAME);
    repository.setProperties(props);
    repository.setStore(store);
    return repository;
  }
}
