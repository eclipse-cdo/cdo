/*
 * Copyright (c) 2010-2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model5.GenListOfIntArray;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * Bug 322804 - ClassCastException adding an object to an EList with objects of a custom data type
 *
 * @author Eike Stepper
 */
public class Bugzilla_322804_Test extends AbstractCDOTest
{
  public void testAddElementToCustomTypedList() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/r1"));

    msg("Fill and commit a resource");
    GenListOfIntArray object = getModel5Factory().createGenListOfIntArray();
    resource.getContents().add(object);
    transaction.commit();

    msg("Add int[] elements");
    object.getElements().add(new int[] { 1, 2, 3, 4, 5 });
    object.getElements().add(new int[] { 10, 20, 30, 40, 50 });
    transaction.commit();
    session.close();
  }
}
