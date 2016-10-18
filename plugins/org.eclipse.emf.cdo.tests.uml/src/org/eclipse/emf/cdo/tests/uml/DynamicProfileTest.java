/*
 * Copyright (c) 2013, 2015, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.uml;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.ModelConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.uml2.common.util.UML2Util;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

/**
 * Tests legacy-mode support for dynamic UML Profiles.
 */
@Requires(ModelConfig.CAPABILITY_LEGACY)
public class DynamicProfileTest extends AbstractCDOTest
{
  private final EPackage stuffPackage = EPackage.Registry.INSTANCE.getEPackage("http://www.eclipse.org/cdo/tests/schema/stuff/1.0");

  private final EFactory originalStuffFactory = stuffPackage.getEFactoryInstance();

  private static final String MY_PROFILE_URI = "http://www.eclipse.org/cdo/tests/schema/myprofile";

  private static final String S_CONCEPT = "Concept";

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

    Model umlModel = UMLFactory.eINSTANCE.createModel();
    res.getContents().add(umlModel);

    umlModel.setName("model");
    org.eclipse.uml2.uml.Class aClass = umlModel.createOwnedClass("Fruit", true);

    Profile profile = UML2Util.load(rset, URI.createURI(MY_PROFILE_URI), UMLPackage.Literals.PROFILE);
    assertNotNull(profile);

    umlModel.applyProfile(profile);

    Stereotype conceptStereotype = profile.getOwnedStereotype(S_CONCEPT);
    aClass.applyStereotype(conceptStereotype);

    transaction.commit();

    // load the model again in a new session (hence new package registry)
    session.close();
    session = openSession();

    transaction = session.openTransaction();
    res = transaction.getResource(getResourcePath("/model1.uml"), true);

    assertEquals(true, res.isLoaded());
    assertEquals(2, res.getContents().size()); // the model and the stereotype instance

    umlModel = (Model)res.getContents().get(0);
    aClass = (org.eclipse.uml2.uml.Class)EcoreUtil.getObjectByType(umlModel.getOwnedTypes(), UMLPackage.Literals.CLASS);
    assertNotNull(aClass);

    // verify that using the UML API to access the applied stereotype works as usual
    assertEquals("Stereotype doesn't appear to be applied.", true, aClass.isStereotypeApplied(conceptStereotype));
    assertEquals("Wrong stereotype EClass.", transaction.getResourceSet().getPackageRegistry().getEPackage(MY_PROFILE_URI).getEClassifier(S_CONCEPT),
        aClass.getStereotypeApplication(conceptStereotype).eClass());
  }

  public void testInstancesOfLocalDynamicUMLProfile() throws Exception
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(UMLPackage.eINSTANCE);

    ResourceSet rset = new ResourceSetImpl();
    CDOTransaction transaction = session.openTransaction(rset);
    CDOResource res = transaction.createResource(getResourcePath("/model1.uml"));

    Model umlModel = UMLFactory.eINSTANCE.createModel();
    res.getContents().add(umlModel);

    umlModel.setName("model");
    org.eclipse.uml2.uml.Class aClass = umlModel.createOwnedClass("Fruit", true);

    CDOResource profileRes = transaction.createResource(getResourcePath("/profile1.profile.uml"));

    // create a local profile resource in the repository
    Profile profile = UMLFactory.eINSTANCE.createProfile();
    profileRes.getContents().add(profile);
    profile.setName("MyProfile");
    Stereotype conceptStereotype = profile.createOwnedStereotype(S_CONCEPT, false);
    Model uml = UML2Util.load(rset, URI.createURI(UMLResource.UML_METAMODEL_URI), UMLPackage.Literals.MODEL);
    profile.createMetamodelReference(uml);
    conceptStereotype.createExtension((org.eclipse.uml2.uml.Class)uml.getOwnedType("Classifier"), false);
    EPackage definition = profile.define();

    // condition the Ecore definition for CDO
    CDOUtil.prepareDynamicEPackage(definition);

    // apply the profile and stereotype to the model
    umlModel.applyProfile(profile);
    aClass.applyStereotype(conceptStereotype);
    assertEquals("Stereotype not applied.", true, aClass.isStereotypeApplied(conceptStereotype));

    transaction.commit();

    // load the model again in a new session (hence new package registry)
    session.close();
    session = openSession();

    transaction = session.openTransaction();
    res = transaction.getResource(getResourcePath("/model1.uml"), true);

    assertEquals(true, res.isLoaded());
    assertEquals(2, res.getContents().size()); // the model and the stereotype instance

    umlModel = (Model)res.getContents().get(0);
    aClass = (org.eclipse.uml2.uml.Class)EcoreUtil.getObjectByType(umlModel.getOwnedTypes(), UMLPackage.Literals.CLASS);
    assertNotNull(aClass);

    EObject stereotypeApplication = res.getContents().get(1);

    // don't have the version of UML2 API that fixes problems in name-based look-up of stereotypes.
    // The main point is that commit didn't fail on unresolved proxies in the Ecore definition
    assertSame("Wrong base element reference.", aClass, stereotypeApplication.eGet(stereotypeApplication.eClass().getEStructuralFeature("base_Classifier")));
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
}
