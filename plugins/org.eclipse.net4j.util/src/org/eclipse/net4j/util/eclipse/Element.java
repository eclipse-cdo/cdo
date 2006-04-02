/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.eclipse;


import org.eclipse.net4j.util.BeanHelper;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;


public class Element extends BeanHelper
{
  protected IConfigurationElement configurationElement;

  public Element()
  {
  }

  public void internalSetConfigurationElement(IConfigurationElement configurationElement)
  {
    this.configurationElement = configurationElement;
  }

  public void validate(ExtensionParser parser)
  {
  }

  public IConfigurationElement configurationElement()
  {
    return configurationElement;
  }

  public IExtension declaringExtension()
  {
    return configurationElement.getDeclaringExtension();
  }

  public String name()
  {
    return configurationElement.getName();
  }

  public void dispatchChild(String name, Object value)
  {
    dispatchChild(this, name, value);
  }

  public void dispatchAttributeValue(String name, String value)
  {
    dispatchAttributeValue(this, name, value);
  }

  protected String executableExtensionPropertyName()
  {
    return "className";
  }


  public interface Factory
  {
    public Element createElementData();
  }
}
