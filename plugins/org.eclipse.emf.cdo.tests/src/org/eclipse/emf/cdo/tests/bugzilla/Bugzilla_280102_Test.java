/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * 280102: CDOView.getRootResource() throws exception on an empty repository
 * <p>
 * See https://bugs.eclipse.org/280102
 * 
 * @author Víctor Roldan Betancort
 */
public class Bugzilla_280102_Test extends AbstractCDOTest
{
  public void testRootResourceInTransaction() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource root = transaction.getRootResource();
    assertEquals(true, root.isRoot());
    transaction.commit();
  }

  public void testRootResourceInReadOnlyView() throws Exception
  {
    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource root = view.getRootResource();
    assertEquals(true, root.isRoot());
  }
}
