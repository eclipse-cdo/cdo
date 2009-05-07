/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.CDOLegacyWrapper;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.UMLFactory;

/**
 * @author Eike Stepper
 */
public class UMLTest extends AbstractCDOTest
{
  public void testCommit() throws Exception
  {
    Class umlClass = UMLFactory.eINSTANCE.createClass();
    umlClass.setName("TestClass");
    umlClass.setIsAbstract(false);
    // TODO umlClass.setVisibility(VisibilityKind.PUBLIC_LITERAL);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    resource.getContents().add(umlClass);
    transaction.commit();

    CDOObject cdoClass = CDOUtil.getCDOObject(umlClass);
    assertEquals(false, CDOLegacyWrapper.isLegacyProxy(cdoClass));
    assertEquals(CDOState.CLEAN, cdoClass.cdoState());
    assertEquals(CDOState.CLEAN, resource.cdoState());
    session.close();
  }

  public void testLoad() throws Exception
  {
    {
      Class umlClass = UMLFactory.eINSTANCE.createClass();
      umlClass.setName("TestClass");
      umlClass.setIsAbstract(false);
      // TODO umlClass.setVisibility(VisibilityKind.PUBLIC_LITERAL);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/test1");
      resource.getContents().add(umlClass);
      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource("/test1");
    EList<EObject> contents = resource.getContents();

    Class umlClass = (Class)contents.get(0);
    CDOObject cdoClass = CDOUtil.getCDOObject(umlClass);
    assertEquals(false, CDOLegacyWrapper.isLegacyProxy(cdoClass));
    assertEquals(CDOState.CLEAN, cdoClass.cdoState());
    assertEquals(CDOState.CLEAN, resource.cdoState());

    String name = umlClass.getName();
    assertEquals("Mr. Hook", name);
    assertEquals(CDOState.CLEAN, cdoClass.cdoState());
    session.close();
  }
}
