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
package org.eclipse.emf.cdo.explorer.ui.checkouts.workingsets;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;

import org.eclipse.net4j.util.collection.CollectionUtil;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.IWorkingSetUpdater;
import org.eclipse.ui.PlatformUI;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class OthersWorkingSetUpdater implements IWorkingSetUpdater
{
  public static final String WORKING_SET_ID = "org.eclipse.emf.cdo.explorer.ui.OthersWorkingSet"; //$NON-NLS-1$

  public static final String WORKING_SET_NAME = "Other CDO Checkouts";

  private final IListener checkoutManagerListener = new ContainerEventAdapter<CDOCheckout>()
  {
    @Override
    protected void notifyContainerEvent(IContainerEvent<CDOCheckout> event)
    {
      updateElements();
    }
  };

  private final IPropertyChangeListener workingSetManagerListener = new IPropertyChangeListener()
  {

    @Override
    public void propertyChange(PropertyChangeEvent event)
    {
      if (IWorkingSetManager.CHANGE_WORKING_SET_CONTENT_CHANGE.equals(event.getProperty()))
      {
        IWorkingSet changedWorkingSet = (IWorkingSet)event.getNewValue();
        if (changedWorkingSet != workingSet)
        {
          updateElements();
        }
      }
    }
  };

  private IWorkingSet workingSet;

  public OthersWorkingSetUpdater()
  {
    CDOExplorerUtil.getCheckoutManager().addListener(checkoutManagerListener);
    PlatformUI.getWorkbench().getWorkingSetManager().addPropertyChangeListener(workingSetManagerListener);
  }

  @Override
  public void dispose()
  {
    PlatformUI.getWorkbench().getWorkingSetManager().removePropertyChangeListener(workingSetManagerListener);
    CDOExplorerUtil.getCheckoutManager().addListener(checkoutManagerListener);
  }

  @Override
  public void add(IWorkingSet workingSet)
  {
    Assert.isTrue(this.workingSet == null);
    this.workingSet = workingSet;
  }

  @Override
  public boolean remove(IWorkingSet workingSet)
  {
    Assert.isTrue(workingSet == this.workingSet);
    this.workingSet = null;
    return true;
  }

  @Override
  public boolean contains(IWorkingSet workingSet)
  {
    return workingSet == this.workingSet;
  }

  public void updateElements()
  {
    Assert.isTrue(workingSet != null);

    Set<CDOCheckout> allocatedCheckouts = new HashSet<>();
    for (IWorkingSet workingSet : PlatformUI.getWorkbench().getWorkingSetManager().getAllWorkingSets())
    {
      if (CheckoutWorkingSetWizardPage.WORKING_SET_ID.equals(workingSet.getId()))
      {
        for (IAdaptable adaptable : workingSet.getElements())
        {
          CollectionUtil.addNotNull(allocatedCheckouts, CDOExplorerUtil.getCheckout(adaptable));
        }
      }
    }

    Set<CDOCheckout> result = new LinkedHashSet<>();
    for (CDOCheckout checkout : CDOExplorerUtil.getCheckoutManager().getCheckouts())
    {
      if (!allocatedCheckouts.contains(checkout))
      {
        result.add(checkout);
      }
    }

    CDOCheckout[] newElements = result.toArray(new CDOCheckout[result.size()]);
    IAdaptable[] oldElements = workingSet.getElements();
    if (!Arrays.equals(newElements, oldElements))
    {
      UIUtil.asyncExec(() -> workingSet.setElements(newElements));
    }
  }
}
