/*
 * Copyright (c) 2009, 2011, 2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.EresourcePackage;

import org.eclipse.net4j.util.tests.AbstractOMTest;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Eike Stepper
 */
public class EMFComplianceTest extends AbstractOMTest
{
  public void testResourceConstants() throws Exception
  {
    assertEquals(Resource.RESOURCE__RESOURCE_SET, EresourcePackage.CDO_RESOURCE__RESOURCE_SET);
    assertEquals(Resource.RESOURCE__URI, EresourcePackage.CDO_RESOURCE__URI);
    assertEquals(Resource.RESOURCE__CONTENTS, EresourcePackage.CDO_RESOURCE__CONTENTS);
    assertEquals(Resource.RESOURCE__IS_MODIFIED, EresourcePackage.CDO_RESOURCE__MODIFIED);
    assertEquals(Resource.RESOURCE__IS_LOADED, EresourcePackage.CDO_RESOURCE__LOADED);
    assertEquals(Resource.RESOURCE__IS_TRACKING_MODIFICATION, EresourcePackage.CDO_RESOURCE__TRACKING_MODIFICATION);
    assertEquals(Resource.RESOURCE__ERRORS, EresourcePackage.CDO_RESOURCE__ERRORS);
    assertEquals(Resource.RESOURCE__WARNINGS, EresourcePackage.CDO_RESOURCE__WARNINGS);
    assertEquals(Resource.RESOURCE__TIME_STAMP, EresourcePackage.CDO_RESOURCE__TIME_STAMP);

    assertEquals(EresourcePackage.eINSTANCE.getCDOResource_ResourceSet(),
        EresourcePackage.eINSTANCE.getCDOResource().getEStructuralFeature(Resource.RESOURCE__RESOURCE_SET));
    assertEquals(EresourcePackage.eINSTANCE.getCDOResource_URI(), EresourcePackage.eINSTANCE.getCDOResource().getEStructuralFeature(Resource.RESOURCE__URI));
    assertEquals(EresourcePackage.eINSTANCE.getCDOResource_Contents(),
        EresourcePackage.eINSTANCE.getCDOResource().getEStructuralFeature(Resource.RESOURCE__CONTENTS));
    assertEquals(EresourcePackage.eINSTANCE.getCDOResource_Modified(),
        EresourcePackage.eINSTANCE.getCDOResource().getEStructuralFeature(Resource.RESOURCE__IS_MODIFIED));
    assertEquals(EresourcePackage.eINSTANCE.getCDOResource_Loaded(),
        EresourcePackage.eINSTANCE.getCDOResource().getEStructuralFeature(Resource.RESOURCE__IS_LOADED));
    assertEquals(EresourcePackage.eINSTANCE.getCDOResource_TrackingModification(),
        EresourcePackage.eINSTANCE.getCDOResource().getEStructuralFeature(Resource.RESOURCE__IS_TRACKING_MODIFICATION));
    assertEquals(EresourcePackage.eINSTANCE.getCDOResource_Errors(),
        EresourcePackage.eINSTANCE.getCDOResource().getEStructuralFeature(Resource.RESOURCE__ERRORS));
    assertEquals(EresourcePackage.eINSTANCE.getCDOResource_Warnings(),
        EresourcePackage.eINSTANCE.getCDOResource().getEStructuralFeature(Resource.RESOURCE__WARNINGS));
    assertEquals(EresourcePackage.eINSTANCE.getCDOResource_TimeStamp(),
        EresourcePackage.eINSTANCE.getCDOResource().getEStructuralFeature(Resource.RESOURCE__TIME_STAMP));
  }
}
