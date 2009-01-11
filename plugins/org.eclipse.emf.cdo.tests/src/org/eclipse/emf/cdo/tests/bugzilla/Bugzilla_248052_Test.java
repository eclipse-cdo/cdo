/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionResolverImpl;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.emf.spi.cdo.InternalCDOObject;

import java.util.Map;

/**
 * 248052: CDO: UnsupportedOperationException in HibernateStoreAccessor
 * <p>
 * See https://bugs.eclipse.org/248052
 * 
 * @author Simon McDuff
 */
public class Bugzilla_248052_Test extends AbstractCDOTest
{
  @Override
  public Map<String, Object> getTestProperties()
  {
    Map<String, Object> testProperties = super.getTestProperties();
    testProperties.put(IRepository.Props.SUPPORTING_AUDITS, "false");
    return testProperties;
  }

  public void testNoSupportingDeltas() throws Exception
  {
    CDOSession session = openModel1Session();

    CDOTransaction transaction1 = session.openTransaction();
    CDOResource res = transaction1.createResource("/test1");

    Customer customer = getModel1Factory().createCustomer();

    res.getContents().add(customer);

    msg("Committing");
    transaction1.commit();

    InternalCDOObject cdoCustomer = FSMUtil.adapt(customer, transaction1);
    CDORevisionResolverImpl revisionManager = (CDORevisionResolverImpl)getRepository().getRevisionManager();
    revisionManager.removeCachedRevision(cdoCustomer.cdoID(), cdoCustomer.cdoRevision().getVersion());

    customer.setName("OTTAWA");

    transaction1.commit();
  }
}
