/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.internal.client.properties;

import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;

import org.eclipse.net4j.util.properties.DefaultPropertyTester;
import org.eclipse.net4j.util.properties.IProperties;
import org.eclipse.net4j.util.properties.Properties;
import org.eclipse.net4j.util.properties.Property;

/**
 * @author Eike Stepper
 */
public class ModuleCheckoutProperties extends Properties<IAssemblyDescriptor>
{
  public static final IProperties<IAssemblyDescriptor> INSTANCE = new ModuleCheckoutProperties();

  public static final String CATEGORY_CHECKOUT = "Module"; //$NON-NLS-1$

  private ModuleCheckoutProperties()
  {
    super(IAssemblyDescriptor.class);

    add(new Property<IAssemblyDescriptor>("moduleName")
    {
      @Override
      protected Object eval(IAssemblyDescriptor descriptor)
      {
        return descriptor.getBaseline().getModule().getName();
      }
    });

    add(new Property<IAssemblyDescriptor>("systemName")
    {
      @Override
      protected Object eval(IAssemblyDescriptor descriptor)
      {
        return descriptor.getBaseline().getSystem().getName();
      }
    });

    add(new Property<IAssemblyDescriptor>("baselineName")
    {
      @Override
      protected Object eval(IAssemblyDescriptor descriptor)
      {
        return descriptor.getBaseline().getName();
      }
    });

    add(new Property<IAssemblyDescriptor>("baselineTypeName")
    {
      @Override
      protected Object eval(IAssemblyDescriptor descriptor)
      {
        return descriptor.getBaseline().getTypeName();
      }
    });

    add(new Property<IAssemblyDescriptor>("baselineType")
    {
      @Override
      protected Object eval(IAssemblyDescriptor descriptor)
      {
        return descriptor.getBaseline().eClass().getName();
      }
    });

    add(new Property<IAssemblyDescriptor>("updatesAvailable")
    {
      @Override
      protected Object eval(IAssemblyDescriptor descriptor)
      {
        return descriptor.hasUpdatesAvailable();
      }
    });
  }

  public static void main(String[] args)
  {
    new Tester().dumpContributionMarkup();
  }

  /**
   * @author Eike Stepper
   */
  public static final class Tester extends DefaultPropertyTester<IAssemblyDescriptor>
  {
    public static final String NAMESPACE = "org.eclipse.emf.cdo.lm.assembly";

    public Tester()
    {
      super(NAMESPACE, INSTANCE);
    }
  }
}
