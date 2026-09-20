/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOFeatureRef;
import org.eclipse.emf.cdo.common.model.CDOModelElementRef;
import org.eclipse.emf.cdo.common.model.CDOPackageRef;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.common.util.CDOPackageNotFoundException;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * Tests the common symbolic references for EMF model elements.
 *
 * @author Eike Stepper
 */
public class ModelElementRefTest extends TestCase
{
  private static final String PACKAGE_URI = "http://www.example.org/model-element-ref"; //$NON-NLS-1$

  public void testPackageRef()
  {
    EPackage ePackage = createPackage();
    CDOPackageRef fromPackage = new CDOPackageRef(ePackage);
    CDOPackageRef fromURI = new CDOPackageRef(PACKAGE_URI);

    assertEquals(PACKAGE_URI, fromPackage.getPackageURI());
    assertEquals(fromURI, fromPackage);
    assertEquals(fromURI.hashCode(), fromPackage.hashCode());
    assertFalse(fromURI.equals(new CDOPackageRef(PACKAGE_URI + ".other"))); //$NON-NLS-1$
    assertSame(ePackage, fromPackage.resolve(registry(ePackage)));
  }

  public void testClassifierRefIntegration()
  {
    EClass eClass = createClass(createPackage(), "Class"); //$NON-NLS-1$
    CDOClassifierRef classifierRef = new CDOClassifierRef(eClass);
    CDOModelElementRef<EClassifier> modelElementRef = classifierRef;

    assertSame(classifierRef, modelElementRef);
    assertEquals(new CDOClassifierRef(PACKAGE_URI, "Class"), classifierRef); //$NON-NLS-1$
    assertEquals(classifierRef.hashCode(), new CDOClassifierRef(PACKAGE_URI, "Class").hashCode()); //$NON-NLS-1$
    assertEquals(eClass, classifierRef.resolve(registry(eClass.getEPackage())));
  }

  public void testFeatureRefUsesDeclaringClass()
  {
    EPackage ePackage = createPackage();
    EClass eClass = createClass(ePackage, "Class"); //$NON-NLS-1$
    EStructuralFeature feature = createFeature(eClass, "feature"); //$NON-NLS-1$
    CDOFeatureRef featureRef = new CDOFeatureRef(feature);

    assertEquals(new CDOClassifierRef(eClass), featureRef.getClassifierRef());
    assertEquals("feature", featureRef.getFeatureName()); //$NON-NLS-1$
    assertEquals(featureRef, new CDOFeatureRef(new CDOClassifierRef(eClass), "feature")); //$NON-NLS-1$
    assertEquals(featureRef.hashCode(), new CDOFeatureRef(new CDOClassifierRef(eClass), "feature").hashCode()); //$NON-NLS-1$
    assertFalse(featureRef.equals(new CDOFeatureRef(new CDOClassifierRef(eClass), "other"))); //$NON-NLS-1$
  }

  public void testInheritedFeatureUsesDeclaringClass()
  {
    EPackage ePackage = createPackage();
    EClass superClass = createClass(ePackage, "Super"); //$NON-NLS-1$
    EClass subClass = createClass(ePackage, "Sub"); //$NON-NLS-1$
    subClass.getESuperTypes().add(superClass);
    EStructuralFeature feature = createFeature(superClass, "inherited"); //$NON-NLS-1$

    CDOFeatureRef featureRef = new CDOFeatureRef(subClass.getEStructuralFeature(feature.getName()));

    assertEquals(new CDOClassifierRef(superClass), featureRef.getClassifierRef());
    assertEquals(feature, featureRef.resolve(registry(ePackage)));
  }

  public void testMissingElements()
  {
    EPackage ePackage = createPackage();
    EClass eClass = createClass(ePackage, "Class"); //$NON-NLS-1$
    EPackage.Registry registry = registry(ePackage);

    try
    {
      new CDOPackageRef("missing").resolve(registry); //$NON-NLS-1$
      fail("Missing package was resolved"); //$NON-NLS-1$
    }
    catch (CDOPackageNotFoundException expected)
    {
      // Expected.
    }

    assertNull(new CDOClassifierRef(PACKAGE_URI, "Missing").resolve(registry)); //$NON-NLS-1$
    assertNull(new CDOFeatureRef(new CDOClassifierRef(eClass), "Missing").resolve(registry)); //$NON-NLS-1$
    EDataType dataType = EcoreFactory.eINSTANCE.createEDataType();
    dataType.setName("DataType"); //$NON-NLS-1$
    ePackage.getEClassifiers().add(dataType);
    assertNull(new CDOFeatureRef(new CDOClassifierRef(dataType), "feature").resolve(registry)); //$NON-NLS-1$
  }

  public void testSerializationRoundTripAndLazyResolution() throws IOException
  {
    EPackage ePackage = createPackage();
    EClass eClass = createClass(ePackage, "Class"); //$NON-NLS-1$
    EStructuralFeature feature = createFeature(eClass, "feature"); //$NON-NLS-1$
    CDOPackageRef packageRef = new CDOPackageRef(ePackage);
    CDOFeatureRef featureRef = new CDOFeatureRef(feature);
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    ExtendedDataOutputStream outputStream = new ExtendedDataOutputStream(bytes);
    CDODataOutput out = CDOCommonUtil.createCDODataOutput(outputStream, null, null, null);
    packageRef.write(out);
    featureRef.write(out);
    outputStream.flush();

    RecordingRegistry registry = new RecordingRegistry(ePackage);
    CDODataInput in = CDOCommonUtil.createCDODataInput(new ExtendedDataInputStream(new ByteArrayInputStream(bytes.toByteArray())), null,
        null, null, null, null, null);
    CDOPackageRef readPackageRef = new CDOPackageRef(in);
    CDOFeatureRef readFeatureRef = new CDOFeatureRef(in);

    assertEquals(packageRef, readPackageRef);
    assertEquals(featureRef, readFeatureRef);
    assertEquals(0, registry.lookupCount);
    readPackageRef.hashCode();
    readFeatureRef.hashCode();
    assertEquals(0, registry.lookupCount);
    assertEquals(feature, readFeatureRef.resolve(registry));
    assertEquals(1, registry.lookupCount);
  }

  private static EPackage createPackage()
  {
    EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
    ePackage.setNsURI(PACKAGE_URI);
    return ePackage;
  }

  private static EClass createClass(EPackage ePackage, String name)
  {
    EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName(name);
    ePackage.getEClassifiers().add(eClass);
    return eClass;
  }

  private static EStructuralFeature createFeature(EClass eClass, String name)
  {
    EStructuralFeature feature = EcoreFactory.eINSTANCE.createEAttribute();
    feature.setName(name);
    feature.setEType(EcoreFactory.eINSTANCE.createEDataType());
    eClass.getEStructuralFeatures().add(feature);
    return feature;
  }

  private static EPackage.Registry registry(EPackage ePackage)
  {
    return new RecordingRegistry(ePackage);
  }

  private static final class RecordingRegistry extends EPackageRegistryImpl
  {
    private static final long serialVersionUID = 1L;

    private final EPackage ePackage;

    private int lookupCount;

    private RecordingRegistry(EPackage ePackage)
    {
      this.ePackage = ePackage;
    }

    @Override
    public EPackage getEPackage(String nsURI)
    {
      ++lookupCount;
      return ePackage.getNsURI().equals(nsURI) ? ePackage : null;
    }
  }
}
