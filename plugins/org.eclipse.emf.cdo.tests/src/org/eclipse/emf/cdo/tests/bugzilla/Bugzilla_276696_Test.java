/*
 * Copyright (c) 2009-2012, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EAttribute;

/**
 * ArrayIndexOutOfBoundsException while unsetting "modified" EAttribute in CDOResource
 * <p>
 * See bug 276696
 *
 * @author Victor Roldan Betancort
 */
public class Bugzilla_276696_Test extends AbstractCDOTest
{
  public void testModifiedUnset() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.setModified(true);

    EAttribute attrib = (EAttribute)resource.eClass().getEStructuralFeature("modified");
    resource.eUnset(attrib);

    transaction.commit();
    session.close();
  }
}
