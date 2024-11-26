/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.util.ResourceSetConfigurer;
import org.eclipse.emf.cdo.common.util.ResourceSetConfigurer.Registry.ResourceSetConfiguration;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.lm.assembly.Assembly;
import org.eclipse.emf.cdo.lm.assembly.AssemblyModule;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor.ResolutionException;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.lifecycle.IDeactivateable;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.ViewResourceSetConfigurer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class LMResourceSetConfigurer extends ViewResourceSetConfigurer
{
  public static final String TYPE = "lm";

  private static final ThreadLocal<Boolean> BYPASS_CONFIGURE = new ThreadLocal<>();

  public LMResourceSetConfigurer()
  {
  }

  @Override
  protected Result configureViewResourceSet(ResourceSet resourceSet, CDOView view)
  {
    if (BYPASS_CONFIGURE.get() == Boolean.TRUE)
    {
      return null;
    }

    CDOSession session = view.getSession();
    ISystemDescriptor systemDescriptor = SystemDescriptor.getSystemDescriptor(session);
    if (systemDescriptor == null)
    {
      return null;
    }

    String moduleName = SystemDescriptor.getModuleName(session);
    if (moduleName == null)
    {
      return null;
    }

    return configureViewResourceSet(resourceSet, view, systemDescriptor);
  }

  private Result configureViewResourceSet(ResourceSet resourceSet, CDOView view, ISystemDescriptor systemDescriptor)
  {
    CDOCheckout checkout = CDOExplorerUtil.getCheckout(view);
    if (checkout != null)
    {
      CheckoutResult result = new CheckoutResult(resourceSet, systemDescriptor);

      IAssemblyDescriptor assemblyDescriptor = IAssemblyManager.INSTANCE.getDescriptor(checkout);
      if (assemblyDescriptor != null)
      {
        result.setAssemblyDescriptor(assemblyDescriptor);
      }
      else
      {
        // The checkout is probably just opening.
        IAssemblyManager.INSTANCE.addListener(new ContainerEventAdapter<IAssemblyDescriptor>()
        {
          @Override
          protected void onAdded(IContainer<IAssemblyDescriptor> container, IAssemblyDescriptor assemblyDescriptor)
          {
            if (assemblyDescriptor.getCheckout() == checkout)
            {
              result.setAssemblyDescriptor(assemblyDescriptor);
            }
          }
        });
      }

      return result;
    }

    try
    {
      Map<String, CDOView> moduleViews = systemDescriptor.configureModuleResourceSet(view);
      return new StandaloneResult(resourceSet, systemDescriptor, moduleViews);
    }
    catch (ResolutionException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static void bypassConfigure(boolean on)
  {
    if (on)
    {
      BYPASS_CONFIGURE.set(Boolean.TRUE);
    }
    else
    {
      BYPASS_CONFIGURE.remove();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Result implements IDeactivateable
  {
    private final ResourceSet resourceSet;

    private final ISystemDescriptor systemDescriptor;

    private final Map<String, CDOView> dependencyViews = new HashMap<>();

    public Result(ResourceSet resourceSet, ISystemDescriptor systemDescriptor)
    {
      this.resourceSet = resourceSet;
      this.systemDescriptor = systemDescriptor;
    }

    public ResourceSet getResourceSet()
    {
      return resourceSet;
    }

    public CDOView getDependencyView(AssemblyModule assemblyModule)
    {
      return getDependencyView(assemblyModule.getName());
    }

    public CDOView getDependencyView(String moduleName)
    {
      return dependencyViews.get(moduleName);
    }

    @Override
    public Exception deactivate()
    {
      Exception exception = null;

      for (CDOView view : dependencyViews.values())
      {
        try
        {
          view.close();
        }
        catch (Exception ex)
        {
          if (exception == null)
          {
            exception = ex;
          }
        }
      }

      dependencyViews.clear();
      return exception;
    }

    protected final void registerDependencyView(String moduleName, CDOView view)
    {
      dependencyViews.put(moduleName, view);
    }

    protected final CDOView addDependencyView(AssemblyModule module)
    {
      CDOView view = openDependencyView(systemDescriptor, module, resourceSet);
      if (view != null)
      {
        registerDependencyView(module.getName(), view);
      }

      return view;
    }

    protected final CDOView removeDependencyView(AssemblyModule module)
    {
      CDOView view = dependencyViews.remove(module.getName());
      if (view != null)
      {
        view.close();
      }

      return view;
    }

    protected final CDOView modifyDependencyView(AssemblyModule module, CDOBranchPointRef branchPointRef)
    {
      CDOView view = getDependencyView(module.getName());
      if (view != null)
      {
        CDOBranchPoint branchPoint = branchPointRef.resolve(view.getSession().getBranchManager());
        view.setBranchPoint(branchPoint);
      }

      return view;
    }

    public static Result of(ResourceSet resourceSet)
    {
      ResourceSetConfiguration resourceSetConfiguration = ResourceSetConfiguration.of(resourceSet);
      if (resourceSetConfiguration == null)
      {
        return null;
      }

      Map<String, Object> configurerResults = resourceSetConfiguration.getConfigurerResults();
      return (Result)configurerResults.get(TYPE);
    }

    public static CDOView openDependencyView(ISystemDescriptor systemDescriptor, AssemblyModule module, ResourceSet resourceSet)
    {
      bypassConfigure(true);
      
      try
      {
        URI viewURI = LMViewProvider.createViewURI(module);
        return CDOUtil.getView(resourceSet, viewURI);
      }
      finally
      {
        bypassConfigure(false);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class StandaloneResult extends Result
  {
    public StandaloneResult(ResourceSet resourceSet, ISystemDescriptor systemDescriptor, Map<String, CDOView> moduleViews)
    {
      super(resourceSet, systemDescriptor);
      moduleViews.forEach(this::registerDependencyView);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class CheckoutResult extends Result
  {
    private IAssemblyDescriptor assemblyDescriptor;

    public CheckoutResult(ResourceSet resourceSet, ISystemDescriptor systemDescriptor)
    {
      super(resourceSet, systemDescriptor);
    }

    public void setAssemblyDescriptor(IAssemblyDescriptor assemblyDescriptor)
    {
      this.assemblyDescriptor = assemblyDescriptor;
      ((AssemblyDescriptor)assemblyDescriptor).addResourceSet(this);

      Assembly assembly = getAssembly();
      assembly.forEachDependency(this::addDependencyView);

      CDOCheckout checkout = getCheckout();
      checkout.waitUntilPrefetched();
    }

    public Assembly getAssembly()
    {
      return assemblyDescriptor.getAssembly();
    }

    public CDOCheckout getCheckout()
    {
      return assemblyDescriptor.getCheckout();
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
            addDependencyView(module);
            break;

          case REMOVAL:
            removeDependencyView(module);
            break;

          case MODIFICATION:
            modifyDependencyView(module, delta.getNewBranchPoint());
            break;
          }
        }
      }

      CDOCheckout checkout = getCheckout();
      checkout.waitUntilPrefetched();
    }

    @Override
    public Exception deactivate()
    {
      try
      {
        super.deactivate();
      }
      finally
      {
        ((AssemblyDescriptor)assemblyDescriptor).removeResourceSet(this);
      }

      return null;
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

  /**
   * @author Eike Stepper
   */
  public static class Factory extends ResourceSetConfigurer.Factory
  {
    public Factory()
    {
      super(TYPE);
    }

    @Override
    public LMResourceSetConfigurer create(String description) throws ProductCreationException
    {
      return new LMResourceSetConfigurer();
    }
  }
}
