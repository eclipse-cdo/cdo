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
package org.eclipse.net4j.util.factory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Eike Stepper
 */
public class FactoryDescriptor extends Factory
{
  private static final String ATTR_PRODUCT_GROUP = "productGroup";

  private static final String ATTR_TYPE = "type";

  private static final String ATTR_CLASS = "class";

  private IConfigurationElement configurationElement;

  public FactoryDescriptor(IConfigurationElement configurationElement)
  {
    super(createFactoryKey(configurationElement));
    this.configurationElement = configurationElement;
  }

  public IConfigurationElement getConfigurationElement()
  {
    return configurationElement;
  }

  public IFactory createFactory()
  {
    try
    {
      return (IFactory)configurationElement.createExecutableExtension(ATTR_CLASS);
    }
    catch (CoreException ex)
    {
      throw new FactoryCreationException(ex);
    }
  }

  public Object create(String description)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getDescriptionFor(Object product)
  {
    throw new UnsupportedOperationException();
  }

  private static FactoryKey createFactoryKey(IConfigurationElement element)
  {
    String productGroup = element.getAttribute(ATTR_PRODUCT_GROUP);
    String type = element.getAttribute(ATTR_TYPE);
    return new FactoryKey(productGroup, type);
  }
}
