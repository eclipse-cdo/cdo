/*
 * Copyright (c) 2010-2012, 2025 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.tests.model4.RefMultiContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefSingleContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class Bugzilla_318998_Test extends AbstractCDOTest
{
  @Override
  protected void initTestProperties(Map<String, Object> properties)
  {
    super.initTestProperties(properties);
    properties.put(IRepository.Props.ENSURE_REFERENTIAL_INTEGRITY, "true");
  }

  public void testQueryXRefSingle() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    msg("Creating test data");
    CDOResource resource = transaction.createResource(getResourcePath("/test"));
    RefSingleContainedNPL container = getModel4Factory().createRefSingleContainedNPL();
    RefSingleNonContainedNPL nonContainer = getModel4Factory().createRefSingleNonContainedNPL();
    ContainedElementNoOpposite contained = getModel4Factory().createContainedElementNoOpposite();
    container.setElement(contained);
    nonContainer.setElement(contained);

    resource.getContents().add(container);
    resource.getContents().add(nonContainer);

    transaction.commit();

    container.setElement(null);

    try
    {
      transaction.commit();
      fail("Exception expected");
    }
    catch (Exception e)
    {
      msg("catched expected ex: " + e.getMessage());
    }
  }

  public void testQueryXRefMany() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    msg("Creating test data");
    CDOResource resource = transaction.createResource(getResourcePath("/test"));
    RefMultiContainedNPL container = getModel4Factory().createRefMultiContainedNPL();
    RefMultiNonContainedNPL nonContainer = getModel4Factory().createRefMultiNonContainedNPL();
    ContainedElementNoOpposite contained = getModel4Factory().createContainedElementNoOpposite();
    container.getElements().add(contained);
    nonContainer.getElements().add(contained);

    resource.getContents().add(container);
    resource.getContents().add(nonContainer);

    transaction.commit();

    container.getElements().remove(0);

    try
    {
      transaction.commit();
      fail("Exception expected");
    }
    catch (Exception e)
    {
      msg("Caught expected exception: " + e.getMessage());
    }
  }
}
