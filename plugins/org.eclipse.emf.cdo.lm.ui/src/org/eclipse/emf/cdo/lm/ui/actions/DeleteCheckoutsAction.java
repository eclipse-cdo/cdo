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
package org.eclipse.emf.cdo.lm.ui.actions;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.DeleteElementsDialog;
import org.eclipse.emf.cdo.internal.explorer.AbstractElement;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.internal.client.LMManager;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public abstract class DeleteCheckoutsAction<CONTEXT extends CDOObject> extends LMAction<CONTEXT>
{
  private AbstractElement[] checkouts;

  private boolean deleteContents;

  private DeleteCheckoutsAction(IWorkbenchPage page, CONTEXT context, String contextTypeName)
  {
    super(page, //
        "Delete Checkouts" + INTERACTIVE, //
        "Delete the checkouts of the selected '" + contextTypeName.toLowerCase() + "'", //
        OM.getImageDescriptor("icons/Delete.gif"), //
        "Delete the checkouts of the selected '" + contextTypeName.toLowerCase() + "'.", //
        null, //
        context);
  }

  public boolean contributeIfNeeded(IMenuManager manager)
  {
    checkouts = collectCheckouts(getContext());
    if (checkouts.length != 0)
    {
      manager.add(this);
      return true;
    }

    return false;
  }

  @Override
  protected boolean isDialogNeeded()
  {
    return false;
  }

  @Override
  protected void fillDialogArea(LMDialog dialog, Composite parent)
  {
    // Do nothing.
  }

  @Override
  protected void preRun() throws Exception
  {
    if (checkouts == null)
    {
      checkouts = collectCheckouts(getContext());
    }

    DeleteElementsDialog dialog = new DeleteElementsDialog(getShell(), checkouts);
    if (dialog.open() == DeleteElementsDialog.OK)
    {
      deleteContents = dialog.isDeleteContents();
    }
    else
    {
      cancel();
    }
  }

  private AbstractElement[] collectCheckouts(CONTEXT context)
  {
    List<AbstractElement> result = new ArrayList<>();
    String propertyName = getLMPropertyName();
    String propertyValue = getLMPropertyValue(context);

    for (CDOCheckout checkout : CDOExplorerUtil.getCheckoutManager().getCheckouts())
    {
      Properties properties = LMManager.loadLMProperties(checkout);
      if (properties != null)
      {

        String property = properties.getProperty(propertyName);
        if (Objects.equals(property, propertyValue))
        {
          result.add((AbstractElement)checkout);
        }
      }
    }

    return result.toArray(new AbstractElement[result.size()]);
  }

  protected abstract String getLMPropertyName();

  protected abstract String getLMPropertyValue(CONTEXT context);

  @Override
  protected void doRun(CONTEXT context, IProgressMonitor monitor) throws Exception
  {
    for (AbstractElement checkout : checkouts)
    {
      checkout.delete(deleteContents);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class OfSystem extends DeleteCheckoutsAction<org.eclipse.emf.cdo.lm.System>
  {
    public OfSystem(IWorkbenchPage page, System system)
    {
      super(page, system, "system");
    }

    @Override
    protected String getLMPropertyName()
    {
      return "systemName";
    }

    @Override
    protected String getLMPropertyValue(org.eclipse.emf.cdo.lm.System system)
    {
      return system.getName();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class OfModule extends DeleteCheckoutsAction<org.eclipse.emf.cdo.lm.Module>
  {
    public OfModule(IWorkbenchPage page, org.eclipse.emf.cdo.lm.Module module)
    {
      super(page, module, "module");
    }

    @Override
    protected String getLMPropertyName()
    {
      return "moduleName";
    }

    @Override
    protected String getLMPropertyValue(org.eclipse.emf.cdo.lm.Module module)
    {
      return module.getName();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class OfBaseline extends DeleteCheckoutsAction<Baseline>
  {
    public OfBaseline(IWorkbenchPage page, Baseline baseline)
    {
      super(page, baseline, "baseline");
    }

    @Override
    protected String getLMPropertyName()
    {
      return "baselineID";
    }

    @Override
    protected String getLMPropertyValue(Baseline baseline)
    {
      return CDOExplorerUtil.getCDOIDString(baseline.cdoID());
    }
  }
}
