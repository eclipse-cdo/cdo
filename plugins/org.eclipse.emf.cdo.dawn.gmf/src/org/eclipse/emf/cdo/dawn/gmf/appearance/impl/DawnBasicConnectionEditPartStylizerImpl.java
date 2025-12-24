/*
 * Copyright (c) 2011, 2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.gmf.appearance.impl;

import org.eclipse.emf.cdo.dawn.gmf.appearance.DawnAppearancer;
import org.eclipse.emf.cdo.dawn.gmf.appearance.DawnEditPartStylizer;
import org.eclipse.emf.cdo.dawn.spi.DawnState;
import org.eclipse.emf.cdo.dawn.ui.DawnColorConstants;

import org.eclipse.emf.workspace.AbstractEMFOperation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.common.core.util.StringStatics;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.ChangePropertyValueRequest;
import org.eclipse.gmf.runtime.draw2d.ui.figures.FigureUtilities;
import org.eclipse.gmf.runtime.emf.core.util.PackageUtil;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.swt.graphics.Color;

/**
 * @author Martin Fluegge
 * @since 2.0
 */
public class DawnBasicConnectionEditPartStylizerImpl extends DawnEditPartStylizer
{

  /**
   * @since 2.0
   */
  @Override
  public void setDefault(EditPart editPart)
  {
    setEdge(editPart, DawnColorConstants.COLOR_NO_CONFLICT);
  }

  /**
   * @since 2.0
   */
  @Override
  public void setConflicted(EditPart editPart, int type)
  {
    // Color color = DawnColorConstants.COLOR_DELETE_CONFLICT;
    Color color = getForegroundColor(editPart, DawnState.CONFLICT);
    setEdge(editPart, color);
  }

  /**
   * @since 2.0
   */
  private void setEdge(EditPart editPart, Color color)
  {
    ChangePropertyValueRequest request = new ChangePropertyValueRequest(StringStatics.BLANK,
        PackageUtil.getID(NotationPackage.eINSTANCE.getLineStyle_LineColor()), FigureUtilities.colorToInteger(color));
    final Command command = editPart.getCommand(request);

    AbstractEMFOperation operation = new AbstractEMFOperation(((IGraphicalEditPart)editPart).getEditingDomain(), StringStatics.BLANK, null)
    {
      @Override
      protected IStatus doExecute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
      {
        command.execute();
        return Status.OK_STATUS;
      }
    };

    try
    {
      operation.execute(new NullProgressMonitor(), null);
    }
    catch (ExecutionException e)
    {
    }

    editPart.refresh();
    editPart.getRoot().refresh();
  }

  /**
   * @since 2.0
   */
  @Override
  public void setLocked(EditPart editPart, int type)
  {
    Color color = null;
    switch (type)
    {
    case DawnAppearancer.TYPE_LOCKED_LOCALLY:
    {
      color = getBackgroundColor(editPart, DawnState.LOCKED_LOCALLY);
      break;
    }
    case DawnAppearancer.TYPE_LOCKED_GLOBALLY:
    {
      color = getBackgroundColor(editPart, DawnState.LOCKED_REMOTELY);
      break;
    }

    default:
      break;
    }
    if (color != null)
    {
      setEdge(editPart, color);
    }
  }
}
