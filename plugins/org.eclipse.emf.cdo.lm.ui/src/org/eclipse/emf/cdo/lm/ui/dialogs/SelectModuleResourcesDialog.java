/*
 * Copyright (c) 2022-2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui.dialogs;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.lm.assembly.Assembly;
import org.eclipse.emf.cdo.lm.assembly.AssemblyModule;
import org.eclipse.emf.cdo.lm.internal.client.LMResourceSetConfigurer;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;
import org.eclipse.emf.cdo.ui.AbstractResourceSelectionDialog;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.swt.widgets.Shell;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Eike Stepper
 */
public class SelectModuleResourcesDialog extends AbstractResourceSelectionDialog<LMResourceSetConfigurer.CheckoutResult>
{
  private final LMResourceSetConfigurer.CheckoutResult lmResourceSetConfigurerResult;

  private final Map<AssemblyModule, CDOView> views = new LinkedHashMap<>();

  public SelectModuleResourcesDialog(Shell shell, boolean multi, LMResourceSetConfigurer.CheckoutResult lmResourceSetConfigurerResult)
  {
    super(shell, multi, "Select Module Resources", "Select resources from the current module and its dependencies.",
        OM.Activator.INSTANCE.loadImageDescriptor("icons/NewModule.png"));
    this.lmResourceSetConfigurerResult = lmResourceSetConfigurerResult;

    if (lmResourceSetConfigurerResult != null)
    {
      Assembly assembly = lmResourceSetConfigurerResult.getAssembly();
      AssemblyModule rootModule = assembly.getRootModule();
      views.put(rootModule, CDOUtil.getViewSet(lmResourceSetConfigurerResult.getResourceSet()).getViews()[0]);
      assembly.forEachDependency(module -> views.put(module, lmResourceSetConfigurerResult.getDependencyView(module)));
    }
  }

  @Override
  protected LMResourceSetConfigurer.CheckoutResult getInput()
  {
    return lmResourceSetConfigurerResult;
  }

  @Override
  protected boolean elementHasChildren(Object object, Predicate<Object> defaultHasChildren)
  {
    if (object == views)
    {
      return !views.isEmpty();
    }

    return super.elementHasChildren(object, defaultHasChildren);
  }

  @Override
  protected Object[] elementGetChildren(Object object, Function<Object, Object[]> defaultGetChildren)
  {
    if (object == lmResourceSetConfigurerResult)
    {
      // Return the assembly modules.
      return views.keySet().toArray();
    }

    if (object instanceof AssemblyModule)
    {
      AssemblyModule module = (AssemblyModule)object;
      CDOView view = views.get(module);
      CDOResource rootResource = view.getRootResource();
      return defaultGetChildren.apply(rootResource);
    }

    return super.elementGetChildren(object, defaultGetChildren);
  }

  @Override
  protected Object elementGetParent(Object object, Function<Object, Object> defaultGetParent)
  {
    if (object instanceof CDOResourceNode)
    {
      CDOResourceNode node = (CDOResourceNode)object;
      CDOResourceFolder folder = node.getFolder();

      if (folder == null)
      {
        CDOView view = node.cdoView();

        for (Map.Entry<AssemblyModule, CDOView> entry : views.entrySet())
        {
          if (entry.getValue() == view)
          {
            return entry.getKey();
          }
        }

        return null;
      }

      return folder;
    }

    if (object instanceof AssemblyModule)
    {
      return lmResourceSetConfigurerResult;
    }

    return super.elementGetParent(object, defaultGetParent);
  }

  @Override
  protected String elementGetText(Object object, Function<Object, String> defaultGetText)
  {
    if (object instanceof AssemblyModule)
    {
      AssemblyModule module = (AssemblyModule)object;
      return module.getName() + " " + module.getVersion();
    }

    return super.elementGetText(object, defaultGetText);
  }
}
