/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.tests.model6.UnorderedList;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.EList;

import java.util.Arrays;

/**
 * Bug 390283: Incorrect handling of operations on unordered ELists
 *
 * @author Eike Stepper
 */
public class Bugzilla_390283_Test extends AbstractCDOTest
{
  public void testAddAfterRemove() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/resource1"));

    UnorderedList object = getModel6Factory().createUnorderedList();
    EList<UnorderedList> list = object.getContained();

    UnorderedList elem0 = getModel6Factory().createUnorderedList();
    UnorderedList elem1 = getModel6Factory().createUnorderedList();
    UnorderedList elem2 = getModel6Factory().createUnorderedList();
    UnorderedList elem3 = getModel6Factory().createUnorderedList(); // Does not fail for less than 4 elems!
    list.addAll(Arrays.asList(elem0, elem1, elem2, elem3));

    resource.getContents().add(object);
    transaction.commit();

    // Remove
    list.remove(elem1);

    // Add
    UnorderedList elem4 = getModel6Factory().createUnorderedList();
    list.add(elem4);

    transaction.commit();
  }

  public void testRemoveFromMiddle() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/resource1"));

    UnorderedList object = getModel6Factory().createUnorderedList();
    EList<UnorderedList> list = object.getContained();

    UnorderedList elem0 = getModel6Factory().createUnorderedList();
    UnorderedList elem1 = getModel6Factory().createUnorderedList();
    UnorderedList elem2 = getModel6Factory().createUnorderedList();
    list.addAll(Arrays.asList(elem0, elem1, elem2));

    resource.getContents().add(object);
    transaction.commit();

    // Remove
    list.remove(elem0); // Does not fail for list.remove(0)!

    transaction.commit();
  }
}
