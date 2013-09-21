/*
 * Copyright (c) 2010-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests.common;

import org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot;
import org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage;
import org.eclipse.emf.cdo.dawn.resources.DawnWrapperResource;
import org.eclipse.emf.cdo.dawn.tests.AbstractDawnTest;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.gmf.runtime.notation.Diagram;

/**
 * This test tests the test framework itself.
 * 
 * @author Martin Fluegge
 */
public class TestFrameworkTest extends AbstractDawnTest
{
  public void testResourceCreation() throws CommitException
  {
    CDOSession session = openSession();

    createCDOResourcesFromXMI("simple", AcorePackage.eINSTANCE, session);

    ResourceSet resourceSet = createResourceSet();
    CDOTransaction transaction = session.openTransaction(resourceSet);

    CDOResource semanticResource = transaction.getResource("/simple.acore");

    assertInstanceOf(CDOResource.class, semanticResource);
    DawnWrapperResource notationalResource = (DawnWrapperResource)resourceSet.getResource(
        URI.createURI("dawn://repo1/simple.acore_diagram"), true);// container.getNotationalResource();
    assertInstanceOf(DawnWrapperResource.class, notationalResource);
    ACoreRoot acoreRoot = (ACoreRoot)semanticResource.getContents().get(0);

    assertEquals(3, acoreRoot.getClasses().size());
    assertEquals("A", acoreRoot.getClasses().get(0).getName());
    assertEquals("B", acoreRoot.getClasses().get(1).getName());
    assertEquals("C", acoreRoot.getClasses().get(2).getName());

    Diagram diagram = (Diagram)notationalResource.getContents().get(0);
    assertEquals(3, diagram.getChildren().size());
    assertEquals(2, diagram.getEdges().size());
  }
}
