/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings
 *    Stefan Winkler - 249610: [DB] Support external references (Implementation)
 */
package org.eclipse.emf.cdo.server.lissome;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStore.CanHandleClientAssignedIDs;
import org.eclipse.emf.cdo.server.ITransaction;

/**
 * The main entry point to the API of CDO's proprietary Lissome store.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ILissomeStore extends IStore, CanHandleClientAssignedIDs
{
  @Override
  public ILissomeStoreAccessor getReader(ISession session);

  @Override
  public ILissomeStoreAccessor getWriter(ITransaction transaction);

  /**
   * Contains symbolic constants that specifiy valid keys of {@link IRepository#getProperties() Lissome store properties}.
   *
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface Props
  {
  }
}
