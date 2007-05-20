/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.util.factory;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.IFactoryKey;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public final class FactoryKey implements IFactoryKey, Serializable, Comparable
{
  private static final long serialVersionUID = 1L;

  private String productGroup;

  private String type;

  public FactoryKey(String productGroup, String type)
  {
    this.productGroup = productGroup;
    this.type = type;
  }

  public String getProductGroup()
  {
    return productGroup;
  }

  public String getType()
  {
    return type;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof FactoryKey)
    {
      FactoryKey key = (FactoryKey)obj;
      return ObjectUtil.equals(productGroup, key.productGroup) && ObjectUtil.equals(type, key.type);
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return ObjectUtil.hashCode(productGroup) ^ ObjectUtil.hashCode(type);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}[{1}]", productGroup, type);
  }

  public int compareTo(Object o)
  {
    if (o instanceof FactoryKey)
    {
      FactoryKey key = (FactoryKey)o;
      int result = StringUtil.compare(productGroup, key.productGroup);
      if (result == 0)
      {
        result = StringUtil.compare(type, key.type);
      }

      return result;
    }

    return 0;
  }
}