/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.mem.MEMStoreUtil;

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
    return MEMStoreUtil.createMEMStore();
  }

  protected IRepository createRepository(String name, Map<String, String> testProperties)
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(Props.CURRENT_LRU_CAPACITY, "10000");
    props.put(Props.REVISED_LRU_CAPACITY, "10000");

    props.putAll(testProperties);

    return CDOServerUtil.createRepository(name, createStore(), props);
  }
}
