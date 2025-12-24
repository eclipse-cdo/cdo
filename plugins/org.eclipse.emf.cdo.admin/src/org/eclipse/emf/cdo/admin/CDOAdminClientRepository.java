/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.admin;

import org.eclipse.emf.cdo.common.admin.CDOAdminRepository;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;

/**
 * A client-side {@link CDOAdminRepository administrative interface} to a remote repository.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOAdminClientRepository extends CDOAdminRepository
{
  @Override
  public CDOAdminClient getAdmin();

  public CDONet4jSession openSession();

  public CDONet4jSession openSession(SessionConfigurator configurator);

  /**
   * Provides access to the {@link CDONet4jSessionConfiguration session configuration}
   * used in calls to {@link CDOAdminClientRepository#openSession()}.
   *
   * @author Eike Stepper
   */
  public interface SessionConfigurator
  {
    public void prepare(CDONet4jSessionConfiguration configuration);
  }
}
