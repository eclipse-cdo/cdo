/*
 * Copyright (c) 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class ServerDebugUtil
{
  private static final Map<IRepository, IStoreAccessor> accessors = new HashMap<IRepository, IStoreAccessor>();

  private ServerDebugUtil()
  {
  }

  public static void addAccessor(IStoreAccessor accessor)
  {
    accessors.put(accessor.getStore().getRepository(), accessor);
  }

  public static void removeAccessor(IStoreAccessor accessor)
  {
    removeAccessor(accessor.getStore().getRepository());
  }

  public static void removeAccessor(IRepository repository)
  {
    accessors.remove(repository);
  }

  public static IStoreAccessor getAccessor(IRepository repository)
  {
    return accessors.get(repository);
  }
}
