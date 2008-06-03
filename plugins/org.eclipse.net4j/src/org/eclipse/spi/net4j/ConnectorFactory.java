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
package org.eclipse.spi.net4j;

import org.eclipse.net4j.util.factory.Factory;

/**
 * @author Eike Stepper
 */
public abstract class ConnectorFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.connectors";

  public ConnectorFactory(String type)
  {
    super(PRODUCT_GROUP, type);
  }
}
