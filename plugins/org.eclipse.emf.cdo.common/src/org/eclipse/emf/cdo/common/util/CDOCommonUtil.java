/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.util;

import org.eclipse.emf.cdo.common.revision.CDORevision;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public final class CDOCommonUtil
{
  private CDOCommonUtil()
  {
  }

  public static String formatTimeStamp()
  {
    return formatTimeStamp(System.currentTimeMillis());
  }

  public static String formatTimeStamp(long timeStamp)
  {
    if (timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      return "*";
    }

    return MessageFormat.format("{0,date} {0,time,HH:mm:ss:SSS}", timeStamp);
  }
}
