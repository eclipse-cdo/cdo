/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.admin.CDOAdminUtil;
import org.eclipse.emf.cdo.common.admin.CDOAdmin;
import org.eclipse.emf.cdo.common.admin.CDOAdminRepository;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.admin.CDOAdminHandler;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.Map;

/**
 * Design a repository administration API
 * <p>
 * See bug 381472
 *
 * @author Eike Stepper
 */
public class Bugzilla_381472_Test extends AbstractCDOTest
{
  private static final String ADMIN_HANDLER_TYPE = "test";

  private CDOAdmin admin;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    getContainerConfig().getServerContainer().registerFactory(new CDOAdminHandler.Factory(ADMIN_HANDLER_TYPE)
    {
      @Override
      public CDOAdminHandler create(String description) throws ProductCreationException
      {
        return new CDOAdminHandler()
        {
          public String getType()
          {
            return ADMIN_HANDLER_TYPE;
          }

          public IRepository createRepository(String name, Map<String, Object> properties)
          {
            return getRepository(name);
          }

          public void deleteRepository(IRepository delegate)
          {
            // Do nothing
          }
        };
      }
    });

    SessionConfig.Net4j sessionConfig = (SessionConfig.Net4j)getSessionConfig();
    IConnector connector = sessionConfig.getConnector();
    admin = CDOAdminUtil.openAdmin(connector);
  }

  @Override
  protected void doTearDown() throws Exception
  {
    admin.close();
    super.doTearDown();
  }

  @Deprecated
  public void testDummy() throws Exception
  {
  }

  public void _testEmpty() throws Exception
  {
    CDOAdminRepository[] repositories = admin.getRepositories();
    System.out.println(repositories);
  }
}
