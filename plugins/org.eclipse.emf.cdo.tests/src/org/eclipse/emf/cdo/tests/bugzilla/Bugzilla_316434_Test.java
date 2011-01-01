/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class Bugzilla_316434_Test extends AbstractCDOTest
{
  @Override
  public synchronized Map<String, Object> getTestProperties()
  {
    Map<String, Object> map = super.getTestProperties();
    map.put(IRepository.Props.ENSURE_REFERENTIAL_INTEGRITY, "true");
    return map;
  }

  public void testInitial() throws Exception
  {
    {
      ContainedElementNoOpposite target = getModel4Factory().createContainedElementNoOpposite();
      RefSingleNonContainedNPL source = getModel4Factory().createRefSingleNonContainedNPL();
      source.setElement(target);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.createResource("/test1");
      resource.getContents().add(target);
      resource.getContents().add(source);

      transaction.commit();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource = transaction.getResource("/test1");
    resource.getContents().remove(0);

    try
    {
      transaction.commit();
      fail("CommitException expected");
    }
    catch (CommitException expected)
    {
      // SUCCESS
    }
  }
}
