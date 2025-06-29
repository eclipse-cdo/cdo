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

/**
 * @author Eike Stepper
 */
public class LMModuleCheckoutProperties extends AbstractLMProperties<IAssemblyDescriptor>
{
  public static final IProperties<IAssemblyDescriptor> INSTANCE = new LMModuleCheckoutProperties();

  public static final String CATEGORY_CHECKOUT = "Module"; //$NON-NLS-1$

  private LMModuleCheckoutProperties()
  {
    super(IAssemblyDescriptor.class);
  }

  @Override
  protected IAssemblyDescriptor getAssemblyDescriptor(IAssemblyDescriptor descriptor)
  {
    return descriptor;
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
