/*
 * Copyright (c) 2004 - 2012 Esteban Dugueperoux (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model3.Model3Factory;
import org.eclipse.emf.cdo.tests.model3.NodeA;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * @author Esteban Dugueperoux
 */
public class Bugzilla_400236_Test extends AbstractCDOTest
{
  public void testCommit() throws Exception
  {

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resourceA = transaction.createResource(getResourcePath("test1"));
    CDOResource resourceB = transaction.createResource(getResourcePath("test2"));

    NodeA nodeA1 = Model3Factory.eINSTANCE.createNodeA(); // Use native object!
    nodeA1.setName("nodeA1");

    NodeA nodeA2 = getModel3Factory().createNodeA();
    nodeA2.setName("nodeA2");

    nodeA1.getOtherNodes().add(nodeA2);

    resourceA.getContents().add(nodeA1);
    resourceB.getContents().add(nodeA2);
    transaction.commit();
    session.close();

    session = openSession();
    transaction = session.openTransaction();
    resourceA = transaction.getResource(getResourcePath("test1"));
    resourceB = transaction.getResource(getResourcePath("test2"));
    nodeA1 = (NodeA)resourceA.getContents().get(0);
    nodeA2 = (NodeA)resourceB.getContents().get(0);

    NodeA nodeA3 = Model3Factory.eINSTANCE.createNodeA(); // Use native object!
    nodeA3.setName("nodeA3");

    NodeA nodeA4 = getModel3Factory().createNodeA();
    nodeA4.setName("nodeA4");

    nodeA2.getChildren().add(nodeA4);
    nodeA3.getOtherNodes().add(nodeA4);
    nodeA1.getChildren().add(nodeA3);

    nodeA4 = nodeA3.getOtherNodes().get(0);
  }
}
