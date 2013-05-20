/*
 * Copyright (c) 2008-2012 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.defs.CDOSessionDef;
import org.eclipse.emf.cdo.defs.CDOTransactionDef;
import org.eclipse.emf.cdo.defs.util.CDODefsUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig.Net4j;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.defs.util.Net4jDefsUtil;

/**
 * @author Andre Dietisheim
 */
public class CDOTransactionDefImplTest extends AbstractCDOTest
{
  private static final String TEST_RESOURCE = "/test1";

  public void testTransactionIsReused()
  {
    CDOTransactionDef transactionDef = CDODefsUtil.createCDOTransactionDef( //
        CDODefsUtil.createSessionDef( //
            IRepositoryConfig.REPOSITORY_NAME, //
            CDODefsUtil.createEagerPackageRegistryDef(), //
            Net4jDefsUtil.createTCPConnectorDef(Net4j.TCP.CONNECTOR_HOST)));

    CDOTransaction thisCdoTransactionReference = (CDOTransaction)transactionDef.getInstance();
    CDOTransaction thatCdoTransactionReference = (CDOTransaction)transactionDef.getInstance();

    assertEquals(true, thisCdoTransactionReference == thatCdoTransactionReference);

    thisCdoTransactionReference.getSession().close();
  }

  public void testClosedTransactionIsRecreated()
  {
    CDOTransactionDef cdoTransactionDef = CDODefsUtil.createCDOTransactionDef( //
        CDODefsUtil.createSessionDef( //
            IRepositoryConfig.REPOSITORY_NAME, //
            CDODefsUtil.createEagerPackageRegistryDef(), //
            Net4jDefsUtil.createTCPConnectorDef(Net4j.TCP.CONNECTOR_HOST)));
    CDOTransaction transactionInstance = (CDOTransaction)cdoTransactionDef.getInstance();
    transactionInstance.close();
    CDOTransaction newTransactionInstance = (CDOTransaction)cdoTransactionDef.getInstance();

    assertEquals(true, newTransactionInstance != transactionInstance);

    newTransactionInstance.getSession().close();
  }

  public void testCreateAndReadModel() throws CommitException
  {
    CDOSessionDef cdoSessionDef = //
    CDODefsUtil.createSessionDef( //
        IRepositoryConfig.REPOSITORY_NAME, //
        CDODefsUtil.createEagerPackageRegistryDef(), //
        Net4jDefsUtil.createTCPConnectorDef(Net4j.TCP.CONNECTOR_HOST));

    CDOTransactionDef transactionDef = CDODefsUtil.createCDOTransactionDef(cdoSessionDef);
    CDOTransaction transaction = (CDOTransaction)transactionDef.getInstance();

    transaction.getSession().getPackageRegistry().putEPackage(getModel1Package());
    CDOResource resource = transaction.createResource(TEST_RESOURCE);
    Customer customer = getModel1Factory().createCustomer();
    resource.getContents().add(customer);
    transaction.commit();

    CDOResource resourceFetched = transaction.getResource(getResourcePath(TEST_RESOURCE));
    assertEquals(true, resourceFetched.eContents().contains(customer));

    transaction.getSession().close();
  }
}
