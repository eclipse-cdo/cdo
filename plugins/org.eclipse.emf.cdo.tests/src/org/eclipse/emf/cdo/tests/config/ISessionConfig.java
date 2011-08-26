/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.config;

import org.eclipse.emf.cdo.session.CDOSession;

/**
 * @author Eike Stepper
 */
public interface ISessionConfig extends IConfig
{
  public static final String CAPABILITY_EMBEDDED = "session.embedded";

  public static final String CAPABILITY_NET4J = "session.net4j";

  public static final String CAPABILITY_NET4J_JVM = "session.net4j.jvm";

  public static final String CAPABILITY_NET4J_TCP = "session.net4j.tcp";

  public static final String CAPABILITY_NET4J_SSL = "session.net4j.ssl";

  public void startTransport() throws Exception;

  public void stopTransport() throws Exception;

  public String getURIPrefix();

  public CDOSession openSession();

  public CDOSession openSession(String repositoryName);
}
