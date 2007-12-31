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
package org.eclipse.net4j.jvm;

import org.eclipse.net4j.internal.jvm.JVMAcceptorFactory;
import org.eclipse.net4j.internal.jvm.JVMConnectorFactory;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public final class JVMUtil
{
  private JVMUtil()
  {
  }

  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new JVMAcceptorFactory());
    container.registerFactory(new JVMConnectorFactory());
  }

  public static IJVMAcceptor getAcceptor(IManagedContainer container, String description)
  {
    return (IJVMAcceptor)container.getElement(JVMAcceptorFactory.PRODUCT_GROUP, JVMAcceptorFactory.TYPE, description);
  }

  public static IJVMConnector getConnector(IManagedContainer container, String description)
  {
    return (IJVMConnector)container
        .getElement(JVMConnectorFactory.PRODUCT_GROUP, JVMConnectorFactory.TYPE, description);
  }
}
