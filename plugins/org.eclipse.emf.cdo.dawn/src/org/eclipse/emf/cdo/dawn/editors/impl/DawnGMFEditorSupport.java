/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.editors.impl;

import org.eclipse.emf.cdo.dawn.appearance.DawnAppearancer;
import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.notifications.DawnNotificationUtil;
import org.eclipse.emf.cdo.dawn.util.DawnDiagramUpdater;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;

/**
 * @author Martin Fluegge
 */
public class DawnGMFEditorSupport extends DawnAbstractEditorSupport
{
  public DawnGMFEditorSupport(IDawnEditor editor)
  {
    super(editor);
  }

  public void close()
  {
    CDOView view = getView();
    if (view != null && !view.isClosed())
    {
      view.close();
    }
  }

  public void registerListeners()
  {
    // DawnNotificationUtil.registerResourceListeners(getEditingDomain().getResourceSet(), this);
    DawnNotificationUtil.registerTransactionListeners((CDOTransaction)getView(), getEditor());
    DawnNotificationUtil.setChangeSubscriptionPolicy((CDOTransaction)getView());
  }

  /**
   * @since 1.0
   */
  @Override
  public void rollback()
  {
    super.rollback();
    final DiagramDocumentEditor diagramDocumentEditor = (DiagramDocumentEditor)getEditor();
    TransactionalEditingDomain editingDomain = diagramDocumentEditor.getEditingDomain();
    editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain)
    {
      @Override
      public void doExecute()
      {
        DawnAppearancer.setEditPartDefaultAllChildren(diagramDocumentEditor.getDiagramEditPart());
        DawnDiagramUpdater.refreshEditPart(diagramDocumentEditor.getDiagramEditPart());
      }
    });
  }
}
