/*
 * Copyright (c) 2008-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.defs;

import org.eclipse.emf.cdo.defs.CDODefsFactory;
import org.eclipse.emf.cdo.defs.CDOSessionDef;
import org.eclipse.emf.cdo.defs.util.CDODefsUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig.Net4j;

import org.eclipse.net4j.defs.util.Net4jDefsUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * @author Andre Dietisheim
 */
public class CDOSessionDefImplTest extends AbstractCDOTest
{
  public void testNoPackageRegistryThrows()
  {
    try
    {
      CDOSessionDef cdoSessionDef = CDODefsFactory.eINSTANCE.createCDOSessionDef();
      cdoSessionDef.setConnectorDef(Net4jDefsUtil.createTCPConnectorDef(Net4j.TCP.CONNECTOR_HOST));
      cdoSessionDef.setLegacySupportEnabled(false);
      cdoSessionDef.setRepositoryName(IRepositoryConfig.REPOSITORY_NAME);
      cdoSessionDef.getInstance();
      fail("IllegalStateException expected!");
    }
    catch (IllegalStateException e)
    {
      // Success
    }
  }

  public void testConnectorAndFailMayBeUnset()
  {
    {
      CDOSessionDef cdoSessionDef = CDODefsFactory.eINSTANCE.createCDOSessionDef();
      cdoSessionDef.setConnectorDef(Net4jDefsUtil.createTCPConnectorDef(Net4j.TCP.CONNECTOR_HOST));
    }

    {
      CDOSessionDef cdoSessionDef = CDODefsFactory.eINSTANCE.createCDOSessionDef();
      cdoSessionDef.setConnectorDef(Net4jDefsUtil.createTCPConnectorDef(Net4j.TCP.CONNECTOR_HOST));
      cdoSessionDef.unsetConnectorDef();
    }
  }

  public void testSessionInstanceIsActive()
  {
    CDOSessionDef cdoSessionDef = CDODefsFactory.eINSTANCE.createCDOSessionDef();
    cdoSessionDef.setConnectorDef( //
        Net4jDefsUtil.createTCPConnectorDef(Net4j.TCP.CONNECTOR_HOST));
    cdoSessionDef.setLegacySupportEnabled(false);
    cdoSessionDef.setRepositoryName(IRepositoryConfig.REPOSITORY_NAME);
    cdoSessionDef.setCdoPackageRegistryDef(CDODefsUtil.createEagerPackageRegistryDef());
    CDOSession cdoSession = (CDOSession)cdoSessionDef.getInstance();
    assertEquals(true, LifecycleUtil.isActive(cdoSession));
  }
}
