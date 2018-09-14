/*
 * Copyright (c) 2004-2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.evolution.util;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.ListIterator;

/**
 * @author Eike Stepper
 */
public class IDAnnotation
{
  public static final String SOURCE = "http://www.eclipse.org/CDO/evolution/ID";

  public static final String VALUE_KEY = "value";

  public static final String OLD_VALUE_KEY = "oldValue";

  public static EAnnotation getFrom(EModelElement element, boolean createOnDemand)
  {
    EList<EAnnotation> annotations = element.getEAnnotations();
    for (ListIterator<EAnnotation> it = annotations.listIterator(); it.hasNext();)
    {
      EAnnotation annotation = it.next();
      if (SOURCE.equals(annotation.getSource()))
      {
        return annotation;
      }
    }

    if (createOnDemand)
    {
      EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
      annotation.setSource(IDAnnotation.SOURCE);
      annotations.add(annotation);
      return annotation;
    }

    return null;
  }

  public static boolean removeFrom(EModelElement element)
  {
    boolean removed = false;

    for (ListIterator<EAnnotation> it = element.getEAnnotations().listIterator(); it.hasNext();)
    {
      EAnnotation annotation = it.next();
      if (IDAnnotation.SOURCE.equals(annotation.getSource()))
      {
        it.remove();
        removed = true;
      }
    }

    return removed;
  }

  public static String getOldValue(EModelElement element)
  {
    EAnnotation annotation = getFrom(element, false);
    if (annotation != null)
    {
      return annotation.getDetails().get(OLD_VALUE_KEY);
    }

    return null;
  }

  public static String setOldValue(EModelElement element, String oldValue)
  {
    EAnnotation annotation = getFrom(element, true);
    if (oldValue == null || oldValue.length() == 0)
    {
      oldValue = annotation.getDetails().get(OLD_VALUE_KEY);
      annotation.getDetails().remove(OLD_VALUE_KEY);
      return oldValue;
    }

    return annotation.getDetails().put(OLD_VALUE_KEY, oldValue);
  }

  public static String getValue(EModelElement element)
  {
    return getValue(element, false);
  }

  public static String getValue(EModelElement element, boolean considerOldValue)
  {
    EAnnotation annotation = getFrom(element, false);
    if (annotation != null)
    {
      EMap<String, String> details = annotation.getDetails();
      if (considerOldValue)
      {
        String oldValue = details.get(OLD_VALUE_KEY);
        if (oldValue != null && oldValue.length() != 0)
        {
          return oldValue;
        }
      }

      return details.get(VALUE_KEY);
    }

    return null;
  }

  public static String setValue(EModelElement element, String value)
  {
    EAnnotation annotation = getFrom(element, true);
    if (value == null || value.length() == 0)
    {
      value = annotation.getDetails().get(VALUE_KEY);
      annotation.getDetails().remove(VALUE_KEY);
      return value;
    }

    return annotation.getDetails().put(VALUE_KEY, value);
  }

  public static String ensureValue(EModelElement element)
  {
    EAnnotation annotation = getFrom(element, true);
    EMap<String, String> details = annotation.getDetails();

    String value = details.get(VALUE_KEY);
    if (value != null && value.length() != 0)
    {
      return null;
    }

    value = EcoreUtil.generateUUID();
    details.put(VALUE_KEY, value);
    return value;
  }
}
