/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @author Stefan Winkler
 */
public class Bugzilla_350137_Test extends AbstractCDOTest
{
  /**
   * Just check if an EChar attribute can be stored to the database per default. (PostgreSQL has problems with this).
   */
  public void testDefault() throws Exception
  {
    EPackage pkg = EMFUtil.createEPackage("Test", "t", "http://cdo.eclipse.org/tests/Bugzilla350137_Test1.ecore");
    EClass cls = EMFUtil.createEClass(pkg, "foo", false, false);

    @SuppressWarnings("unused")
    EAttribute att = EMFUtil.createEAttribute(cls, "bar", EcorePackage.eINSTANCE.getEChar());

    if (!isConfig(LEGACY))
    {
      CDOUtil.prepareDynamicEPackage(pkg);
    }

    EObject obj = EcoreUtil.create(cls);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test"));

    resource.getContents().add(obj);
    transaction.commit();
    transaction.close();
    session.close();
  }

  /**
   * Check if an EChar attribute with explicit zero value can be stored to the database per default. (PostgreSQL has
   * problems with this).
   */
  public void testExplicitZero() throws Exception
  {
    EPackage pkg = EMFUtil.createEPackage("Test", "t", "http://cdo.eclipse.org/tests/Bugzilla350137_Test2.ecore");
    EClass cls = EMFUtil.createEClass(pkg, "foo2", false, false);

    EAttribute att = EMFUtil.createEAttribute(cls, "bar", EcorePackage.eINSTANCE.getEChar());

    if (!isConfig(LEGACY))
    {
      CDOUtil.prepareDynamicEPackage(pkg);
    }

    EObject obj = EcoreUtil.create(cls);
    obj.eSet(att, '\u0000');

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test"));

    resource.getContents().add(obj);
    transaction.commit();
    transaction.close();
    session.close();
  }
}
