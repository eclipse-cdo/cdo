/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.OfflineConfig;

/**
 * @author Eike Stepper
 */
public abstract class DBOfflineConfig extends OfflineConfig
{
  private static final long serialVersionUID = 1L;

  private transient DBBrowser dbBrowser;

  public DBOfflineConfig(String name)
  {
    super(name);
  }

  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    dbBrowser = new DBBrowser(repositories);
    dbBrowser.activate();
  }

  @Override
  public void tearDown() throws Exception
  {
    dbBrowser.deactivate();
    super.tearDown();
  }
}
