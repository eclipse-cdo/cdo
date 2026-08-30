/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.sql.DataSource;

import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.util.container.ManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.Test;

/**
 * @author Eike Stepper
 */
public class ConsumerSmokeTest
{
  @Test
  public void clientAndCommonApisAreUsable()
  {
    ManagedContainer container = new ManagedContainer();
    assertNotNull(container);
    assertNotNull(CDOUtil.createCollectionLoadingPolicy(10, 20));
    LifecycleUtil.activate(container);
    
    try
    {
      assertTrue(container.isEmpty());
    }
    finally
    {
      LifecycleUtil.deactivate(container);
    }
  }

  @Test
  public void serverH2ApisAndExplicitDriverAreUsable()
  {
    H2Adapter adapter = new H2Adapter();
    assertNotNull(adapter);

    JdbcDataSource h2 = new JdbcDataSource();
    h2.setURL("jdbc:h2:mem:cdo-consumer-test;DB_CLOSE_DELAY=-1");
    h2.setUser("sa");
    h2.setPassword("");
    
    DataSource dataSource = h2;
    assertNotNull(dataSource);
    assertNotNull(dataSource.getClass().getName());
  }
}
