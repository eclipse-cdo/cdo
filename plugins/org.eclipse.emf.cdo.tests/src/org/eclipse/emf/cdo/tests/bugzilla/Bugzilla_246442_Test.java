/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;

import org.eclipse.emf.internal.cdo.CDOSessionPackageManagerImpl;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * ArrayIndexOutOfBoundsException when importing resoruces.
 * <p>
 * 
 * @see https://bugs.eclipse.org/246442
 * @author Simon McDuff
 */
public class Bugzilla_246442_Test extends AbstractCDOTest
{
  public void testBugzilla_246442() throws Exception
  {
    CDOID lookupObject = null;

    {
      EPackage topPackage = createDynamicEPackage();

      EPackage subpackage1 = topPackage.getESubpackages().get(0);
      EClass class1Class = (EClass)subpackage1.getEClassifier("class1");

      CDOSession session = openSession();

      session.getPackageRegistry().putEPackage(topPackage);
      CDOPackage packageObject = ModelUtil.getCDOPackage(topPackage, (CDOSessionPackageManagerImpl)session
          .getPackageManager());
      assertNotNull(packageObject.getEcore());

      CDOTransaction transaction = session.openTransaction();
      CDOObject instance = (CDOObject)subpackage1.getEFactoryInstance().create(class1Class);
      CDOResource resource = transaction.createResource("/test1");
      resource.getContents().add(instance);
      transaction.commit();

      lookupObject = instance.cdoID();
      session.close();
    }

    CDOSession session = openSession();
    session.getProtocol().setTimeout(2000L);

    CDOTransaction transaction = session.openTransaction();
    transaction.getObject(lookupObject);
  }

  public void testBugzilla_246442_lookupEClass() throws Exception
  {
    EPackage topPackage = createDynamicEPackage();

    EPackage subpackage1 = topPackage.getESubpackages().get(0);
    EClass class1Class = (EClass)subpackage1.getEClassifier("class1");
    EClass class2Class = (EClass)subpackage1.getEClassifier("class2");
    EClass class3Class = (EClass)subpackage1.getEClassifier("class3");

    CDOSession session = openSession();

    session.getPackageRegistry().putEPackage(topPackage);

    CDOClass cdoClass1 = ModelUtil.getCDOClass(class1Class, (CDOSessionPackageManagerImpl)session.getPackageManager());
    CDOClass cdoClass2 = ModelUtil.getCDOClass(class2Class, (CDOSessionPackageManagerImpl)session.getPackageManager());
    CDOClass cdoClass3 = ModelUtil.getCDOClass(class3Class, (CDOSessionPackageManagerImpl)session.getPackageManager());

    assertEquals(class1Class.getName(), cdoClass1.getName());
    assertEquals(class2Class.getName(), cdoClass2.getName());
    assertEquals(class3Class.getName(), cdoClass3.getName());
  }

  private EPackage createDynamicEPackage()
  {
    final EcoreFactory efactory = EcoreFactory.eINSTANCE;
    final EcorePackage epackage = EcorePackage.eINSTANCE;

    // Create a new EPackage and add the new EClasses
    EPackage topPackage = efactory.createEPackage();
    topPackage.setName("toppackage");
    topPackage.setNsPrefix("toppackage");
    topPackage.setNsURI("http:///www.elver.org/toppackage");

    EPackage subPackage1 = efactory.createEPackage();
    subPackage1.setName("subPackage1");
    subPackage1.setNsPrefix("subPackage1");
    subPackage1.setNsURI("http:///www.elver.org/subPackage1");

    {
      EClass schoolBookEClass = efactory.createEClass();
      schoolBookEClass.setName("class1");
      // create a new attribute for this EClass
      EAttribute level = efactory.createEAttribute();
      level.setName("level");
      level.setEType(epackage.getEInt());
      schoolBookEClass.getEStructuralFeatures().add(level);
      subPackage1.getEClassifiers().add(schoolBookEClass);
    }

    {
      EEnum schoolBookEClass = efactory.createEEnum();
      schoolBookEClass.setName("enum");
      subPackage1.getEClassifiers().add(schoolBookEClass);
    }

    {
      EClass schoolBookEClass = efactory.createEClass();
      schoolBookEClass.setName("class2");
      // create a new attribute for this EClass
      subPackage1.getEClassifiers().add(schoolBookEClass);
    }

    {
      EClass schoolBookEClass = efactory.createEClass();
      schoolBookEClass.setName("class3");
      // create a new attribute for this EClass
      EAttribute level = efactory.createEAttribute();
      level.setName("level");
      level.setEType(epackage.getEInt());
      schoolBookEClass.getEStructuralFeatures().add(level);
      subPackage1.getEClassifiers().add(schoolBookEClass);
    }

    EPackage subPackage2 = efactory.createEPackage();
    subPackage2.setName("subPackage2");
    subPackage2.setNsPrefix("subPackage2");
    subPackage2.setNsURI("http:///www.elver.org/subPackage2");

    topPackage.getESubpackages().add(subPackage1);
    return topPackage;
  }
}
