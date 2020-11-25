/*
 * Copyright (c) 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOLockStatePrefetcher;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogHandler;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.log.OMLogger.Level;

import org.eclipse.emf.ecore.util.EcoreUtil;

import org.junit.Assert;

/**
 * Bug 466721 about NPE on {@link CDOView#getObject(CDOID)} call with a detached object's id and lock state prefetch option enabled.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_466721_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "test1.model1";

  /**
   * Test {@link CDOView#getObject(CDOID)} with a detached object's id and lock state prefetch option enabled.
   */
  public void testObjectNotFoundExceptionWithLockStatePrefetchEnabled() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    new CDOLockStatePrefetcher(transaction1, false);

    CDOResource resource1 = transaction1.createResource(getResourcePath(RESOURCE_NAME));
    Company company = getModel1Factory().createCompany();
    resource1.getContents().add(company);
    transaction1.commit();

    CDOObject companyCDOObject = CDOUtil.getCDOObject(company);
    CDOID companyID = companyCDOObject.cdoID();
    EcoreUtil.remove(company);
    transaction1.commit();
    ExceptionAsserter exceptionAsserter = new ExceptionAsserter();
    try
    {
      OMPlatform.INSTANCE.addLogHandler(exceptionAsserter);
      transaction1.getObject(companyID);
      fail("an ObjectNotFoundException should be thrown for this deleted object");
    }
    catch (ObjectNotFoundException e)
    {
      Throwable t = exceptionAsserter.getThrowable();
      Assert.assertFalse(t != null ? exceptionAsserter.getThrowable().toString() : "", exceptionAsserter.exceptionsOccured());
    }
    finally
    {
      OMPlatform.INSTANCE.removeLogHandler(exceptionAsserter);
    }
  }

  private static final class ExceptionAsserter implements OMLogHandler
  {
    private boolean exceptionsOccured;

    private Throwable throwable;

    @Override
    public void logged(OMLogger logger, Level level, String msg, Throwable t)
    {
      exceptionsOccured = exceptionsOccured || t != null;
      throwable = t;
    }

    public boolean exceptionsOccured()
    {
      return exceptionsOccured;
    }

    public Throwable getThrowable()
    {
      return throwable;
    }

  }
}
