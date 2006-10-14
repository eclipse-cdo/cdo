/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;


import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.change.ChangeDescription;
import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;

import java.util.Iterator;

import junit.framework.TestCase;


public class EMFDetachTest extends TestCase
{
  private EClass treeNodeClass;

  private EAttribute nameAttribute;

  private EReference childrenReference;

  private EPackage modelPackage;

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    treeNodeClass = EcoreFactory.eINSTANCE.createEClass();
    treeNodeClass.setName("TreeNode");

    nameAttribute = EcoreFactory.eINSTANCE.createEAttribute();
    nameAttribute.setName("nameAttribute");
    nameAttribute.setEType(EcorePackage.eINSTANCE.getEString());

    childrenReference = EcoreFactory.eINSTANCE.createEReference();
    childrenReference.setName("childrenReference");
    childrenReference.setEType(treeNodeClass);
    childrenReference.setContainment(true);
    childrenReference.setLowerBound(0);
    childrenReference.setUpperBound(-1);

    treeNodeClass.getEStructuralFeatures().add(nameAttribute);
    treeNodeClass.getEStructuralFeatures().add(childrenReference);

    modelPackage = EcoreFactory.eINSTANCE.createEPackage();
    modelPackage.setName("modelPackage");
    modelPackage.setNsPrefix("modelPackage");
    modelPackage.setNsURI("http://modelPackage");
    modelPackage.getEClassifiers().add(treeNodeClass);
  }

  @Override
  protected void tearDown() throws Exception
  {
    modelPackage = null;
    childrenReference = null;
    nameAttribute = null;
    treeNodeClass = null;
    super.tearDown();
  }

  public void testDetachSingle()
  {
    EObject root = createNode("root", null);
    for (int i = 0; i < 5; i++)
    {
      createNode("child" + i, root);
    }

    Resource resource = new ResourceImpl();
    resource.getContents().add(root);

    System.out.println();
    ChangeRecorder recorder = new ChangeRecorder(root);
    removeNode(root, 2);
    ChangeDescription changeDescription = recorder.endRecording();

    System.out.println();
    EList objectsToAttach = changeDescription.getObjectsToAttach();
    for (Iterator it = objectsToAttach.iterator(); it.hasNext();)
    {
      EObject node = (EObject) it.next();
      System.out.println("Node to attach: " + node.eGet(nameAttribute));

    }

    assertEquals(1, objectsToAttach.size());
  }

  private EObject createNode(String name, EObject parent)
  {
    EFactory factory = modelPackage.getEFactoryInstance();
    EObject node = factory.create(treeNodeClass);
    node.eSet(nameAttribute, name);

    if (parent != null)
    {
      EList children = (EList) parent.eGet(childrenReference);
      children.add(node);
    }

    System.out.println("Created node " + name);
    return node;
  }

  private void removeNode(EObject parent, int index)
  {
    EList children = (EList) parent.eGet(childrenReference);
    EObject child = (EObject) children.remove(index);
    System.out.println("Removed node " + child.eGet(nameAttribute));
  }
}
