/*
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

import org.eclipse.emf.cdo.defs.CDODefsFactory;
import org.eclipse.emf.cdo.defs.CDOSessionDef;
import org.eclipse.emf.cdo.defs.CDOViewDef;
import org.eclipse.emf.cdo.defs.impl.CDOViewDefImpl;
import org.eclipse.emf.cdo.defs.util.CDODefsUtil;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig.Net4j;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.defs.TCPConnectorDef;
import org.eclipse.net4j.defs.util.Net4jDefsUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * @author Andre Dietisheim
 */
public class CDOViewDefImplTest extends AbstractCDOTest
{
  private CDOViewDef cdoViewDef;

  private CDOSessionDef cdoSessionDef;

  private TCPConnectorDef tcpConnectorDef;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    cdoViewDef = CDODefsFactory.eINSTANCE.createCDOViewDef();
    tcpConnectorDef = Net4jDefsUtil.createTCPConnectorDef(Net4j.TCP.CONNECTOR_HOST);
    cdoSessionDef = CDODefsUtil.createSessionDef(//
        IRepositoryConfig.REPOSITORY_NAME, //
        CDODefsUtil.createEagerPackageRegistryDef(), //
        tcpConnectorDef);
    cdoViewDef.setCdoSessionDef( //
        cdoSessionDef);
  }

  @Override
  protected void doTearDown() throws Exception
  {
    cdoViewDef.unsetInstance();
    cdoSessionDef.unsetInstance();
    tcpConnectorDef.unsetInstance();
    super.doTearDown();
  }

  private static final String RESOURCE_ID = "/test1";

  public void testCreateView()
  {
    CDOView cdoView = (CDOView)cdoViewDef.getInstance();
    assertEquals(true, cdoView.isReadOnly());
  }

  public void testCreateViewCreatesOnceAndReuses()
  {
    CDOView thisCdoViewReference = (CDOView)cdoViewDef.getInstance();
    CDOView thatCdoViewReference = (CDOView)cdoViewDef.getInstance();

    assertEquals(true, thisCdoViewReference == thatCdoViewReference);
  }

  public void testViewCreatedCanRead() throws CommitException
  {
    CDOTransaction transaction = openSession().openTransaction();
    transaction.createResource(RESOURCE_ID);
    transaction.commit();

    CDOView cdoView = (CDOView)cdoViewDef.getInstance();

    assertEquals(true, cdoView.hasResource(RESOURCE_ID));
    assertEquals(false, cdoView.hasResource("/test2"));
  }

  public void testViewMayBeUnset()
  {
    CDOView cdoView = (CDOView)cdoViewDef.getInstance();
    cdoViewDef.unsetInstance(); // deactivates instance
    assertEquals(true, ((CDOViewDefImpl)cdoViewDef).getInternalInstance() == null);
    assertEquals(true, !LifecycleUtil.isActive(cdoView));
  }
}
