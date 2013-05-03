/*
 * Copyright (c) 2009-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Stefan Winkler - enhanced test cases
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model5.Doctor;
import org.eclipse.emf.cdo.tests.model5.TestFeatureMap;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;

import java.util.List;

/**
 * @author Simon McDuff
 */
public class FeatureMapTest extends AbstractCDOTest
{
  private EPackage pkg;

  private EClass dummy;

  private EAttribute name;

  private EClass fmapContainer;

  private EAttribute string1;

  private EAttribute integer;

  private EAttribute fMap;

  private EAttribute string2;

  private EAttribute longObj;

  private EAttribute bool;

  private EReference ref1;

  private EReference ref2;

  private CDOSession session;

  private CDOTransaction tx;

  private CDOResource resource;

  private EObject dummyObj;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    final EcorePackage epackage = EcorePackage.eINSTANCE;
    pkg = EMFUtil.createEPackage("fmaps", "fmaps", "http://cdo.emf.eclipse.org/fmaps");
    dummy = EMFUtil.createEClass(pkg, "Dummy", false, false);
    name = EMFUtil.createEAttribute(dummy, "name", epackage.getEString());
    fmapContainer = EMFUtil.createEClass(pkg, "FMapContainer", false, false);

    string1 = EMFUtil.createEAttribute(fmapContainer, "string1", epackage.getEString());
    string1.setUpperBound(-1);

    integer = EMFUtil.createEAttribute(fmapContainer, "integer", epackage.getEInt());
    integer.setUpperBound(-1);

    fMap = EMFUtil.createEAttribute(fmapContainer, "fmap", epackage.getEFeatureMapEntry());
    fMap.setUpperBound(-1);

    string2 = EMFUtil.createEAttribute(fmapContainer, "string2", epackage.getEString());
    string2.setUpperBound(-1);

    longObj = EMFUtil.createEAttribute(fmapContainer, "longObj", epackage.getELongObject());
    longObj.setUpperBound(-1);

    bool = EMFUtil.createEAttribute(fmapContainer, "bool", epackage.getEBoolean());

    ref1 = EMFUtil.createEReference(fmapContainer, "ref1", dummy, true, true);
    ref1.setUpperBound(-1);

    ref2 = EMFUtil.createEReference(fmapContainer, "ref2", dummy, true, false);

    ExtendedMetaData.INSTANCE.setFeatureKind(fMap, ExtendedMetaData.GROUP_FEATURE);

    ExtendedMetaData.INSTANCE.setGroup(string1, fMap);
    ExtendedMetaData.INSTANCE.setGroup(string2, fMap);
    ExtendedMetaData.INSTANCE.setGroup(integer, fMap);
    ExtendedMetaData.INSTANCE.setGroup(longObj, fMap);
    ExtendedMetaData.INSTANCE.setGroup(bool, fMap);
    ExtendedMetaData.INSTANCE.setGroup(ref1, fMap);
    ExtendedMetaData.INSTANCE.setGroup(ref2, fMap);
    if (!isConfig(LEGACY))
    {
      CDOUtil.prepareDynamicEPackage(pkg);
    }

    dummyObj = pkg.getEFactoryInstance().create(dummy);
    dummyObj.eSet(name, "DummyName");

    session = openSession();
    tx = session.openTransaction();
    resource = tx.getOrCreateResource(getResourcePath("/test/" + System.currentTimeMillis()));
    session.getPackageRegistry().putEPackage(pkg);
    resource.getContents().add(dummyObj);
    tx.commit();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    pkg = null;
    dummy = null;
    name = null;
    fmapContainer = null;
    string1 = null;
    integer = null;
    fMap = null;
    string2 = null;
    longObj = null;
    bool = null;
    ref1 = null;
    ref2 = null;
    session = null;
    tx = null;
    resource = null;
    dummyObj = null;
    super.doTearDown();
  }

  public void _testAddDifferentTypes() throws CommitException
  {
    EObject fMapObj = EcoreUtil.create(fmapContainer);

    FeatureMap fm = (FeatureMap)fMapObj.eGet(fMap);
    fm.add(string1, "Hello");
    fm.add(string2, "lorem ipsum dolor sit amet");
    fm.add(string1, "World");
    fm.add(integer, 5);
    fm.add(bool, true);
    fm.add(ref1, dummyObj);
    fm.add(longObj, 12345678901234567L);
    fm.add(ref2, dummyObj);
    fm.add(bool, false); // Overwrites bool=true
    fm.add(ref2, null); // Overwrites ref2=dummyObj

    assertEquals(8, fm.size());

    resource.getContents().add(fMapObj);
    tx.commit();

    purgeCaches();

    EObject fMapObjDb = resource.getContents().get(1);
    FeatureMap fmDb = (FeatureMap)fMapObjDb.eGet(fMap);
    EObject dummyDb = resource.getContents().get(0);

    assertEquals(8, fmDb.size());
    assertEquals("Hello", fmDb.getValue(0));
    assertEquals(string1, fmDb.getEStructuralFeature(0));

    assertEquals("lorem ipsum dolor sit amet", fmDb.getValue(1));
    assertEquals(string2, fmDb.getEStructuralFeature(1));

    assertEquals("World", fmDb.getValue(2));
    assertEquals(string1, fmDb.getEStructuralFeature(2));

    assertEquals(5, fmDb.getValue(3));
    assertEquals(integer, fmDb.getEStructuralFeature(3));

    assertEquals(false, (boolean)(Boolean)fmDb.getValue(4));
    assertEquals(bool, fmDb.getEStructuralFeature(4));

    assertEquals(dummyDb, fmDb.getValue(5));
    assertEquals(ref1, fmDb.getEStructuralFeature(5));

    assertEquals(12345678901234567L, fmDb.getValue(6));
    assertEquals(longObj, fmDb.getEStructuralFeature(6));

    assertNull(fmDb.getValue(7));
    assertEquals(ref2, fmDb.getEStructuralFeature(7));
  }

  @Skips("Postgresql")
  public void testFeatureMaps() throws Exception
  {
    skipStoreWithoutFeatureMaps();

    EReference feature = getModel5Package().getTestFeatureMap_Doctors();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    TestFeatureMap featureMap = getModel5Factory().createTestFeatureMap();
    FeatureMap people = featureMap.getPeople();

    Doctor doctor1 = getModel5Factory().createDoctor();
    Doctor doctor2 = getModel5Factory().createDoctor();

    resource.getContents().add(doctor1);
    resource.getContents().add(doctor2);

    people.add(feature, doctor1);
    people.add(feature, doctor2);

    int featureMapIndex = resource.getContents().size();
    resource.getContents().add(featureMap);

    assertEquals(doctor1, people.get(0).getValue());
    List<?> doctors = (List<?>)people.get(feature, true);
    assertEquals(doctor1, doctors.get(0));
    assertEquals(doctor2, doctors.get(1));
    transaction.commit();

    clearCache(getRepository().getRevisionManager());

    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getResource(getResourcePath("/res1"));

    featureMap = (TestFeatureMap)resource.getContents().get(featureMapIndex);
    doctors = (List<?>)featureMap.getPeople().get(feature, true);
    assertInstanceOf(Doctor.class, doctors.get(0));
    assertInstanceOf(Doctor.class, doctors.get(1));
  }

  private void purgeCaches()
  {
    // according to Eike's comment at Bug 249681, client caches are
    // ignored, if a new session is opened.
    // server caches are wiped by the clearCache call.

    String path = resource.getPath();

    tx.close();
    session.close();

    clearCache(getRepository().getRevisionManager());

    session = openSession();
    session.getPackageRegistry().putEPackage(pkg);

    tx = session.openTransaction();

    resource = tx.getResource(getResourcePath(path));
  }
}
