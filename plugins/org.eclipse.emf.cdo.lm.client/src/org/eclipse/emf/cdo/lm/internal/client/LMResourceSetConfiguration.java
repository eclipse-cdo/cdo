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
package org.eclipse.emf.cdo.lm.internal.client;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.common.util.ResourceSetConfigurer.Registry.ResourceSetConfiguration;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.lm.assembly.Assembly;
import org.eclipse.emf.cdo.lm.assembly.AssemblyModule;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewProviderRegistry;

import org.eclipse.net4j.util.lifecycle.IDeactivateable;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class LMResourceSetConfiguration implements IDeactivateable
{
  private final Map<String, CDOView> moduleViews = new HashMap<>();

  private final IAssemblyDescriptor assemblyDescriptor;

  private final ResourceSet resourceSet;

  public LMResourceSetConfiguration(IAssemblyDescriptor assemblyDescriptor, ResourceSet resourceSet)
  {
    this.assemblyDescriptor = assemblyDescriptor;
    this.resourceSet = resourceSet;

    Assembly assembly = assemblyDescriptor.getAssembly();
    assembly.forEachDependency(module -> {
      addView(module);
    });

    CDOCheckout checkout = getCheckout();
    checkout.waitUntilPrefetched();
  }

  public IAssemblyDescriptor getAssemblyDescriptor()
  {
    return assemblyDescriptor;
  }

  public Assembly getAssembly()
  {
    return assemblyDescriptor.getAssembly();
  }

  public CDOCheckout getCheckout()
  {
    return assemblyDescriptor.getCheckout();
  }

  public ResourceSet getResourceSet()
  {
    return resourceSet;
  }

  public CDOView getView(AssemblyModule assemblyModule)
  {
    return getView(assemblyModule.getName());
  }

  public CDOView getView(String moduleName)
  {
    return moduleViews.get(moduleName);
  }

  @Override
  public Exception deactivate()
  {
    try
    {
      for (CDOView view : moduleViews.values())
      {
        view.close();
      }

      moduleViews.clear();
    }
    finally
    {
      ((AssemblyDescriptor)assemblyDescriptor).removeResourceSet(this);
    }

    return null;
  }

  @Override
  public String toString()
  {
    return "LMResourceSetConfiguration[" + assemblyDescriptor + " --> " + resourceSet + "]";
  }

  public void reconfigure(List<BranchPointDelta> deltas)
  {
    for (BranchPointDelta delta : deltas)
    {
      AssemblyModule module = delta.getModule();
      if (!module.isRoot())
      {
        switch (delta.getKind())
        {
        case ADDITION:
          addView(module);
          break;

        case REMOVAL:
          removeView(module);
          break;

        case MODIFICATION:
          modifyView(module, delta.getNewBranchPoint());
          break;
        }
      }
    }

    CDOCheckout checkout = getCheckout();
    checkout.waitUntilPrefetched();
  }

  private CDOView addView(AssemblyModule module)
  {
    ISystemDescriptor systemDescriptor = assemblyDescriptor.getSystemDescriptor();

    CDOView view = openView(systemDescriptor, module, resourceSet);
    if (view != null)
    {
      moduleViews.put(module.getName(), view);
    }

    return view;
  }

  private CDOView removeView(AssemblyModule module)
  {
    CDOView view = moduleViews.remove(module.getName());
    if (view != null)
    {
      view.close();
    }

    return view;
  }

  private CDOView modifyView(AssemblyModule module, CDOBranchPointRef branchPointRef)
  {
    CDOView view = moduleViews.get(module.getName());
    if (view != null)
    {
      CDOBranchPoint branchPoint = branchPointRef.resolve(view.getSession().getBranchManager());
      view.setBranchPoint(branchPoint);
    }

    return view;
  }

  public static LMResourceSetConfiguration of(ResourceSet resourceSet)
  {
    ResourceSetConfiguration resourceSetConfiguration = ResourceSetConfiguration.of(resourceSet);
    if (resourceSetConfiguration == null)
    {
      return null;
    }

    Map<String, Object> configurerResults = resourceSetConfiguration.getConfigurerResults();
    return (LMResourceSetConfiguration)configurerResults.get(LMResourceSetConfigurer.TYPE);
  }

  public static CDOView openView(ISystemDescriptor systemDescriptor, AssemblyModule module, ResourceSet resourceSet)
  {
    URI viewURI = LMViewProvider.createViewURI(module);
    return CDOViewProviderRegistry.INSTANCE.provideView(viewURI, resourceSet);
  }

  /**
   * @author Eike Stepper
   */
  public static final class BranchPointDelta
  {
    private final AssemblyModule module;

    private final CDOBranchPointRef oldBranchPoint;

    private final CDOBranchPointRef newBranchPoint;

    public BranchPointDelta(AssemblyModule module, //
        CDOBranchPointRef oldBranchPoint, //
        CDOBranchPointRef newBranchPoint)
    {
      this.module = module;
      this.oldBranchPoint = oldBranchPoint;
      this.newBranchPoint = newBranchPoint;
    }

    public AssemblyModule getModule()
    {
      return module;
    }

    public CDOBranchPointRef getOldBranchPoint()
    {
      return oldBranchPoint;
    }

    public CDOBranchPointRef getNewBranchPoint()
    {
      return newBranchPoint;
    }

    public Kind getKind()
    {
      if (oldBranchPoint == null)
      {
        return Kind.ADDITION;
      }

      if (newBranchPoint == null)
      {
        return Kind.REMOVAL;
      }

      return Kind.MODIFICATION;
    }

    @Override
    public String toString()
    {
      return "BranchPointDelta[module=" + module + //
          ", oldBranchPoint=" + oldBranchPoint + //
          ", newBranchPoint=" + newBranchPoint + "]";
    }

    /**
     * @author Eike Stepper
     */
    public static enum Kind
    {
      ADDITION, REMOVAL, MODIFICATION
    }
  }
}
