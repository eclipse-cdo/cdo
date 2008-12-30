/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.defs;

import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.cdodefs.CDODefsFactory;
import org.eclipse.emf.cdo.cdodefs.CDOSessionDef;
import org.eclipse.emf.cdo.cdodefs.CDOViewDef;
import org.eclipse.emf.cdo.cdodefs.util.CDODefsUtil;
import org.eclipse.emf.cdo.common.CDOProtocolView.Type;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;

import org.eclipse.net4j.net4jdefs.TCPConnectorDef;
import org.eclipse.net4j.net4jdefs.util.Net4jDefsUtil;

/**
 * @author Eike Stepper
 */
public class CDOViewDefImplTest extends AbstractCDOTest
{
  private static final String RESOURCE_ID = "/test1";

  public void testCreateView()
  {

    CDOViewDef cdoViewDef = CDODefsFactory.eINSTANCE.createCDOViewDef();
    TCPConnectorDef tcpConnectorDef = Net4jDefsUtil.createTCPConnectorDef(SessionConfig.TCP.CONNECTOR_HOST);
    CDOSessionDef cdoSessionDef = CDODefsUtil.createSessionDef(//
        IRepositoryConfig.REPOSITORY_NAME, //
        CDODefsUtil.createEagerPackageRegistryDef(), //
        tcpConnectorDef);
    cdoViewDef.setCdoSessionDef( //
        cdoSessionDef);

    CDOView cdoView = (CDOView)cdoViewDef.getInstance();
    assertTrue(cdoView.getViewType() == Type.READONLY);

    // cleanup
    cdoViewDef.unsetInstance();
    cdoSessionDef.unsetInstance();
    tcpConnectorDef.unsetInstance();
  }

  public void testCreateViewCreatesOnceAndReuses()
  {
    CDOViewDef cdoViewDef = CDODefsFactory.eINSTANCE.createCDOViewDef();
    TCPConnectorDef tcpConnectorDef = Net4jDefsUtil.createTCPConnectorDef(SessionConfig.TCP.CONNECTOR_HOST);
    CDOSessionDef cdoSessionDef = CDODefsUtil.createSessionDef(//
        IRepositoryConfig.REPOSITORY_NAME, //
        CDODefsUtil.createEagerPackageRegistryDef(), //
        tcpConnectorDef);
    cdoViewDef.setCdoSessionDef( //
        cdoSessionDef);

    CDOView thisCdoViewReference = (CDOView)cdoViewDef.getInstance();
    CDOView thatCdoViewReference = (CDOView)cdoViewDef.getInstance();

    assertTrue(thisCdoViewReference == thatCdoViewReference);

    // cleanup
    cdoViewDef.unsetInstance();
    cdoSessionDef.unsetInstance();
    tcpConnectorDef.unsetInstance();
  }

  public void testViewCreatedCanRead()
  {
    CDOTransaction transaction = openSession().openTransaction();
    transaction.createResource(RESOURCE_ID);
    transaction.commit();

    CDOViewDef cdoViewDef = CDODefsFactory.eINSTANCE.createCDOViewDef();
    TCPConnectorDef tcpConnectorDef = Net4jDefsUtil.createTCPConnectorDef(SessionConfig.TCP.CONNECTOR_HOST);
    CDOSessionDef cdoSessionDef = CDODefsUtil.createSessionDef(//
        IRepositoryConfig.REPOSITORY_NAME, //
        CDODefsUtil.createEagerPackageRegistryDef(), //
        tcpConnectorDef);
    cdoViewDef.setCdoSessionDef( //
        cdoSessionDef);

    CDOView cdoView = (CDOView)cdoViewDef.getInstance();

    assertEquals(true, cdoView.hasResource(RESOURCE_ID));
    assertEquals(false, cdoView.hasResource("/test2"));

    // cleanup
    cdoViewDef.unsetInstance();
    cdoSessionDef.unsetInstance();
    tcpConnectorDef.unsetInstance();
  }
}
