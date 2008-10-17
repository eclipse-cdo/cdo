/***************************************************************************
 * Copyright (c) 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Simon McDuff - maintenance
 **************************************************************************/

package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;

import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.ecore.EObject;

/**
 * Persisted objects keeps references to detached objects through deltas
 * <p>
 * See https://bugs.eclipse.org/250757
 * 
 * @author Victor Roldan Betancort
 */
public class Bugzilla_250757_Test extends AbstractCDOTest
{

  public void testAddAndRemoveFromPersistedList() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();

    EObject obj = getModel1Factory().createCompany();
    res.getContents().add(obj);
    res.getContents().remove(obj);

    try
    {
      transaction1.commit();
    }
    catch (TransactionException e)
    {
      fail("Should not have an exception");
    }
  }

  public void testAddAndMoveAndRemoveFromPersistedList() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();

    EObject obj = getModel1Factory().createCompany();
    res.getContents().add(obj);
    res.getContents().move(1, 0);
    res.getContents().remove(obj);

    try
    {
      transaction1.commit();
    }
    catch (TransactionException e)
    {
      fail("Should not have an exception");
    }
  }

  public void testAddAndMoveAndRemoveFromPersistedListWithSavePoint() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();

    EObject obj = getModel1Factory().createCompany();
    res.getContents().add(obj);

    transaction1.setSavepoint();

    res.getContents().move(1, 0);
    res.getContents().remove(obj);

    try
    {
      transaction1.commit();
    }
    catch (TransactionException e)
    {
      e.printStackTrace();
      fail("Should not have an exception");
    }
  }

  public void testAddAndMoveAndRemoveFromPersistedListWithManySavePoint() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();

    transaction1.setSavepoint();

    EObject obj = getModel1Factory().createCompany();
    res.getContents().add(obj);
    transaction1.setSavepoint();

    res.getContents().move(1, 0);
    transaction1.setSavepoint();

    res.getContents().remove(obj);
    transaction1.setSavepoint();

    try
    {
      transaction1.commit();
    }
    catch (TransactionException e)
    {
      e.printStackTrace();
      fail("Should not have an exception");
    }
  }
}
