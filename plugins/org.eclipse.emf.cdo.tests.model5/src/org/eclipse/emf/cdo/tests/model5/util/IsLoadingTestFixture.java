/*
 * Copyright (c) 2012, 2013, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model5.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import org.junit.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Test fixture for the CDO resource "is loading" tests. Bug 393164.
 */
public class IsLoadingTestFixture extends Assert
{
  private static IsLoadingTestFixture instance;

  private Map<Resource, Set<EObject>> objectsReportedLoading = new HashMap<>();

  private IsLoadingTestFixture()
  {
  }

  public static IsLoadingTestFixture newInstance()
  {
    IsLoadingTestFixture result = new IsLoadingTestFixture();
    instance = result;
    return result;
  }

  public void dispose()
  {
    if (instance == this)
    {
      instance = null;
    }

    objectsReportedLoading.clear();
  }

  public static void reportLoading(Resource resource, EObject object)
  {
    if (instance != null)
    {
      instance.doReportLoading(resource, object);
    }
  }

  private Set<EObject> demandObjectsReportedLoading(Resource resource)
  {
    Set<EObject> result = objectsReportedLoading.get(resource);
    if (result == null)
    {
      result = new java.util.HashSet<>();
      objectsReportedLoading.put(resource, result);
    }

    return result;
  }

  private Set<EObject> getObjectsReportedLoading(Resource resource)
  {
    Set<EObject> result = objectsReportedLoading.get(resource);
    if (result == null)
    {
      result = Collections.emptySet();
    }

    return result;
  }

  private void doReportLoading(Resource resource, EObject object)
  {
    if (resource instanceof Resource.Internal && ((Resource.Internal)resource).isLoading())
    {
      demandObjectsReportedLoading(resource).add(object);
    }
  }

  public void assertReportedLoading(Resource resource, EObject object)
  {
    assertEquals("Object did not report loading: " + object, true, getObjectsReportedLoading(resource).contains(object));
  }

  public void assertNotReportedLoading(Resource resource, EObject object)
  {
    assertEquals("Object reported loading: " + object, false, getObjectsReportedLoading(resource).contains(object));
  }
}
