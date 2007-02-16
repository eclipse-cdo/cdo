/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.jvm;

import org.eclipse.internal.net4j.transport.DescriptionUtil;

/**
 * @author Eike Stepper
 */
public final class JVMUtil
{
  private JVMUtil()
  {
  }

  public static String createAcceptorDescription()
  {
    return createAcceptorDescription(JVMConstants.DEFAULT_NAME);
  }

  public static String createAcceptorDescription(String name)
  {
    Object[] elements = { name };
    return DescriptionUtil.getDescription(JVMConstants.TYPE, elements);
  }

  public static String createConnectorDescription()
  {
    return createConnectorDescription(JVMConstants.DEFAULT_NAME);
  }

  public static String createConnectorDescription(String name)
  {
    return createConnectorDescription(null, name);
  }

  public static String createConnectorDescription(String userName, String name)
  {
    Object[] elements = { userName, name };
    return DescriptionUtil.getDescription(JVMConstants.TYPE, elements);
  }
}
