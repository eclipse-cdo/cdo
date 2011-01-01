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

import org.eclipse.emf.cdo.defs.CDOAuditDef;
import org.eclipse.emf.cdo.defs.util.CDODefsUtil;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig.Net4j;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.defs.util.Net4jDefsUtil;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

import org.eclipse.emf.ecore.EObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Andre Dietisheim
 */
public class CDOAuditDefImplTest extends AbstractCDOTest
{
  private static final String RESOURCE_ID = "/defsTest1";

  public void testCreateAuditCanRead() throws CommitException
  {
    final String customerName = "Heino";

    CDOTransaction transaction = openSession().openTransaction();
    Customer customer = getModel1Factory().createCustomer();
    customer.setName(customerName);
    transaction.createResource(RESOURCE_ID).getContents().add(customer);
    transaction.commit();

    CDOAuditDef cdoAuditDef = CDODefsUtil.createCDOAuditDef( //
        CDODefsUtil.createSessionDef( //
            IRepositoryConfig.REPOSITORY_NAME, //
            CDODefsUtil.createEagerPackageRegistryDef(), //
            Net4jDefsUtil.createTCPConnectorDef( //
                Net4j.TCP.CONNECTOR_HOST)));
    cdoAuditDef.setTimeStamp(new Date());

    CDOView cdoAudit = (CDOView)cdoAuditDef.getInstance();
    EObject object = cdoAudit.getResource(RESOURCE_ID).getContents().get(0);
    assertTrue(object instanceof Customer && customerName.equals(customer.getName()));
  }

  public void testCreateAuditCreatesOnceAndReusesEvenOnChangedTimestamp()
  {
    CDOAuditDef cdoAuditDef = CDODefsUtil.createCDOAuditDef( //
        CDODefsUtil.createSessionDef( //
            IRepositoryConfig.REPOSITORY_NAME, //
            CDODefsUtil.createEagerPackageRegistryDef(), //
            Net4jDefsUtil.createTCPConnectorDef( //
                Net4j.TCP.CONNECTOR_HOST)));
    cdoAuditDef.setTimeStamp(new Date());

    CDOView thisCdoAuditReference = (CDOView)cdoAuditDef.getInstance();

    Calendar calendar = GregorianCalendar.getInstance();
    ConcurrencyUtil.sleep(1000l);
    calendar.roll(Calendar.SECOND, true);
    cdoAuditDef.setTimeStamp(calendar.getTime());
    CDOView thatCdoAuditReference = (CDOView)cdoAuditDef.getInstance();
    assertTrue(thisCdoAuditReference == thatCdoAuditReference);
  }
}
