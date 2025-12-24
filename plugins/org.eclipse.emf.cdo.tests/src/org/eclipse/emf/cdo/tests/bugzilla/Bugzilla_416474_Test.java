/*
 * Copyright (c) 2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.core.runtime.Path;

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
    EPackage root = createPackage("root", "root", "http://www.eclipse.org/CDO/test1/bug416474/Root");
    EClass a = createClass(root, "RootA");
    EClass b = createClass(root, "RootB");

    EPackage nested = createPackage("nested", "nested", "http://www.eclipse.org/CDO/test1/bug416474/Nested");
    createClass(nested, "NestedA");
    createClass(nested, "NestedB");
    root.getESubpackages().add(nested);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    String rootPath = getResourcePath("root.ecore");
    CDOResource resource1 = transaction.createResource(rootPath);
    resource1.getContents().add(root);
    String nestedPath = getResourcePath("nested.ecore");
    CDOResource resource2 = transaction.createResource(nestedPath);
    resource2.getContents().add(nested);

    transaction.commit();

    CDOView newView = session.openView();
    CDOQuery ocl = newView.createQuery("ocl", "eresource::CDOResource.allInstances()->any(path=rootPath).cdoAllProperContents()",
        EcorePackage.Literals.EPACKAGE);
    ocl.setParameter("rootPath", rootPath);

    List<?> results = ocl.getResult();
    assertEquals(true, results.contains(newView.getObject(root)));
    assertEquals(true, results.contains(newView.getObject(a)));
    assertEquals(true, results.contains(newView.getObject(b)));
    assertEquals(3, results.size());
  }

  public void testAllProperContentsTypeFilter() throws Exception
  {
    EPackage root = createPackage("root", "root", "http://www.eclipse.org/CDO/test2/bug416474/Root");
    createClass(root, "RootA");
    createClass(root, "RootB");

    EPackage nested = createPackage("nested", "nested", "http://www.eclipse.org/CDO/test2/bug416474/Nested");
    createClass(nested, "NestedA");
    createClass(nested, "NestedB");
    root.getESubpackages().add(nested);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    String rootPath = getResourcePath("root.ecore");
    CDOResource resource1 = transaction.createResource(rootPath);
    resource1.getContents().add(root);
    String nestedPath = getResourcePath("nested.ecore");
    CDOResource resource2 = transaction.createResource(nestedPath);
    resource2.getContents().add(nested);

    transaction.commit();

    CDOView newView = session.openView();
    CDOQuery ocl = newView.createQuery("ocl", "eresource::CDOResource.allInstances()->any(path=rootPath).cdoAllProperContents(EClass).name->asSet()",
        EcorePackage.Literals.EPACKAGE);
    ocl.setParameter("rootPath", rootPath);

    List<?> results = ocl.getResult();
    assertEquals(true, results.contains("RootA"));
    assertEquals(true, results.contains("RootB"));
    assertEquals(2, results.size());
  }

  public void testMatchesAnyStringAttribute() throws Exception
  {
    EPackage root = createPackage("root", "root", "http://www.eclipse.org/CDO/test3/bug416474/Root");
    createClass(root, "RootA");
    createClass(root, "RootB");

    EPackage nested = createPackage("nested", "nested", "http://www.eclipse.org/CDO/test3/bug416474/Nested");
    EClass a1 = createClass(nested, "NestedA");
    EClass b1 = createClass(nested, "NestedB");
    root.getESubpackages().add(nested);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    String rootPath = getResourcePath("root.ecore");
    CDOResource resource1 = transaction.createResource(rootPath);
    resource1.getContents().add(root);
    String nestedPath = getResourcePath("nested.ecore");
    CDOResource resource2 = transaction.createResource(nestedPath);
    resource2.getContents().add(nested);

    transaction.commit();

    // Ensure a trailing separator without adding an extra one if already present
    String folderPath = new Path(resource2.getFolder().getPath()).addTrailingSeparator().toString();

    // Scope the query to resources in this test's unique folder *without* relying on the
    // cdoAllProperContents() operation tested by other methods in this class
    String scopeClause = "e.eResource().oclAsType(eresource::CDOResource).path.startsWith(folderPath) and ";

    CDOView newView = session.openView();
    CDOQuery ocl = newView.createQuery("ocl", "EModelElement.allInstances()->select(e | " + scopeClause + "e.cdoMatches('.*bug416474.*'))",
        EcorePackage.Literals.EPACKAGE);
    ocl.setParameter("cdoImplicitRootClass", EcorePackage.Literals.EOBJECT);
    ocl.setParameter("folderPath", folderPath);

    List<?> results = ocl.getResult();
    assertEquals(true, results.contains(newView.getObject(root)));
    assertEquals(true, results.contains(newView.getObject(nested)));
    assertEquals(2, results.size());

    ocl = newView.createQuery("ocl", "EModelElement.allInstances()->select(e | " + scopeClause + "e.cdoMatches('.*Nested.?'))", EcorePackage.Literals.EPACKAGE);
    ocl.setParameter("cdoImplicitRootClass", EcorePackage.Literals.EOBJECT);
    ocl.setParameter("folderPath", folderPath);

    results = ocl.getResult();
    assertEquals(true, results.contains(newView.getObject(nested)));
    assertEquals(true, results.contains(newView.getObject(a1)));
    assertEquals(true, results.contains(newView.getObject(b1)));
    assertEquals(3, results.size());
  }

  /**
   * {@link #createUniquePackage()} would be better but {@link #testMatchesAnyStringAttribute()} relies on particular values.
   */
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
