/*
 * Copyright (c) 2008, 2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.spi.net4j;

import org.eclipse.net4j.util.factory.Factory;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 */
public abstract class ConnectorFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.connectors"; //$NON-NLS-1$

  public ConnectorFactory(String type)
  {
    super(PRODUCT_GROUP, type);
  }
}
