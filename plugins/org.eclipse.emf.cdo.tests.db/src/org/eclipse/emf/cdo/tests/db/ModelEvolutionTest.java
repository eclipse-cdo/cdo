/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.internal.db.ModelEvolutionSupport;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.event.LogListener;

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
  private static final EPackage.Registry PACKAGE_REGISTRY = EPackage.Registry.INSTANCE;

  private static final ResourceSet RESOURCE_SET = EMFUtil.newEcoreResourceSet(PACKAGE_REGISTRY);

  private static final EPackage V1 = createModelV1();

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    RESOURCE_SET.getResources().clear();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    RESOURCE_SET.getResources().clear();
    super.doTearDown();
  }

  @Override
  public synchronized Map<String, Object> getTestProperties()
  {
    Map<String, Object> testProperties = super.getTestProperties();
    testProperties.put(RepositoryConfig.PROP_TEST_INITIAL_PACKAGES, new EPackage[] { V1 });
    ModelEvolutionSupport modelEvolutionSupport = new ModelEvolutionSupport();
    modelEvolutionSupport.addListener(new LogListener());
    testProperties.put(DBConfig.PROP_TEST_MODEL_EVOLUTION_SUPPORT, modelEvolutionSupport);
    return testProperties;
  }

  public void test() throws Exception
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

    // Add 'name' attributes to class A.
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

    // Add 'name' attributes to class B.
    EAttribute v2Bname = EcoreFactory.eINSTANCE.createEAttribute();
    v2Bname.setName("name");
    v2Bname.setEType(EcorePackage.Literals.ESTRING);
    v2B.getEStructuralFeatures().add(0, v2Bname);

    restartRepository();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("test"));

    EObject a = resource.getContents().get(0);
    a.eSet(v2Aname, "Eike Stepper");

    EObject b3 = create(v2, "B");
    list(a, "new_children").add(b3);

    transaction.commit();
    System.out.println();
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
    RESOURCE_SET.createResource(URI.createURI(nsURI)).getContents().add(ePackage);
    PACKAGE_REGISTRY.put(nsURI, ePackage);
    return ePackage;
  }

  private static EPackage createModelV1()
  {
    // Package v1
    EPackage model = EMFUtil.createEPackage("model", "model", "http://www.example.org/model");

    // Enum Shape
    EEnum Shape = EMFUtil.createEEnum(model, "Shape", "CIRCLE", "SQUARE", "TRIANGLE", "RECTANGLE", "OVAL", "DIAMOND");

    // Class A
    EClass A = EMFUtil.createEClass(model, "A");

    // Class B
    EClass B = EMFUtil.createEClass(model, "B");

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

    // EAttribute B_shape =
    EMFUtil.createEAttribute(B, "shape", Shape);

    return registerPackage(model);
  }
}
