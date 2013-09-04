/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import java.util.List;

/**
 * Bug 416474: [OCL] Add some non-standard operations to support efficient OCL queries
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class Bugzilla_416474_Test extends AbstractCDOTest
{
  public void testAllProperContentsNoTypeFilter() throws Exception
  {
    EPackage root = createPackage("root_416474_1", "root_416474_1",
        "http://www.eclipse.org/CDO/test/bug416474/Root_416474_1");
    EClass a = createClass(root, "RootA_416474_1");
    EClass b = createClass(root, "RootB_416474_1");

    EPackage nested = createPackage("nested_416474_1", "nested_416474_1",
        "http://www.eclipse.org/CDO/test/bug416474/Nested_416474_1");
    createClass(nested, "NestedA_416474_1");
    createClass(nested, "NestedB_416474_1");
    root.getESubpackages().add(nested);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("root_416474_1.ecore"));
    resource1.getContents().add(root);
    CDOResource resource2 = transaction.createResource(getResourcePath("nested_416474_1.ecore"));
    resource2.getContents().add(nested);

    transaction.commit();

    CDOView newView = session.openView();
    CDOQuery ocl = newView.createQuery("ocl",
        "eresource::CDOResource.allInstances()->any(name='root_416474_1.ecore').cdoAllProperContents()",
        EcorePackage.Literals.EPACKAGE);

    List<?> results = ocl.getResult();
    assertEquals(true, results.contains(newView.getObject(root)));
    assertEquals(true, results.contains(newView.getObject(a)));
    assertEquals(true, results.contains(newView.getObject(b)));
    assertEquals(3, results.size());
  }

  public void testAllProperContentsTypeFilter() throws Exception
  {
    EPackage root = createPackage("root_416474_2", "root_416474_2",
        "http://www.eclipse.org/CDO/test/bug416474/Root_416474_2");
    createClass(root, "RootA_416474_2");
    createClass(root, "RootB_416474_2");

    EPackage nested = createPackage("nested_416474_2", "nested_416474_2",
        "http://www.eclipse.org/CDO/test/bug416474/Nested_416474_2");
    createClass(nested, "NestedA_416474_2");
    createClass(nested, "NestedB_416474_2");
    root.getESubpackages().add(nested);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("root_416474_2.ecore"));
    resource1.getContents().add(root);
    CDOResource resource2 = transaction.createResource(getResourcePath("nested_416474_2.ecore"));
    resource2.getContents().add(nested);

    transaction.commit();

    CDOView newView = session.openView();
    CDOQuery ocl = newView
        .createQuery(
            "ocl",
            "eresource::CDOResource.allInstances()->any(name='root_416474_2.ecore').cdoAllProperContents(EClass).name->asSet()",
            EcorePackage.Literals.EPACKAGE);

    List<?> results = ocl.getResult();
    assertEquals(true, results.contains("RootA_416474_2"));
    assertEquals(true, results.contains("RootB_416474_2"));
    assertEquals(2, results.size());
  }

  public void testMatchesAnyStringAttribute() throws Exception
  {
    EPackage root = createPackage("root_416474_3", "root_416474_3",
        "http://www.eclipse.org/CDO/test/bug416474/Root_416474_3");
    createClass(root, "RootA_416474_3");
    createClass(root, "RootB_416474_3");

    EPackage nested = createPackage("nested_416474_3", "nested_416474_3",
        "http://www.eclipse.org/CDO/test/bug416474/Nested_416474_3");
    EClass a1 = createClass(nested, "NestedA_416474_3");
    EClass b1 = createClass(nested, "NestedB_416474_3");
    root.getESubpackages().add(nested);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("root_416474_3.ecore"));
    resource1.getContents().add(root);
    CDOResource resource2 = transaction.createResource(getResourcePath("nested_416474_3.ecore"));
    resource2.getContents().add(nested);

    transaction.commit();

    CDOView newView = session.openView();
    CDOQuery ocl = newView.createQuery("ocl",
        "EModelElement.allInstances()->select(e | e.cdoMatches('http://www.eclipse.org/CDO/test/.*_416474_3'))",
        EcorePackage.Literals.EPACKAGE);

    List<?> results = ocl.getResult();
    assertEquals(true, results.contains(newView.getObject(root)));
    assertEquals(true, results.contains(newView.getObject(nested)));
    assertEquals(2, results.size());

    ocl = newView.createQuery("ocl", "EModelElement.allInstances()->select(e | e.cdoMatches('.*Nested.?_416474_3'))",
        EcorePackage.Literals.EPACKAGE);

    results = ocl.getResult();
    assertEquals(true, results.contains(newView.getObject(nested)));
    assertEquals(true, results.contains(newView.getObject(a1)));
    assertEquals(true, results.contains(newView.getObject(b1)));
    assertEquals(3, results.size());
  }

  private EPackage createPackage(String name, String nsPrefix, String nsURI)
  {
    EPackage result = EcoreFactory.eINSTANCE.createEPackage();
    result.setName(name);
    result.setNsPrefix(nsPrefix);
    result.setNsURI(nsURI);
    return result;
  }

  private EClass createClass(EPackage owner, String name)
  {
    EClass result = EcoreFactory.eINSTANCE.createEClass();
    result.setName(name);
    owner.getEClassifiers().add(result);
    return result;
  }
}
