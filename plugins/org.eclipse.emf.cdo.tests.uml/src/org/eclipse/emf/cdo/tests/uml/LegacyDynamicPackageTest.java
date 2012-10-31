/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.uml;

import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.ModelConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Tests legacy-mode support for registered dynamic packages.  This is not strictly a
 * UML-specific concern, but UML uses registered dynamic packages in profiles, so it
 * serves as a "control" test.
 */
@Requires(ModelConfig.CAPABILITY_LEGACY)
public class LegacyDynamicPackageTest extends AbstractCDOTest
{
  private final EPackage stuffPackage = EPackage.Registry.INSTANCE
      .getEPackage("http://www.eclipse.org/cdo/tests/schema/stuff/1.0");

  private final EFactory originalStuffFactory = stuffPackage.getEFactoryInstance();

  private final EClass thingClass = (EClass)stuffPackage.getEClassifier("Thing");

  private final EClass propertyClass = (EClass)stuffPackage.getEClassifier("Property");

  private final EClass worldClass = (EClass)stuffPackage.getEClassifier("World");

  private final EAttribute thingName = (EAttribute)thingClass.getEStructuralFeature("name");

  private final EReference thingProperties = (EReference)thingClass.getEStructuralFeature("properties");

  private final EAttribute propertyName = (EAttribute)propertyClass.getEStructuralFeature("name");

  private final EAttribute propertyIntrinsic = (EAttribute)propertyClass.getEStructuralFeature("intrinsic");

  private final EReference worldProperties = (EReference)worldClass.getEStructuralFeature("propertiesOfThings");

  private final EReference worldThings = (EReference)worldClass.getEStructuralFeature("things");

  //
  // Test cases
  //

  public void testInstancesOfRegisteredDynamicPackage() throws Exception
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(stuffPackage);

    // despite that it's registered, this is a dynamic package
    CDOPackageInfo info = session.getPackageRegistry().getPackageInfo(stuffPackage);
    CDOPackageUnit unit = info.getPackageUnit();
    assertEquals(CDOPackageUnit.Type.DYNAMIC, unit.getOriginalType());
    assertEquals(CDOPackageUnit.State.NEW, unit.getState());

    final EFactory stuffFactory = stuffPackage.getEFactoryInstance();

    EObject world = stuffFactory.create(worldClass);
    EObject colour = stuffFactory.create(propertyClass);
    colour.eSet(propertyName, "colour");
    colour.eSet(propertyIntrinsic, true);
    EObject apple = stuffFactory.create(thingClass);
    apple.eSet(thingName, "apple");
    list(apple, thingProperties).add(colour);
    list(world, worldProperties).add(colour);
    list(world, worldThings).add(apple);

    // keep an exact replica for later comparison, because 'world' will be owned by a transaction that has since closed
    final EObject oldWorld = EcoreUtil.copy(world);

    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/world1.stuff"));

    res.getContents().add(world);
    transaction.commit();

    // load the stuff again in a new session (hence new package registry)
    session.close();
    session = openSession();

    transaction = session.openTransaction();
    res = transaction.getResource(getResourcePath("/world1.stuff"), true);

    assertEquals(true, res.isLoaded());
    assertEquals(1, res.getContents().size());

    EObject newWorld = res.getContents().get(0);
    EPackage newPackage = newWorld.eClass().getEPackage();
    assertSame(stuffPackage, newPackage);

    // now the package is in loaded state
    info = session.getPackageRegistry().getPackageInfo(stuffPackage);
    unit = info.getPackageUnit();
    assertEquals(CDOPackageUnit.Type.DYNAMIC, unit.getOriginalType());
    assertEquals(CDOPackageUnit.State.LOADED, unit.getState());

    // we correctly and completely loaded the model
    assertEquals(true, EcoreUtil.equals(oldWorld, newWorld));
  }

  //
  // Test framework
  //

  @Override
  protected void doTearDown() throws Exception
  {
    // restore the factory replaced by CDO
    stuffPackage.setEFactoryInstance(originalStuffFactory);

    super.doTearDown();
  }

  @SuppressWarnings("unchecked")
  private <T> EList<T> list(EObject owner, EStructuralFeature feature)
  {
    return (EList<T>)owner.eGet(feature);
  }
}
