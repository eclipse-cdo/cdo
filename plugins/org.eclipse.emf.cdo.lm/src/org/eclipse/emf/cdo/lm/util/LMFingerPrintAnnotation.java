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
package org.eclipse.emf.cdo.lm.util;

import org.eclipse.emf.cdo.common.util.CDOFingerPrinter.FingerPrint;
import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.EtypesFactory;
import org.eclipse.emf.cdo.etypes.ModelElement;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Eike Stepper
 * @since 1.4
 */
public final class LMFingerPrintAnnotation
{
  public static final String ANNOTATION_SOURCE = "http://www.eclipse.org/CDO/LM/FingerPrint";

  public static final String ANNOTATION_DETAIL_VALUE = "value";

  public static final String ANNOTATION_DETAIL_COUNT = "count";

  public static final String ANNOTATION_DETAIL_PARAM = "param";

  private LMFingerPrintAnnotation()
  {
  }

  public static Annotation create()
  {
    return create(null, null, null);
  }

  public static Annotation create(FingerPrint fingerPrint)
  {
    if (fingerPrint == null)
    {
      return null;
    }

    String value = fingerPrint.getValue();
    String count = Long.toString(fingerPrint.getCount());
    String param = fingerPrint.getParam();

    return create(value, count, param);
  }

  public static Annotation create(String value, String count, String param)
  {
    Annotation annotation = EtypesFactory.eINSTANCE.createAnnotation(ANNOTATION_SOURCE);
    EMap<String, String> details = annotation.getDetails();

    if (!StringUtil.isEmpty(value))
    {
      details.put(ANNOTATION_DETAIL_VALUE, value);
    }

    if (!StringUtil.isEmpty(count))
    {
      details.put(ANNOTATION_DETAIL_COUNT, count);
    }

    if (!StringUtil.isEmpty(param))
    {
      details.put(ANNOTATION_DETAIL_PARAM, param);
    }

    return annotation;
  }

  public static FingerPrint getFingerPrint(EObject object)
  {
    if (object instanceof ModelElement)
    {
      ModelElement modelElement = (ModelElement)object;

      Annotation annotation = modelElement.getAnnotation(ANNOTATION_SOURCE);
      return annotation == null ? null : getFingerPrint(annotation);
    }

    return null;
  }

  public static FingerPrint getFingerPrint(Annotation annotation)
  {
    if (annotation != null && ANNOTATION_SOURCE.equals(annotation.getSource()))
    {
      EMap<String, String> details = annotation.getDetails();

      String value = details.get(ANNOTATION_DETAIL_VALUE);
      long count = getCount(details);
      String param = details.get(ANNOTATION_DETAIL_PARAM);

      return new FingerPrint(value, count, param);
    }

    return null;
  }

  private static long getCount(EMap<String, String> details)
  {
    String count = details.get(ANNOTATION_DETAIL_COUNT);
    if (count != null)
    {
      try
      {
        return Long.parseLong(count);
      }
      catch (NumberFormatException ex)
      {
        //$FALL-THROUGH$
      }
    }

    return 0;
  }
}
