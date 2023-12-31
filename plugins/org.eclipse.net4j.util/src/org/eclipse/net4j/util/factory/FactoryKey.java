/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2019, 2022, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.factory;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * A default implementation of a {@link IFactoryKey factory key}.
 *
 * @author Eike Stepper
 */
public final class FactoryKey implements IFactoryKey, Serializable, Comparable<FactoryKey>
{
  private static final long serialVersionUID = 1L;

  private String productGroup;

  private String type;

  public FactoryKey(String productGroup, String type)
  {
    CheckUtil.checkArg(!StringUtil.isEmpty(productGroup), "productGroup is empty"); //$NON-NLS-1$
    this.productGroup = productGroup;
    this.type = type;
  }

  @Override
  public String getProductGroup()
  {
    return productGroup;
  }

  public void setProductGroup(String productGroup)
  {
    this.productGroup = productGroup;
  }

  @Override
  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof IFactoryKey)
    {
      IFactoryKey key = (IFactoryKey)obj;
      return ObjectUtil.equals(productGroup, key.getProductGroup()) && ObjectUtil.equals(type, key.getType());
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return ObjectUtil.hashCode(productGroup) ^ ObjectUtil.hashCode(type);
  }

  @Override
  public int compareTo(FactoryKey key)
  {
    int result = StringUtil.compare(productGroup, key.productGroup);
    if (result == 0)
    {
      result = StringUtil.compare(type, key.type);
    }

    return result;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}[{1}]", productGroup, type); //$NON-NLS-1$
  }
}
