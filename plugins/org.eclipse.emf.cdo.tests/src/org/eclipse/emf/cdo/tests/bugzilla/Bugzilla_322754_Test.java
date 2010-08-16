/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * Bug 322754 - NullPointerException after deleting a resource
 * 
 * @author Eike Stepper
 */
public class Bugzilla_322754_Test extends AbstractCDOTest
{
  public void testResourceDelete() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/r1");

    msg("Fill and commit a resource");
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    transaction.commit();

    msg("Delete and commit the resource");
    resource.delete(null);
    transaction.commit();
    session.close();
  }
}
