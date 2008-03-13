/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.StoreUtil;
import org.eclipse.emf.cdo.server.IRepository.Props;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 * @author Martin Taal
 */
public class StoreRepositoryProvider
{
  private static StoreRepositoryProvider instance = new StoreRepositoryProvider();

  public static StoreRepositoryProvider getInstance()
  {
    return instance;
  }

  public static void setInstance(StoreRepositoryProvider instance)
  {
    StoreRepositoryProvider.instance = instance;
  }

  protected IStore createStore()
  {
    return StoreUtil.createMEMStore();
  }

  protected IRepository createRepository(String name)
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(Props.PROP_SUPPORTING_REVISION_DELTAS, "true");
    props.put(Props.PROP_CURRENT_LRU_CAPACITY, "10000");
    props.put(Props.PROP_REVISED_LRU_CAPACITY, "10000");
    return CDOServerUtil.createRepository(name, createStore(), props);
  }
}
