/*
 * Copyright (c) 2018 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.AbstractHorizontalMappingStrategy;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOQuery;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.resource.Resource;

import java.util.List;

/**
 * Bug 534438: Too many errors like java.lang.IllegalStateException: SELECT CDO_CLASS FROM CDO_OBJECTS WHERE CDO_ID=? already in cache.
 *
 * @author Eike Stepper
 */
public class Bugzilla_534438_Test extends AbstractCDOTest
{
  public void testDBConnectionUsedByMultipleThreads() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    for (int i = 0; i < 100; i++)
    {
      CDOResource resource = transaction.createResource(getResourcePath("resource-" + i + "-fill.transformation"));
      resource.getContents().add(getModel1Factory().createSupplier());
    }

    transaction.commit();

    // Clear revision cache to force ObjectTypeTable to be used.
    clearCache(getRepository().getRevisionManager());

    // Clear object type cache to force ObjectTypeTable to be used.
    AbstractHorizontalMappingStrategy mappingStrategy = (AbstractHorizontalMappingStrategy)((DBStore)getRepository().getStore()).getMappingStrategy();
    LifecycleUtil.deactivate(mappingStrategy.getObjectTypeMapper());
    LifecycleUtil.activate(mappingStrategy.getObjectTypeMapper());

    CDOQuery query = transaction.createQuery("ocl", "Supplier.allInstances()->select( o | o.oclAsType(ecore::EObject).eResource()."
        + "oclAsType(eresource::CDOResource).name.endsWith('-fill.transformation'))", getModel1Package().getSupplier());

    List<Supplier> result = query.getResult();
    msg(result.size());

    for (Supplier supplier : result)
    {
      Resource resource = supplier.eResource();
      resource.delete(null);
    }

    transaction.commit();
  }
}
