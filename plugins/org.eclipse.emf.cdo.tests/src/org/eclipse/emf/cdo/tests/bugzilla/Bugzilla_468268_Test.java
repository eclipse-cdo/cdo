/*
 * Copyright (c) 2015, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model6.ContainmentObject;

import org.eclipse.net4j.util.ReflectUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Bug 468268 about NPE on RevisionWithoutID.hashCode() call with TRANSIENT CDOObject.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_468268_Test extends AbstractCDOTest
{
  public void testHashCodeCallOnAllFields() throws Exception
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("model6", new XMIResourceFactoryImpl());

    URI localResourceURI = URI.createFileURI(createTempFile("main", ".model6").getCanonicalPath());
    Resource localResource = resourceSet.createResource(localResourceURI);

    ContainmentObject mainContainmentObject = getModel6Factory().createContainmentObject();
    testHashCodeOnAllFieds(mainContainmentObject);
    localResource.getContents().add(mainContainmentObject);
    testHashCodeOnAllFieds(mainContainmentObject);
    localResource.save(Collections.emptyMap());
    testHashCodeOnAllFieds(mainContainmentObject);
  }

  private void testHashCodeOnAllFieds(EObject eObject) throws Exception
  {
    Set<Field> fields = getFields(eObject);

    for (Field field : fields)
    {
      Object value = getValue(field, eObject);
      if (value != null)
      {
        value.hashCode();
      }
    }
  }

  private Object getValue(Field field, EObject eObject) throws Exception
  {
    ReflectUtil.makeAccessible(field);
    return field.get(eObject);
  }

  private Set<Field> getFields(EObject eObject)
  {
    Set<Field> allFields = new HashSet<>();
    Field[] fields = eObject.getClass().getDeclaredFields();
    allFields.addAll(Arrays.asList(fields));
    Class<?> currentClass = eObject.getClass().getSuperclass();

    while (currentClass != Object.class)
    {
      fields = currentClass.getDeclaredFields();
      allFields.addAll(Arrays.asList(fields));
      currentClass = currentClass.getSuperclass();
    }

    return allFields;
  }
}
