/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOFeatureRef;
import org.eclipse.emf.cdo.common.model.CDOPackageRef;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOCollectionLoadingConfig;
import org.eclipse.emf.cdo.common.revision.CDOCollectionLoadingConfig.ChunkConfig;
import org.eclipse.emf.cdo.common.revision.CDOCollectionLoadingConfigResolver;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.emf.internal.cdo.session.CDOSessionImpl;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tests the declarative collection-loading configuration foundation.
 */
public class CollectionLoadingConfigTest extends AbstractCDOTest
{
  public void testChunkConfigSemantics()
  {
    assertEquals(-2, ChunkConfig.INHERIT);
    assertEquals(-1, ChunkConfig.ALL);
    assertEquals(0, ChunkConfig.NONE);
    assertEquals(new ChunkConfig(ChunkConfig.NONE, ChunkConfig.INHERIT), new ChunkConfig(0, -2));
    assertEquals(new ChunkConfig(3, 7).hashCode(), new ChunkConfig(3, 7).hashCode());

    try
    {
      new ChunkConfig(-3, 1);
      fail("Expected invalid chunk size");
    }
    catch (IllegalArgumentException expected)
    {
      // Expected.
    }
  }

  public void testConfigurationSnapshotAndKeys()
  {
    EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
    ePackage.setNsURI("test://collection-loading");
    EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName("Type");
    ePackage.getEClassifiers().add(eClass);
    EStructuralFeature feature = EcoreFactory.eINSTANCE.createEAttribute();
    feature.setName("values");
    eClass.getEStructuralFeatures().add(feature);

    Map<EModelElement, ChunkConfig> overrides = new LinkedHashMap<>();
    overrides.put(ePackage, new ChunkConfig(1, 2));
    overrides.put(eClass, new ChunkConfig(3, 4));
    overrides.put(feature, new ChunkConfig(5, 6));

    CDOCollectionLoadingConfig config = new CDOCollectionLoadingConfig(new ChunkConfig(7, 8), overrides);
    overrides.clear();

    assertNotNull(config.getDefaultChunkConfig());
    assertEquals(3, config.getOverrides().size());
    assertEquals(new ChunkConfig(5, 6), config.get(feature));
    assertEquals(new ChunkConfig(1, 2), config.get(new CDOPackageRef(ePackage)));
    assertEquals(new ChunkConfig(3, 4), config.get(new CDOClassifierRef(eClass)));
    assertEquals(new ChunkConfig(5, 6), config.get(new CDOFeatureRef(feature)));

    EPackage equivalentPackage = EcoreFactory.eINSTANCE.createEPackage();
    equivalentPackage.setNsURI(ePackage.getNsURI());
    EClass equivalentClass = EcoreFactory.eINSTANCE.createEClass();
    equivalentClass.setName(eClass.getName());
    equivalentPackage.getEClassifiers().add(equivalentClass);
    EStructuralFeature equivalentFeature = EcoreFactory.eINSTANCE.createEAttribute();
    equivalentFeature.setName(feature.getName());
    equivalentClass.getEStructuralFeatures().add(equivalentFeature);
    assertEquals(new ChunkConfig(1, 2), config.get(equivalentPackage));
    assertEquals(new ChunkConfig(3, 4), config.get(equivalentClass));
    assertEquals(new ChunkConfig(5, 6), config.get(equivalentFeature));

    try
    {
      config.getOverrides().clear();
      fail("Expected immutable overrides");
    }
    catch (UnsupportedOperationException expected)
    {
      // Expected.
    }

    EAnnotation invalidKey = EcoreFactory.eINSTANCE.createEAnnotation();
    try
    {
      new CDOCollectionLoadingConfig(null, Collections.singletonMap(invalidKey, new ChunkConfig(1, 1)));
      fail("Expected invalid override key");
    }
    catch (IllegalArgumentException expected)
    {
      // Expected.
    }
  }

  public void testSymbolicConfigurationRoundTripWithoutResolution() throws IOException
  {
    EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
    ePackage.setNsURI("test://symbolic-collection-loading");
    EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName("Type");
    ePackage.getEClassifiers().add(eClass);
    EStructuralFeature feature = EcoreFactory.eINSTANCE.createEAttribute();
    feature.setName("values");
    eClass.getEStructuralFeatures().add(feature);

    Map<Object, ChunkConfig> overrides = new LinkedHashMap<>();
    overrides.put(new CDOPackageRef(ePackage), new ChunkConfig(1, ChunkConfig.INHERIT));
    overrides.put(new CDOClassifierRef(eClass), new ChunkConfig(ChunkConfig.INHERIT, 2));
    overrides.put(new CDOFeatureRef(feature), new ChunkConfig(3, 4));
    CDOCollectionLoadingConfig config = new CDOCollectionLoadingConfig(new ChunkConfig(5, 6), overrides);

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    ExtendedDataOutputStream outputStream = new ExtendedDataOutputStream(bytes);
    CDODataOutput out = CDOCommonUtil.createCDODataOutput(outputStream, null, null, null);
    config.write(out);
    outputStream.flush();

    CDODataInput in = CDOCommonUtil.createCDODataInput(new ExtendedDataInputStream(new ByteArrayInputStream(bytes.toByteArray())), null, null, null, null, null,
        null);
    CDOCollectionLoadingConfig read = CDOCollectionLoadingConfig.read(in);
    assertEquals(config, read);
    assertEquals(new ChunkConfig(1, ChunkConfig.INHERIT), read.get(new CDOPackageRef(ePackage)));
    assertEquals(new ChunkConfig(ChunkConfig.INHERIT, 2), read.get(new CDOClassifierRef(eClass)));
    assertEquals(new ChunkConfig(3, 4), read.get(new CDOFeatureRef(feature)));

    CDOCollectionLoadingConfig missing = new CDOCollectionLoadingConfig(null,
        Collections.singletonMap(new CDOPackageRef("test://missing-collection-loading"), new ChunkConfig(1, 2)));
    bytes.reset();
    outputStream = new ExtendedDataOutputStream(bytes);
    missing.write(CDOCommonUtil.createCDODataOutput(outputStream, null, null, null));
    outputStream.flush();
    in = CDOCommonUtil.createCDODataInput(new ExtendedDataInputStream(new ByteArrayInputStream(bytes.toByteArray())), null, null, null, null, null, null);
    assertEquals(missing, CDOCollectionLoadingConfig.read(in));
  }

  public void testAnnotationParsing()
  {
    EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
    annotation.setSource(EMFUtil.CDO_ANNOTATION_SOURCE);
    annotation.getDetails().put(EMFUtil.CDO_ANNOTATION_KEY_INITIAL_CHUNK_SIZE, "0");
    eClass.getEAnnotations().add(annotation);

    ChunkConfig config = EMFUtil.getCollectionLoadingChunkConfig(eClass);
    assertNotNull(config);
    assertEquals(ChunkConfig.NONE, config.getInitialChunkSize());
    assertEquals(ChunkConfig.INHERIT, config.getResolveChunkSize());

    annotation.getDetails().put(EMFUtil.CDO_ANNOTATION_KEY_INITIAL_CHUNK_SIZE, "invalid");
    annotation.getDetails().put(EMFUtil.CDO_ANNOTATION_KEY_RESOLVE_CHUNK_SIZE, "-1");
    config = EMFUtil.getCollectionLoadingChunkConfig(eClass);
    assertNotNull(config);
    assertEquals(ChunkConfig.INHERIT, config.getInitialChunkSize());
    assertEquals(ChunkConfig.ALL, config.getResolveChunkSize());

    annotation.getDetails().put(EMFUtil.CDO_ANNOTATION_KEY_RESOLVE_CHUNK_SIZE, "-3");
    assertNull(EMFUtil.getCollectionLoadingChunkConfig(eClass));
  }

  public void testEffectiveConfigurationResolution()
  {
    EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
    ePackage.setNsURI("test://effective-collection-loading");

    EClass superClass = EcoreFactory.eINSTANCE.createEClass();
    superClass.setName("SuperType");
    ePackage.getEClassifiers().add(superClass);

    EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName("Type");
    eClass.getESuperTypes().add(superClass);
    ePackage.getEClassifiers().add(eClass);

    EClass subClass = EcoreFactory.eINSTANCE.createEClass();
    subClass.setName("SubType");
    subClass.getESuperTypes().add(eClass);
    ePackage.getEClassifiers().add(subClass);

    EStructuralFeature feature = EcoreFactory.eINSTANCE.createEAttribute();
    feature.setName("values");
    eClass.getEStructuralFeatures().add(feature);

    EPackage fallbackPackage = EcoreFactory.eINSTANCE.createEPackage();
    fallbackPackage.setNsURI("test://effective-collection-loading-fallback");
    EClass fallbackClass = EcoreFactory.eINSTANCE.createEClass();
    fallbackClass.setName("FallbackType");
    fallbackPackage.getEClassifiers().add(fallbackClass);
    EStructuralFeature fallbackFeature = EcoreFactory.eINSTANCE.createEAttribute();
    fallbackFeature.setName("values");
    fallbackClass.getEStructuralFeatures().add(fallbackFeature);

    EAnnotation packageAnnotation = createAnnotation(11, ChunkConfig.INHERIT);
    EAnnotation classAnnotation = createAnnotation(12, 120);
    EAnnotation featureAnnotation = createAnnotation(13, 130);
    ePackage.getEAnnotations().add(packageAnnotation);
    eClass.getEAnnotations().add(classAnnotation);
    feature.getEAnnotations().add(featureAnnotation);

    try (CDOSession session = openSession())
    {
      session.getPackageRegistry().putEPackage(ePackage);
      session.getPackageRegistry().putEPackage(fallbackPackage);
      getRepository().getPackageRegistry().putEPackage(ePackage);
      getRepository().getPackageRegistry().putEPackage(fallbackPackage);
      InternalSession serverSession = serverSession(session);

      getRepository().setCollectionLoadingConfig(new CDOCollectionLoadingConfig(new ChunkConfig(21, 22), Collections.emptyMap()));
      assertNull(getRepository().resolveCollectionLoadingConfig(serverSession, feature));

      serverSession.setCollectionLoadingConfig(new CDOCollectionLoadingConfig(null, Collections.emptyMap()));
      assertEquals(new ChunkConfig(13, 130), getRepository().resolveCollectionLoadingConfig(serverSession, feature));

      Map<EModelElement, ChunkConfig> repositoryOverrides = new LinkedHashMap<>();
      repositoryOverrides.put(ePackage, new ChunkConfig(31, 32));
      repositoryOverrides.put(eClass, new ChunkConfig(ChunkConfig.INHERIT, 33));
      repositoryOverrides.put(feature, new ChunkConfig(ChunkConfig.NONE, ChunkConfig.INHERIT));
      getRepository().setCollectionLoadingConfig(new CDOCollectionLoadingConfig(new ChunkConfig(41, 42), repositoryOverrides));

      Map<EModelElement, ChunkConfig> sessionOverrides = new LinkedHashMap<>();
      sessionOverrides.put(ePackage, new ChunkConfig(51, ChunkConfig.INHERIT));
      serverSession.setCollectionLoadingConfig(new CDOCollectionLoadingConfig(new ChunkConfig(61, 62), sessionOverrides));
      assertEquals(new ChunkConfig(51, 33), getRepository().resolveCollectionLoadingConfig(serverSession, feature));

      sessionOverrides.clear();
      sessionOverrides.put(feature, new ChunkConfig(ChunkConfig.NONE, ChunkConfig.ALL));
      sessionOverrides.put(eClass, new ChunkConfig(71, 72));
      serverSession.setCollectionLoadingConfig(new CDOCollectionLoadingConfig(null, sessionOverrides));
      assertEquals(new ChunkConfig(ChunkConfig.NONE, ChunkConfig.ALL), getRepository().resolveCollectionLoadingConfig(serverSession, feature));

      sessionOverrides.clear();
      sessionOverrides.put(subClass, new ChunkConfig(81, 82));
      sessionOverrides.put(superClass, new ChunkConfig(91, 92));
      serverSession.setCollectionLoadingConfig(new CDOCollectionLoadingConfig(null, sessionOverrides));
      assertEquals(new ChunkConfig(ChunkConfig.NONE, 33), getRepository().resolveCollectionLoadingConfig(serverSession, feature));

      getRepository().setCollectionLoadingConfig(null);
      serverSession.setCollectionLoadingConfig(new CDOCollectionLoadingConfig(new ChunkConfig(ChunkConfig.NONE, ChunkConfig.INHERIT), Collections.emptyMap()));
      assertEquals(new ChunkConfig(ChunkConfig.NONE, ChunkConfig.ALL), getRepository().resolveCollectionLoadingConfig(serverSession, fallbackFeature));
    }
  }

  public void testCommonResolverEnablementAndIndependentDimensions()
  {
    EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
    ePackage.setNsURI("test://common-collection-loading");
    EClass superClass = EcoreFactory.eINSTANCE.createEClass();
    superClass.setName("SuperType");
    ePackage.getEClassifiers().add(superClass);
    EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName("Type");
    eClass.getESuperTypes().add(superClass);
    ePackage.getEClassifiers().add(eClass);
    EStructuralFeature feature = EcoreFactory.eINSTANCE.createEAttribute();
    feature.setName("values");
    eClass.getEStructuralFeatures().add(feature);

    Map<EModelElement, ChunkConfig> repositoryOverrides = new LinkedHashMap<>();
    repositoryOverrides.put(eClass, new ChunkConfig(ChunkConfig.INHERIT, 50));
    CDOCollectionLoadingConfig repositoryConfig = new CDOCollectionLoadingConfig(new ChunkConfig(70, 71), repositoryOverrides);
    CDOCollectionLoadingConfig sessionConfig = new CDOCollectionLoadingConfig(null,
        Collections.singletonMap(feature, new ChunkConfig(ChunkConfig.NONE, ChunkConfig.INHERIT)));
    CDOCollectionLoadingConfigResolver resolver = new CDOCollectionLoadingConfigResolver();

    assertNull(resolver.resolve(null, repositoryConfig, feature));
    assertEquals(new ChunkConfig(ChunkConfig.ALL, ChunkConfig.ALL), resolver.resolve(new CDOCollectionLoadingConfig(), null, feature));
    assertEquals(new ChunkConfig(ChunkConfig.NONE, 50), resolver.resolve(sessionConfig, repositoryConfig, feature));

    Map<EModelElement, ChunkConfig> superclassOverrides = new LinkedHashMap<>();
    superclassOverrides.put(superClass, new ChunkConfig(81, 82));
    CDOCollectionLoadingConfig superclassConfig = new CDOCollectionLoadingConfig(null, superclassOverrides);
    assertEquals(new ChunkConfig(ChunkConfig.ALL, ChunkConfig.ALL), resolver.resolve(new CDOCollectionLoadingConfig(), superclassConfig, feature));

    resolver.clearModelCache();
  }

  public void testClientRepositoryConfigurationSnapshotRoundTrip()
  {
    CDOCollectionLoadingConfig repositoryConfig = new CDOCollectionLoadingConfig(new ChunkConfig(17, 19), Collections.emptyMap());
    CDOCollectionLoadingConfig sessionConfig = new CDOCollectionLoadingConfig(new ChunkConfig(23, 29), Collections.emptyMap());

    try (CDOSession session = openSession())
    {
      getRepository().setCollectionLoadingConfig(repositoryConfig);
      session.options().setCollectionLoadingConfig(sessionConfig);

      @SuppressWarnings("resource")
      CDOSessionImpl internalSession = (CDOSessionImpl)session;
      assertEquals(repositoryConfig, internalSession.getRepositoryCollectionLoadingConfig());

      getRepository().setCollectionLoadingConfig(null);
      session.options().setCollectionLoadingConfig(sessionConfig);
      assertNull(internalSession.getRepositoryCollectionLoadingConfig());

      session.options().setCollectionLoadingConfig(null);
      assertNull(session.options().getCollectionLoadingConfig());
      assertNull(internalSession.getRepositoryCollectionLoadingConfig());
    }
  }

  private static EAnnotation createAnnotation(int initialChunkSize, int resolveChunkSize)
  {
    EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
    annotation.setSource(EMFUtil.CDO_ANNOTATION_SOURCE);
    annotation.getDetails().put(EMFUtil.CDO_ANNOTATION_KEY_INITIAL_CHUNK_SIZE, Integer.toString(initialChunkSize));
    annotation.getDetails().put(EMFUtil.CDO_ANNOTATION_KEY_RESOLVE_CHUNK_SIZE, Integer.toString(resolveChunkSize));
    return annotation;
  }
}
