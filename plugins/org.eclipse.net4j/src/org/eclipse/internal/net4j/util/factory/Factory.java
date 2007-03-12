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
package org.eclipse.internal.net4j.util.factory;

import org.eclipse.net4j.util.factory.IFactory;

/**
 * @author Eike Stepper
 */
public abstract class Factory<PRODUCT> implements IFactory<PRODUCT>
{
  private FactoryKey key;

  public Factory(String productGroup, String type)
  {
    key = new FactoryKey(productGroup, type);
  }

  public FactoryKey getKey()
  {
    return key;
  }

  public String getFactoryType()
  {
    return key.getFactoryType();
  }

  public String getProductGroup()
  {
    return key.getProductGroup();
  }

  public String toString()
  {
    return key.toString();
  }
}
