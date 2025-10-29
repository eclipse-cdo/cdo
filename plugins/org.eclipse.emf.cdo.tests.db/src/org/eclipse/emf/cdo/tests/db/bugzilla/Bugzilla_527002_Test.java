/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Skips;
import org.eclipse.emf.cdo.tests.db.DBConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import java.util.Map;

/**
 * Bug 527002: ClassCastException in mapping strategy.
 *
 * @author Eike Stepper
 */
@Skips({ IRepositoryConfig.CAPABILITY_AUDITING, IRepositoryConfig.CAPABILITY_BRANCHING })
public class Bugzilla_527002_Test extends AbstractCDOTest
{
  @Override
  protected void initTestProperties(Map<String, Object> properties)
  {
    super.initTestProperties(properties);
    properties.put(DBConfig.PROP_TEST_MAPPING_STRATEGY, CDODBUtil.createHorizontalMappingStrategy());
  }

  public void testDelegatingMappingStrategy() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));
    resource.getContents().add(getModel1Factory().createProduct1());
    transaction.commit();
  }
}
