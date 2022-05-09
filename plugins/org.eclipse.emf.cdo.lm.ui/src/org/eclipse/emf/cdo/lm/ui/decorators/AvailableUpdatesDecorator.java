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
package org.eclipse.emf.cdo.lm.ui.decorators;

import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.assembly.AssemblyModule;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor.AvailableUpdatesChangedEvent;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor.UpdateStateChangedEvent;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor.Updates;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.SelfAttachingContainerListener;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;

/**
 * @author Eike Stepper
 */
public class AvailableUpdatesDecorator extends BaseLabelProvider implements ILightweightLabelDecorator
{
  private final ImageDescriptor updateModification = OM.getImageDescriptor("icons/overlay/UpdateModification.png");

  private final ImageDescriptor updateAddition = OM.getImageDescriptor("icons/overlay/UpdateAddition.png");

  private final ImageDescriptor updateRemoval = OM.getImageDescriptor("icons/overlay/UpdateRemoval.png");

  private final IListener assemblyManagerListener = new SelfAttachingContainerListener()
  {
    @Override
    protected void notifyOtherEvent(IEvent event)
    {
      super.notifyOtherEvent(event);

      if (event instanceof UpdateStateChangedEvent)
      {
        UpdateStateChangedEvent e = (UpdateStateChangedEvent)event;
        IAssemblyDescriptor descriptor = e.getDescriptor();
        fireLabelEvent(descriptor, descriptor.getCheckout());
      }
      else if (event instanceof AvailableUpdatesChangedEvent)
      {
        AvailableUpdatesChangedEvent e = (AvailableUpdatesChangedEvent)event;
        Object[] modules = e.getDescriptor().getModules(true);
        fireLabelEvent(modules);
      }
    }
  };

  public AvailableUpdatesDecorator()
  {
    IAssemblyManager.INSTANCE.addListener(assemblyManagerListener);
  }

  @Override
  public void dispose()
  {
    IAssemblyManager.INSTANCE.removeListener(assemblyManagerListener);
    super.dispose();
  }

  @Override
  public void decorate(Object element, IDecoration decoration)
  {
    if (element instanceof IAssemblyDescriptor)
    {
      IAssemblyDescriptor descriptor = (IAssemblyDescriptor)element;
      if (descriptor.hasUpdatesAvailable())
      {
        decoration.addOverlay(updateModification, IDecoration.TOP_RIGHT);
      }
    }
    else if (element instanceof AssemblyModule)
    {
      AssemblyModule module = (AssemblyModule)element;
      String moduleName = module.getName();

      IAssemblyDescriptor descriptor = IAssemblyManager.INSTANCE.getDescriptor(module);
      if (descriptor != null)
      {
        Updates updates = descriptor.getAvailableUpdates();
        if (updates != null)
        {
          if (updates.getAdditions().containsKey(moduleName))
          {
            decoration.addOverlay(updateAddition, IDecoration.TOP_RIGHT);
          }
          else if (updates.getRemovals().contains(moduleName))
          {
            decoration.addOverlay(updateRemoval, IDecoration.TOP_RIGHT);
          }
          else
          {
            AssemblyModule changedModule = updates.getModifications().get(moduleName);
            if (changedModule != null)
            {
              decoration.addOverlay(updateModification, IDecoration.TOP_RIGHT);
              decoration.addSuffix(" \u2190 " + getSuffix(changedModule));
            }
          }
        }
      }
    }
  }

  private void fireLabelEvent(Object... elements)
  {
    UIUtil.asyncExec(() -> fireLabelProviderChanged(new LabelProviderChangedEvent(this, elements)));
  }

  public static String getSuffix(AssemblyModule module)
  {
    String text = StringUtil.safe(module.getVersion());

    String baseline = CDOUtil.getAnnotation(module, //
        LMPackage.ANNOTATION_SOURCE, //
        LMPackage.ANNOTATION_DETAIL_BASELINE_NAME);
    if (!StringUtil.isEmpty(baseline))
    {
      String type = CDOUtil.getAnnotation(module, //
          LMPackage.ANNOTATION_SOURCE, //
          LMPackage.ANNOTATION_DETAIL_BASELINE_TYPE);
      if (!StringUtil.isEmpty(type))
      {
        baseline = type + " " + baseline;
      }

      text += " [" + baseline + "]";
    }

    return text;
  }
}
