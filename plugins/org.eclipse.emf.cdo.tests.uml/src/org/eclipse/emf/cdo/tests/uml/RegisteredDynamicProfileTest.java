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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.ModelConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Tests legacy-mode support for registered dynamic UML Profiles.
 */
@Requires(ModelConfig.CAPABILITY_LEGACY)
public class RegisteredDynamicProfileTest extends AbstractCDOTest
{
  private final EPackage stuffPackage = EPackage.Registry.INSTANCE
      .getEPackage("http://www.eclipse.org/cdo/tests/schema/stuff/1.0");

  private final EFactory originalStuffFactory = stuffPackage.getEFactoryInstance();

  private static final String MY_PROFILE_URI = "http://www.eclipse.org/cdo/tests/schema/myprofile";

  private static final String S_CONCEPT = "Concept";

  private final UMLFactory umlFactory = UMLFactory.eINSTANCE;

  //
  // Test cases
  //

  public void testInstancesOfRegisteredDynamicUMLProfile() throws Exception
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(UMLPackage.eINSTANCE);

    ResourceSet rset = new ResourceSetImpl();
    CDOTransaction transaction = session.openTransaction(rset);
    CDOResource res = transaction.createResource(getResourcePath("/model1.uml"));

    Model umlModel = umlFactory.createModel();
    res.getContents().add(umlModel);

    umlModel.setName("model");
    Class aClass = umlModel.createOwnedClass("Fruit", true);

    Profile profile = UML2Util.load(rset, URI.createURI(MY_PROFILE_URI), UMLPackage.Literals.PROFILE);
    assertNotNull(profile);

    umlModel.applyProfile(profile);

    Stereotype conceptStereotype = profile.getOwnedStereotype(S_CONCEPT);
    aClass.applyStereotype(conceptStereotype);

    // condition the profile for CDO compatibility, so that we don't try to reference the UML content
    // from the EPackage in the registry. This will be implemented in the UML2 API for Kepler release
    convertDefinitionsToWeakReferences(profile);
    convertDefinitionsToWeakReferences(EPackage.Registry.INSTANCE.getEPackage(MY_PROFILE_URI));

    transaction.commit();

    // load the model again in a new session (hence new package registry)
    session.close();
    session = openSession();

    transaction = session.openTransaction();
    res = transaction.getResource(getResourcePath("/model1.uml"), true);

    assertEquals(true, res.isLoaded());
    assertEquals(2, res.getContents().size()); // the model and the stereotype instance

    umlModel = (Model)res.getContents().get(0);
    aClass = (Class)EcoreUtil.getObjectByType(umlModel.getOwnedTypes(), UMLPackage.Literals.CLASS);
    assertNotNull(aClass);

    // changes in UML2 API are needed to recognize the EClass<-->Stereotype relationship using
    // "weak references" (the conditioning step for CDO), so this tests the low-level structure
    EObject conceptInstance = res.getContents().get(1);
    EClass conceptClass = conceptInstance.eClass();
    assertEquals("Concept", conceptClass.getName());
    assertEquals(MY_PROFILE_URI, conceptClass.getEPackage().getNsURI());

    assertEquals(aClass, conceptInstance.eGet(conceptClass.getEStructuralFeature("base_Classifier")));
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

  /**
   * <p>
   * Converts all of the Ecore-to-UML traceability references in a dynamic
   * profile from hard references to weak references.  This is not yet
   * implemented in Eclipse UML2, but will be for Kepler release.
   * </p>
   * <p>
   * TODO: Remove this when the UML2 implementation is available
   * </p>
   *
   * @param profile
   *            a dynamic profile
   *
   * @return the same {@code profile}, for convenience of call chaining
   */
  public static Profile convertDefinitionsToWeakReferences(Profile profile)
  {
    EAnnotation annotation = profile.getEAnnotation("http://www.eclipse.org/uml2/2.0.0/UML");

    if (annotation != null)
    {
      for (EObject next : annotation.getContents())
      {
        if (next instanceof EPackage)
        {
          convertDefinitionsToWeakReferences((EPackage)next);
        }
      }
    }

    return profile;
  }

  /**
   * <p>
   * Converts all of the Ecore-to-UML traceability references in a dynamic
   * profile's Ecore definition from hard references to weak references.
   * This is not yet implemented in Eclipse UML2, but will be for Kepler release.
   * </p>
   * <p>
   * TODO: Remove this when the UML2 implementation is available
   * </p>
   *
   * @param ePackage
   *            a dynamic profile's Ecore definition
   *
   * @return the same {@code ePackage}, for convenience of call chaining
   */
  public static EPackage convertDefinitionsToWeakReferences(EPackage ePackage)
  {
    for (EClassifier next : ePackage.getEClassifiers())
    {
      EAnnotation annotation = next.getEAnnotation("http://www.eclipse.org/uml2/2.0.0/UML");
      if (annotation != null && !annotation.getReferences().isEmpty())
      {
        EObject referenced = annotation.getReferences().get(0);
        if (referenced instanceof NamedElement)
        {
          Resource res = referenced.eResource();
          if (res != null)
          {
            StringBuilder buf = new StringBuilder();
            buf.append(res.getURI()).append('#').append(((NamedElement)referenced).getQualifiedName());
            annotation.getDetails().put("href", buf.toString());
            annotation.getReferences().clear();
          }
        }
      }
    }

    return ePackage;
  }
}
