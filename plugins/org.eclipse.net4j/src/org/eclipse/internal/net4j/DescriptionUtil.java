/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j;

/**
 * @author Eike Stepper
 */
@Deprecated
public final class DescriptionUtil
{
  public static final String SEPARATOR = ":"; //$NON-NLS-1$

  public static final int TYPE_INDEX = 0;

  private DescriptionUtil()
  {
  }

  public static String getType(String description)
  {
    return getElement(description, TYPE_INDEX);
  }

  public static String getElement(String description, int index)
  {
    String[] elements = getElements(description);
    return elements[index];
  }

  public static String[] getElements(String description)
  {
    return description.split(SEPARATOR);
  }

  public static String getDescription(String type, Object[] elements)
  {
    StringBuilder builder = new StringBuilder();
    builder.append(type);

    for (Object element : elements)
    {
      builder.append(":");
      if (element != null)
      {
        builder.append(element);
      }
    }

    return builder.toString();
  }
}
