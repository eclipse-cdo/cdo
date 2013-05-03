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
package org.eclipse.emf.cdo.server.objectivity;

import org.eclipse.emf.cdo.server.internal.objectivity.ObjectivityStore;

/**
 * Provides a static factory method that creates {@link IObjectivityStore} instances.
 *
 * @author Simon McDuff
 */
public final class ObjyStoreUtil
{
  private ObjyStoreUtil()
  {
  }

  // public static IObjectivityStore createStore(IObjectivityStoreConfig storeConfig, boolean reset) {
  // // The store will open the connection to Objy...
  // return new ObjectivityStore(storeConfig, reset);
  // }

  public static IObjectivityStore createStore(IObjectivityStoreConfig storeConfig)
  {
    // The store will open the connection to Objy...
    return new ObjectivityStore(storeConfig);
  }
}
