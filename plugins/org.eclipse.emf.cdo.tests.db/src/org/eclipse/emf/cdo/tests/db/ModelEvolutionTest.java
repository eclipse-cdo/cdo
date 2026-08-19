/*
 * Copyright (c) 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.server.db.evolution.phased.DefaultRepositoryExporter;
import org.eclipse.emf.cdo.server.db.evolution.phased.FolderContextManager;
import org.eclipse.emf.cdo.server.db.evolution.phased.PhasedModelEvolutionSupport;
import org.eclipse.emf.cdo.server.db.evolution.phased.PhasedModelEvolutionSupport.Mode;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.event.LogListener;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * Tests for model evolution support in CDO DB store.
 *
 * @author Eike Stepper
 */
@CleanRepositoriesBefore(reason = "Model evolution")
@CleanRepositoriesAfter(reason = "Model evolution")
public class ModelEvolutionTest extends AbstractCDOTest
{
  private static final boolean LOG = true;

  private static final String FIXED_ROOT_FOLDER = null; // "C:\\develop\\temp\\model-evolution";

  private static final EPackage.Registry PACKAGE_REGISTRY = EPackage.Registry.INSTANCE;

  private static final ResourceSet RESOURCE_SET = EMFUtil.newEcoreResourceSet(PACKAGE_REGISTRY);

  private static final String NS_URI = "http://www.example.org/model";

  private static final EPackage V1 = createModelV1();

  private File rootFolder;

  public void testFeatureAddition() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("test"));

      EObject a = create(V1, "A");
      resource.getContents().add(a);

      EObject b1 = create(V1, "B");
      list(a, "children_bidi").add(b1);
      b1.eSet(b1.eClass().getEStructuralFeature("shape"), ((EEnum)V1.getEClassifier("Shape")).getEEnumLiteral("TRIANGLE"));

      EObject b2 = create(V1, "B");
      list(a, "children").add(b2);
      b2.eSet(b2.eClass().getEStructuralFeature("shape"), ((EEnum)V1.getEClassifier("Shape")).getEEnumLiteral("RECTANGLE"));

      transaction.commit();
    }

    // InternalRepository repository = getRepository();
    // DBStore store = (DBStore)repository.getStore();
    // Map<EClass, IClassMapping> classMappings = store.getMappingStrategy().getClassMappings();

    EPackage v2 = registerPackage(EcoreUtil.copy(V1));
    EEnum v2Shape = (EEnum)v2.getEClassifier("Shape");
    EClass v2A = (EClass)v2.getEClassifier("A");
    EClass v2B = (EClass)v2.getEClassifier("B");

    // Shift existing literals with value >= 3 by one ("RECTANGLE", "OVAL", "DIAMOND").
    for (EEnumLiteral literal : v2Shape.getELiterals())
    {
      if (literal.getValue() >= 3)
      {
        literal.setValue(literal.getValue() + 1);
      }
    }

    // Create a new literal 'PENTAGON' in enum Shape with value 3.
    EMFUtil.createEEnumLiteral(v2Shape, "PENTAGON", 3);

    // Remove the 'sub_a' reference from class A.
    v2A.getEStructuralFeatures().remove(0);

    // Add 'name' attribute to class A.
    EAttribute v2Aname = EcoreFactory.eINSTANCE.createEAttribute();
    v2Aname.setName("name");
    v2Aname.setEType(EcorePackage.Literals.ESTRING);
    v2A.getEStructuralFeatures().add(1, v2Aname);

    // Add 'new_children' containment reference to class A.
    EReference v2Anew_children = EcoreFactory.eINSTANCE.createEReference();
    v2Anew_children.setName("new_children");
    v2Anew_children.setEType(v2B);
    v2Anew_children.setUpperBound(-1);
    v2Anew_children.setContainment(true);
    v2A.getEStructuralFeatures().add(v2Anew_children);

    // Add 'name' attribute to class B.
    EAttribute v2Bname = EcoreFactory.eINSTANCE.createEAttribute();
    v2Bname.setName("name");
    v2Bname.setEType(EcorePackage.Literals.ESTRING);
    v2B.getEStructuralFeatures().add(0, v2Bname);

    restartWithEvolution(v2);

    CDOSession session = openSession();
    msg(EMFUtil.getXMI(session.getPackageRegistry().getEPackage(NS_URI)));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("test"));

    EObject a = resource.getContents().get(0);
    a.eSet(v2Aname, "Eike Stepper");

    EObject b3 = create(v2, "B");
    list(a, "new_children").add(b3);

    transaction.commit();
  }

  public void testAddStringAfterBlob() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("test"));

      EObject c = create(V1, "C");
      resource.getContents().add(c);

      try (InputStream inputStream = org.eclipse.emf.cdo.tests.bundle.OM.BUNDLE.getInputStream("backup-tests/Ecore.uml"))
      {
        CDOBlob blob = new CDOBlob(inputStream);
        c.eSet(c.eClass().getEStructuralFeature("blob"), blob);

        transaction.commit();
      }
    }

    // InternalRepository repository = getRepository();
    // DBStore store = (DBStore)repository.getStore();
    // Map<EClass, IClassMapping> classMappings = store.getMappingStrategy().getClassMappings();

    EPackage v2 = registerPackage(EcoreUtil.copy(V1));
    EClass v2C = (EClass)v2.getEClassifier("C");

    // Add 'name' attribute to class C.
    EAttribute v2Cname = EcoreFactory.eINSTANCE.createEAttribute();
    v2Cname.setName("name");
    v2Cname.setEType(EcorePackage.Literals.ESTRING);
    v2C.getEStructuralFeatures().add(v2Cname);

    restartWithEvolution(v2);

    CDOSession session = openSession();
    msg(EMFUtil.getXMI(session.getPackageRegistry().getEPackage(NS_URI)));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("test"));

    EObject c = resource.getContents().get(0);
    c.eSet(v2Cname, "Eike Stepper");

    transaction.commit();
  }

  public void testRemoveAndAddFeature() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("test"));

      EObject b = create(V1, "B");
      b.eSet(b.eClass().getEStructuralFeature("shape"), ((EEnum)V1.getEClassifier("Shape")).getEEnumLiteral("TRIANGLE"));
      resource.getContents().add(b);

      transaction.commit();
    }

    EPackage v2 = registerPackage(EcoreUtil.copy(V1));
    EClass v2B = (EClass)v2.getEClassifier("B");

    // Remove the 'shape' attribute from class B.
    EList<EStructuralFeature> v2BFeatures = v2B.getEStructuralFeatures();
    v2BFeatures.remove(1);

    // Trigger evolution ID 1.
    restartWithEvolution(v2);

    // {
    // InternalRepository repository = getRepository();
    // DBStore store = (DBStore)repository.getStore();
    //
    // serverRun(() -> {
    // Map<EClass, IClassMapping> classMappings = store.getMappingStrategy().getClassMappings();
    // IClassMapping bMapping = classMappings.get(v2B);
    //
    // EStructuralFeature shapeFeature = v2B.getEStructuralFeature("shape");
    // IFeatureMapping shapeMapping = bMapping.getFeatureMapping(shapeFeature);
    // System.out.println(shapeMapping);
    // });
    // }

    EPackage v3 = registerPackage(EcoreUtil.copy(v2));
    EClass v3B = (EClass)v3.getEClassifier("B");
    EEnum Shape = (EEnum)v3.getEClassifier("Shape");

    // Re-add the 'shape' attribute to class B.
    EMFUtil.createEAttribute(v3B, "shape", Shape);

    // Trigger evolution ID 2.
    restartWithEvolution(v3);

    // {
    // InternalRepository repository = getRepository();
    // DBStore store = (DBStore)repository.getStore();
    //
    // serverRun(() -> {
    // Map<EClass, IClassMapping> classMappings = store.getMappingStrategy().getClassMappings();
    // IClassMapping bMapping = classMappings.get(v3B);
    // IFeatureMapping shapeMapping = bMapping.getFeatureMapping(v3B.getEStructuralFeature("shape"));
    // System.out.println(shapeMapping);
    // });
    // }

    CDOSession session = openSession();
    msg(EMFUtil.getXMI(session.getPackageRegistry().getEPackage(NS_URI)));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("test"));

    EObject b = resource.getContents().get(0);
    b.eSet(b.eClass().getEStructuralFeature("shape"), Shape.getEEnumLiteral("RECTANGLE"));

    transaction.commit();
    System.out.println();
  }

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    RESOURCE_SET.getResources().clear();

    rootFolder = initRootFolder();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    RESOURCE_SET.getResources().clear();
    super.doTearDown();
  }

  @Override
  protected void initTestProperties(Map<String, Object> properties)
  {
    super.initTestProperties(properties);
    initTestProperties(properties, V1);
  }

  private void initTestProperties(Map<String, Object> properties, EPackage initialPackage)
  {
    FolderContextManager contextManager = new FolderContextManager();
    contextManager.setSaveNewModels(true);

    PhasedModelEvolutionSupport support = new PhasedModelEvolutionSupport();
    support.setRootFolder(rootFolder);
    support.setContextManager(contextManager);
    support.setMode(Mode.Migrate);
    support.setRepositoryExporter(new DefaultRepositoryExporter());

    if (LOG)
    {
      support.addListener(new LogListener());
    }

    properties.put(RepositoryConfig.PROP_TEST_INITIAL_PACKAGES, new EPackage[] { initialPackage });
    properties.put(DBConfig.PROP_TEST_MODEL_EVOLUTION_SUPPORT, support);
  }

  private File initRootFolder()
  {
    if (FIXED_ROOT_FOLDER != null)
    {
      File rootFolder = new File(FIXED_ROOT_FOLDER, getTestMethodName());
      IOUtil.delete(rootFolder);
      return rootFolder;
    }

    return createTempFolder();
  }

  private void restartWithEvolution(EPackage initialPackage)
  {
    Map<String, Object> properties = getTestProperties();
    initTestProperties(properties, initialPackage);

    restartRepository();
  }

  private static EObject create(EPackage ePackage, EClass eClass)
  {
    return ePackage.getEFactoryInstance().create(eClass);
  }

  private static EObject create(EPackage ePackage, String className)
  {
    return create(ePackage, (EClass)ePackage.getEClassifier(className));
  }

  @SuppressWarnings("unchecked")
  private static EList<EObject> list(EObject object, EStructuralFeature feature)
  {
    return (EList<EObject>)object.eGet(feature);
  }

  private static EList<EObject> list(EObject object, String featureName)
  {
    return list(object, object.eClass().getEStructuralFeature(featureName));
  }

  private static void linkOpposites(EReference ref1, EReference ref2)
  {
    ref1.setEOpposite(ref2);
    ref2.setEOpposite(ref1);
  }

  private static EPackage registerPackage(EPackage ePackage)
  {
    String nsURI = ePackage.getNsURI();
    URI uri = URI.createURI(nsURI);

    RESOURCE_SET.getResources().removeIf(resource -> uri.equals(resource.getURI()));
    RESOURCE_SET.createResource(uri).getContents().add(ePackage);

    PACKAGE_REGISTRY.put(nsURI, ePackage);
    return ePackage;
  }

  private static EPackage createModelV1()
  {
    // Package v1
    EPackage model = EMFUtil.createEPackage("model", "model", NS_URI);

    // Enum Shape
    EEnum Shape = EMFUtil.createEEnum(model, "Shape", "CIRCLE", "SQUARE", "TRIANGLE", "RECTANGLE", "OVAL", "DIAMOND");

    // Class A
    EClass A = EMFUtil.createEClass(model, "A");

    // Class B
    EClass B = EMFUtil.createEClass(model, "B");

    // Class C
    EClass C = EMFUtil.createEClass(model, "C");

    // Containment reference A.children
    EReference A_sub_a = EMFUtil.createEReference(A, "sub_a", A);
    A_sub_a.setContainment(true);

    // Containment reference A.children
    EReference A_children = EMFUtil.createEReference(A, "children", B, false, true);
    A_children.setContainment(true);

    // Containment reference A.children_bidi
    EReference A_children_bidi = EMFUtil.createEReference(A, "children_bidi", B, false, true);
    A_children_bidi.setContainment(true);

    // Container reference B.parent
    EReference B_parent = EMFUtil.createEReference(B, "parent", A);
    linkOpposites(A_children_bidi, B_parent);

    // Attribute B.shape
    EMFUtil.createEAttribute(B, "shape", Shape);

    // Attribute C.blob
    EMFUtil.createEAttribute(C, "blob", EtypesPackage.Literals.BLOB);

    return registerPackage(model);
  }
}
