/*
 * Copyright (c) 2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model5.Child;
import org.eclipse.emf.cdo.tests.model5.Parent;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EcorePackage;

import java.util.List;

/**
 * Bug 416366: Support implicit root class in OCL queries
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class Bugzilla_416366_Test extends AbstractCDOTest
{
  public void testImplicitEObjectFeatures() throws Exception
  {
    Parent parentA = createParent("A");
    parentA.getChildren().add(createChild("A1"));
    parentA.getChildren().add(createChild("A2"));

    Parent parentB = createParent("B");
    parentB.getChildren().add(createChild("B1"));
    parentB.getChildren().add(createChild("B2"));

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("test-resource"));
    resource.getContents().add(parentA);
    resource.getContents().add(parentB);

    transaction.commit();

    CDOView newView = session.openView();
    CDOQuery ocl = newView.createQuery("ocl", "Child.allInstances()->select(c|c.eContainer().oclAsType(Parent).name = 'B')", getModel5Package().getChild());

    // eContainer() is inherited implicitly from EObject
    ocl.setParameter("cdoImplicitRootClass", EcorePackage.Literals.EOBJECT);

    List<Child> results = ocl.getResult(Child.class);
    assertEquals(2, results.size());

    // They are children of B
    assertEquals(true, parentB.getChildren().contains(transaction.getObject(results.get(0))));
    assertEquals(true, parentB.getChildren().contains(transaction.getObject(results.get(1))));
  }

  private Parent createParent(String name)
  {
    Parent result = getModel5Factory().createParent();
    result.setName(name);
    return result;
  }

  private Child createChild(String name)
  {
    Child result = getModel5Factory().createChild();
    result.setName(name);
    return result;
  }
}
