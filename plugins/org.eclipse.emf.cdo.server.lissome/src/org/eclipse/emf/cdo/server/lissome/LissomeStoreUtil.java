/*
 * Copyright (c) 2012, 2023 Eike Stepper (Loehne, Germany) and others.
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
package org.eclipse.emf.cdo.server.lissome;

import org.eclipse.emf.cdo.server.internal.lissome.LissomeStore;
import org.eclipse.emf.cdo.server.internal.lissome.bundle.OM;

import org.eclipse.net4j.util.container.IManagedContainer;

import java.io.File;

/**
 * Various static methods that may help in setting up and dealing with {@link ILissomeStore Lissome stores}.
 *
 * @author Eike Stepper
 */
public final class LissomeStoreUtil
{
  private LissomeStoreUtil()
  {
  }

  public static void prepareContainer(IManagedContainer container)
  {
    OM.BUNDLE.prepareContainer(container);
  }

  public static ILissomeStore createStore(File folder)
  {
    LissomeStore store = new LissomeStore();
    store.setFolder(folder);
    return store;
  }
}
