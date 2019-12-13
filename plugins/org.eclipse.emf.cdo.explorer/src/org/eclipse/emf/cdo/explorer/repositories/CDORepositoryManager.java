/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.repositories;

import org.eclipse.emf.cdo.explorer.CDOExplorerManager;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.security.IPasswordCredentials;

import java.util.Properties;

/**
 * Manages a set of {@link CDORepository repositories}.
 *
 * @author Eike Stepper
 * @since 4.4
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORepositoryManager extends CDOExplorerManager<CDORepository>
{
  public CDORepository getRepository(String id);

  public CDORepository getRepository(CDOSession session);

  /**
   * @since 4.5
   */
  public CDORepository getRepositoryByLabel(String label);

  public CDORepository[] getRepositories();

  public CDORepository addRepository(Properties properties);

  /**
   * @since 4.5
   */
  public CDORepository addRepository(Properties properties, IPasswordCredentials credentials);

  /**
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface RepositoryConnectionEvent extends IEvent
  {
    @Override
    public CDORepositoryManager getSource();

    public CDORepository getRepository();

    public boolean isConnected();
  }
}
