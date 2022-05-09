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
package org.eclipse.emf.cdo.lm.ui;

import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.modules.DependencyDefinition;
import org.eclipse.emf.cdo.lm.modules.provider.DependencyDefinitionItemProvider.TargetNameProvider;

import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author Eike Stepper
 */
public class LMTargetNameProvider implements TargetNameProvider
{
  public LMTargetNameProvider()
  {
  }

  @Override
  public Collection<String> getTargetNames(DependencyDefinition dependency)
  {
    IAssemblyDescriptor assemblyDescriptor = IAssemblyManager.INSTANCE.getDescriptor(dependency);
    if (assemblyDescriptor != null)
    {
      ISystemDescriptor systemDescriptor = assemblyDescriptor.getSystemDescriptor();
      String owningName = assemblyDescriptor.getBaseline().getModule().getName();

      List<String> result = new ArrayList<>();

      for (Module module : systemDescriptor.getSystem().getModules())
      {
        String name = module.getName();
        if (!Objects.equals(name, owningName))
        {
          result.add(name);
        }
      }

      result.sort(null);
      return result;
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Factory extends TargetNameProvider.Factory
  {
    public static final String TYPE = "lm";

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public TargetNameProvider create(String description) throws ProductCreationException
    {
      return new LMTargetNameProvider();
    }
  }
}
