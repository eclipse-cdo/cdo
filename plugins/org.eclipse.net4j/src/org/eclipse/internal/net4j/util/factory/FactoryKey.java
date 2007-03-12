package org.eclipse.internal.net4j.util.factory;

import org.eclipse.net4j.util.ObjectUtil;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public final class FactoryKey implements Serializable
{
  private static final long serialVersionUID = 1L;

  private String productGroup;

  private String factoryType;

  public FactoryKey(String productGroup, String factoryType)
  {
    this.productGroup = productGroup;
    this.factoryType = factoryType;
  }

  public String getProductGroup()
  {
    return productGroup;
  }

  public String getFactoryType()
  {
    return factoryType;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof FactoryKey)
    {
      FactoryKey key = (FactoryKey)obj;
      return ObjectUtil.equals(productGroup, key.productGroup) && ObjectUtil.equals(factoryType, key.factoryType);
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return ObjectUtil.hashCode(productGroup) ^ ObjectUtil.hashCode(factoryType);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}[{1}]", productGroup, factoryType);
  }
}