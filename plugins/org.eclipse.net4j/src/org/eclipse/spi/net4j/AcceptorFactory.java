/*
 * Copyright (c) 2008, 2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
public abstract class AcceptorFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.acceptors"; //$NON-NLS-1$

  public AcceptorFactory(String type)
  {
    super(PRODUCT_GROUP, type);
  }
}
