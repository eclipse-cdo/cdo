/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.editor;

import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.ui.action.RedoAction;
import org.eclipse.emf.edit.ui.action.UndoAction;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * The editor action-bar contributor for the Security Manager editor.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class CDOSecurityFormActionBarContributor extends EditorActionBarContributor implements IPropertyListener
{
  private IEditorPart activeEditor;

  private UndoAction undoAction;

  private RedoAction redoAction;

  public CDOSecurityFormActionBarContributor()
  {
  }

  @Override
  public void init(IActionBars actionBars)
  {
    super.init(actionBars);

    ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();

    undoAction = new UndoAction();
    undoAction.setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO));
    actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undoAction);

    redoAction = new RedoAction();
    redoAction.setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
    actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redoAction);
  }

  @Override
  public void setActiveEditor(IEditorPart targetEditor)
  {
    if (activeEditor != null)
    {
      deactivate();
    }

    activeEditor = targetEditor;

    if (activeEditor instanceof IEditingDomainProvider)
    {
      activate();
    }
  }

  protected void activate()
  {
    activeEditor.addPropertyListener(this);

    if (undoAction != null)
    {
      undoAction.setActiveWorkbenchPart(activeEditor);
    }

    if (redoAction != null)
    {
      redoAction.setActiveWorkbenchPart(activeEditor);
    }

    update();
  }

  protected void deactivate()
  {
    activeEditor.removePropertyListener(this);

    if (undoAction != null)
    {
      undoAction.setActiveWorkbenchPart(null);
    }

    if (redoAction != null)
    {
      redoAction.setActiveWorkbenchPart(null);
    }

    update();
  }

  protected void update()
  {
    if (undoAction != null)
    {
      undoAction.update();
    }

    if (redoAction != null)
    {
      redoAction.update();
    }
  }

  @Override
  public void propertyChanged(Object source, int propId)
  {
    update();
  }
}
