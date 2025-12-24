/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.net4j;

/**
 * A {@link RecoveringCDOSessionConfiguration session configuration} that recovers from network problems by failing over
 * to backup repositories as directed by a fail-over monitor.
 *
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface FailoverCDOSessionConfiguration extends RecoveringCDOSessionConfiguration
{
  public String getMonitorConnectorDescription();

  public String getRepositoryGroup();
}
