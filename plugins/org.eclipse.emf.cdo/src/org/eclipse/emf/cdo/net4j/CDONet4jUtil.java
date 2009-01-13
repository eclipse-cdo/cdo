/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.net4j;

import org.eclipse.emf.internal.cdo.net4j.CDONet4jSessionConfigurationImpl;
import org.eclipse.emf.internal.cdo.net4j.protocol.CDOClientProtocolFactory;

import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @since 2.0
 * @author Eike Stepper
 */
public final class CDONet4jUtil
{
  private CDONet4jUtil()
  {
  }

  public static CDOSessionConfiguration createSessionConfiguration()
  {
    return new CDONet4jSessionConfigurationImpl();
  }

  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new CDOClientProtocolFactory());
  }
}
