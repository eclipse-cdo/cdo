/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 * 
 */
package org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features;

import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.helper.DawnEditorHelper;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Martin Fluegge
 */
public class AcoreRenameAClassFeature extends AbstractCustomFeature
{
  private boolean hasDoneChanges = false;

  public AcoreRenameAClassFeature(IFeatureProvider fp)
  {
    super(fp);
  }

  @Override
  public String getName()
  {
    return "Rename AClass";
  }

  @Override
  public String getDescription()
  {
    return "Change the name of the AClass";
  }

  @Override
  public boolean canExecute(ICustomContext context)
  {
    // allow rename if exactly one pictogram element
    // representing a AClass is selected
    boolean ret = false;
    PictogramElement[] pes = context.getPictogramElements();
    if (pes != null && pes.length == 1)
    {
      Object bo = getBusinessObjectForPictogramElement(pes[0]);
      if (bo instanceof AClass)
      {
        ret = true;
      }
    }
    return ret;
  }

  public void execute(ICustomContext context)
  {
    PictogramElement[] pes = context.getPictogramElements();
    if (pes != null && pes.length == 1)
    {
      Object bo = getBusinessObjectForPictogramElement(pes[0]);
      if (bo instanceof AClass)
      {
        AClass AClass = (AClass)bo;
        String currentName = AClass.getName();
        // ask user for a new class name
        String newName = askString(getName(), getDescription(), currentName);
        if (newName != null && !newName.equals(currentName))
        {
          hasDoneChanges = true;
          AClass.setName(newName);
          updatePictogramElement(pes[0]);
        }
      }
    }
  }

  @Override
  public boolean hasDoneChanges()
  {
    return hasDoneChanges;
  }

  public String askString(String dialogTitle, String dialogMessage, String initialValue)
  {
    String ret = null;
    Shell shell = DawnEditorHelper.getActiveShell();
    InputDialog inputDialog = new InputDialog(shell, dialogTitle, dialogMessage, initialValue, null);
    int retDialog = inputDialog.open();
    if (retDialog == Window.OK)
    {
      ret = inputDialog.getValue();
    }
    return ret;
  }
}
