/*
 * Copyright (c) 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests.data;

import java.util.StringTokenizer;

/**
 * @author Eike Stepper
 */
public final class TinyData
{
  public static final String NL = System.getProperty("line.separator"); //$NON-NLS-1$

  public static StringTokenizer getTokenizer()
  {
    return new StringTokenizer(getText(), NL);
  }

  public static String[] getArray()
  {
    return getText().split(NL);
  }

  public static byte[] getBytes()
  {
    return getText().getBytes();
  }

  public static String getText()
  {
    return "COPYRIGHT (C) 2004 - 2008 EIKE STEPPER, GERMANY. ALL RIGHTS RESERVED."; //$NON-NLS-1$
  }
}
