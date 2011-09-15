/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
