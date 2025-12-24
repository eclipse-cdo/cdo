/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.server.mem;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreFactory;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @author Simon McDuff
 */
public class MEMStoreFactory implements IStoreFactory
{
  public MEMStoreFactory()
  {
  }

  @Override
  public String getStoreType()
  {
    return MEMStore.TYPE;
  }

  @Override
  public IStore createStore(String repositoryName, Map<String, String> repositoryProperties, Element storeConfig)
  {
    return new MEMStore();
  }
}
