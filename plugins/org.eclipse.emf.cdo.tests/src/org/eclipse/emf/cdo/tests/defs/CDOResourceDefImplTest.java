/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.defs;

import org.eclipse.emf.cdo.defs.CDOResourceDef;
import org.eclipse.emf.cdo.defs.CDOSessionDef;
import org.eclipse.emf.cdo.defs.ResourceMode;
import org.eclipse.emf.cdo.defs.util.CDODefsUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig.Net4j;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.InvalidURIException;

import org.eclipse.net4j.defs.util.Net4jDefsUtil;

import org.eclipse.emf.common.util.WrappedException;

/**
 * @author Andre Dietisheim
 */
public class CDOResourceDefImplTest extends AbstractCDOTest
{
  private static final String TEST_RESOURCE = "/test1";

  public void testGetOrCreateResourceCreatesIfResourceDoesNotExist()
  {
    CDOSessionDef cdoSessionDef = //
    CDODefsUtil.createSessionDef( //
        IRepositoryConfig.REPOSITORY_NAME, //
        CDODefsUtil.createEagerPackageRegistryDef(), //
        Net4jDefsUtil.createTCPConnectorDef(Net4j.TCP.CONNECTOR_HOST));

    CDOResourceDef resourceDef = CDODefsUtil.createCDOResourceDef(CDODefsUtil.createCDOTransactionDef(cdoSessionDef));
    resourceDef.setResourceMode(ResourceMode.GET_OR_CREATE);
    resourceDef.setPath(TEST_RESOURCE);
    CDOResource cdoResource = (CDOResource)resourceDef.getInstance();
    assertNotNull(cdoResource);

    // clean up
    CDOTransaction transaction = (CDOTransaction)resourceDef.getCdoTransaction().getInstance();
    transaction.getSession().close();
  }

  public void testGetResourceFailsIfDoesExist()
  {
    CDOSessionDef cdoSessionDef = //

    CDODefsUtil.createSessionDef( //
        IRepositoryConfig.REPOSITORY_NAME, //
        CDODefsUtil.createEagerPackageRegistryDef(), //
        Net4jDefsUtil.createTCPConnectorDef(Net4j.TCP.CONNECTOR_HOST));

    CDOResourceDef resourceDef = CDODefsUtil.createCDOResourceDef(//
        CDODefsUtil.createCDOTransactionDef(cdoSessionDef));
    resourceDef.setResourceMode(ResourceMode.GET);
    resourceDef.setPath(TEST_RESOURCE);

    try
    {
      resourceDef.getInstance();
      fail("Exception expected!");
    }
    catch (WrappedException e)
    {
      assertEquals(true, e.getCause().getClass() == InvalidURIException.class);
    }
    finally
    {
      // clean up
      CDOTransaction transaction = (CDOTransaction)resourceDef.getCdoTransaction().getInstance();
      transaction.getSession().close();
    }
  }
}
