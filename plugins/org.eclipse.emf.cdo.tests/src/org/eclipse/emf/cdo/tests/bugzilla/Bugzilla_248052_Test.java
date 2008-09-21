/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionResolverImpl;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Customer;

import org.eclipse.emf.internal.cdo.InternalCDOObject;
import org.eclipse.emf.internal.cdo.util.FSMUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 248052: CDO: UnsupportedOperationException in HibernateStoreReader
 * <p>
 * See https://bugs.eclipse.org/248052
 * 
 * @author Simon McDuff
 */
public class Bugzilla_248052_Test extends AbstractCDOTest
{
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

  @Override
  protected Repository createRepository()
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(IRepository.Props.PROP_SUPPORTING_REVISION_DELTAS, "false");
    props.put(IRepository.Props.PROP_SUPPORTING_AUDITS, "false");

    IStore store = createStore();
    Repository repository = new Repository();
    repository.setName(REPOSITORY_NAME);
    repository.setProperties(props);
    repository.setStore(store);
    return repository;
  }
}
