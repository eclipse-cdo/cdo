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

      addRow(rows, "eContainer", eObject.eContainer(), EObject.class.getName(), CATEGORY, foreground);
      addRow(rows, "uri", EcoreUtil.getURI(eObject), URI.class.getName(), CATEGORY, foreground);
      addRow(rows, "resource", eObject.eResource(), Resource.class.getName(), CATEGORY, foreground);
      addRow(rows, "eClass", eObject.eClass(), EClass.class.getName(), CATEGORY, foreground);
      addRow(rows, "eIsProxy", eObject.eIsProxy(), boolean.class.getName(), CATEGORY, foreground);
      addRow(rows, "eDeliver", eObject.eDeliver(), boolean.class.getName(), CATEGORY, foreground);

      for (EStructuralFeature feature : eObject.eClass().getEAllStructuralFeatures())
      {
        Object value = eObject.eGet(feature);
        rows.add(new Row(feature.getName(), value, feature.getEType().getName(), getConcreteType(value)));
      }
    }
  }

  protected static void addRow(List<Row> rows, String name, Object value, String declaredType, int category, Color foreground)
  {
    rows.add(new Row(name, value, declaredType, getConcreteType(value), category, foreground, null));
  }

  private static String getConcreteType(Object value)
  {
    if (value instanceof EObject)
    {
      EObject eObject = (EObject)value;
      return eObject.eClass().getName();
    }

    return value == null ? "" : value.getClass().getName(); //$NON-NLS-1$
  }
}
