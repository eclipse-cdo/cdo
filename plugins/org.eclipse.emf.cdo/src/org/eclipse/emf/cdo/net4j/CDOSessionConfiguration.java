/***************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.net4j;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface CDOSessionConfiguration extends org.eclipse.emf.cdo.session.CDOSessionConfiguration
{
  public IConnector getConnector();

  public void setConnector(IConnector connector);

  public IFailOverStrategy getFailOverStrategy();

  /**
   * The fail-over strategy must be set <b>before</b> the session is opened and can not be changed thereafter.
   */
  public void setFailOverStrategy(IFailOverStrategy failOverStrategy);

  public org.eclipse.emf.cdo.net4j.CDOSession openSession();
}
