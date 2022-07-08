/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.net4j.util.ui.views.RowIntrospectionProvider;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class EMFIntrospectionProvider extends RowIntrospectionProvider
{
  protected static final int PRIORITY = DEFAULT_PRIORITY - 20;

  protected static final int CATEGORY = DEFAULT_CATEGORY - 20;

  private static final String E_CONTAINER = "eContainer";

  private static final String E_CLASS = "eClass";

  private static final String E_IS_PROXY = "eIsProxy";

  private static final String E_DELIVER = "eDeliver";

  private static final String E_ADAPTERS = "eAdapters";

  private static final String E_RESOURCE = "eResource";

  private static final String E_URI = "eURI";

  private final Color foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE);

  public EMFIntrospectionProvider()
  {
    super(EObject.class.getName(), "EObject");
  }

  protected EMFIntrospectionProvider(String id, String label)
  {
    super(id, label);
  }

  @Override
  public int getPriority()
  {
    return PRIORITY;
  }

  @Override
  public boolean canHandle(Object object)
  {
    return object instanceof EObject;
  }

  @Override
  protected void fillRows(Object parent, List<Row> rows) throws Exception
  {
    if (parent instanceof EObject)
    {
      EObject eObject = (EObject)parent;

      rows.add(createEContainerRow(eObject));
      rows.add(createEURIRow(eObject));
      rows.add(createEResourceRow(eObject));
      rows.add(createEClassRow(eObject));
      rows.add(createEIsProxyRow(eObject));
      rows.add(createEDeliverRow(eObject));
      rows.add(createEAdaptersRow(eObject));

      for (EStructuralFeature feature : eObject.eClass().getEAllStructuralFeatures())
      {
        rows.add(createRow(eObject, feature));
      }
    }
  }

  @Override
  public Row getElementByName(Object parent, String name) throws Exception
  {
    if (parent instanceof EObject)
    {
      EObject eObject = (EObject)parent;

      if (E_CONTAINER.equals(name))
      {
        return createEContainerRow(eObject);
      }

      if (E_CLASS.equals(name))
      {
        return createEClassRow(eObject);
      }

      if (E_IS_PROXY.equals(name))
      {
        return createEIsProxyRow(eObject);
      }

      if (E_DELIVER.equals(name))
      {
        return createEDeliverRow(eObject);
      }

      if (E_ADAPTERS.equals(name))
      {
        return createEAdaptersRow(eObject);
      }

      if (E_RESOURCE.equals(name))
      {
        return createEResourceRow(eObject);
      }

      if (E_URI.equals(name))
      {
        return createEURIRow(eObject);
      }

      EStructuralFeature feature = eObject.eClass().getEStructuralFeature(name);
      if (feature != null)
      {
        return createRow(eObject, feature);
      }
    }

    return null;
  }

  private Row createEContainerRow(EObject eObject)
  {
    return createRow(E_CONTAINER, eObject.eContainer(), EObject.class.getName(), CATEGORY, foreground);
  }

  private Row createEURIRow(EObject eObject)
  {
    return createRow(E_URI, EcoreUtil.getURI(eObject), URI.class.getName(), CATEGORY, foreground);
  }

  private Row createEResourceRow(EObject eObject)
  {
    return createRow(E_RESOURCE, eObject.eResource(), Resource.class.getName(), CATEGORY, foreground);
  }

  private Row createEClassRow(EObject eObject)
  {
    return createRow(E_CLASS, eObject.eClass(), EClass.class.getName(), CATEGORY, foreground);
  }

  private Row createEIsProxyRow(EObject eObject)
  {
    return createRow(E_IS_PROXY, eObject.eIsProxy(), boolean.class.getName(), CATEGORY, foreground);
  }

  private Row createEDeliverRow(EObject eObject)
  {
    return createRow(E_DELIVER, eObject.eDeliver(), boolean.class.getName(), CATEGORY, foreground);
  }

  private Row createEAdaptersRow(EObject eObject)
  {
    return createRow(E_ADAPTERS, eObject.eAdapters(), "EList<Adapter>", CATEGORY, foreground);
  }

  private static Row createRow(EObject eObject, EStructuralFeature feature)
  {
    Object value = eObject.eGet(feature);
    return new Row(feature.getName(), value, getDeclaredType(feature), getConcreteType(value));
  }

  protected static Row createRow(String name, Object value, String declaredType, int category, Color foreground)
  {
    return new Row(name, value, declaredType, getConcreteType(value), category, foreground, null);
  }

  private static String getDeclaredType(EStructuralFeature feature)
  {
    String result = feature.getEType().getName();

    int lowerBound = feature.getLowerBound();
    int upperBound = feature.getUpperBound();
    if (lowerBound != 0 || upperBound != 1)
    {
      result += "[" + lowerBound + "..";

      if (upperBound == -1)
      {
        result += "*";
      }
      else
      {
        result += upperBound;
      }

      result += "]";
    }

    return result;
  }

  private static String getConcreteType(Object value)
  {
    if (value instanceof EObject)
    {
      EObject eObject = (EObject)value;
      return eObject.eClass().getName();
    }

    return getClassName(value);
  }
}
