/*
 * Copyright (c) 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.server.IRepositorySynchronizer;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;

import org.eclipse.net4j.util.lifecycle.ILifecycle;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalRepositorySynchronizer extends IRepositorySynchronizer, ILifecycle
{
  @Override
  public InternalSynchronizableRepository getLocalRepository();

  public void setLocalRepository(InternalSynchronizableRepository localRepository);

  public void setRemoteSessionConfigurationFactory(CDOSessionConfigurationFactory remoteSessionConfigurationFactory);

  @Override
  public InternalCDOSession getRemoteSession();
}
