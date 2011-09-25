/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.gmf.editors.impl;

import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.editors.impl.DawnAbstractEditorSupport;
import org.eclipse.emf.cdo.dawn.gmf.appearance.DawnAppearancer;
import org.eclipse.emf.cdo.dawn.gmf.appearance.DawnEditPartStylizer;
import org.eclipse.emf.cdo.dawn.gmf.appearance.DawnEditPartStylizerRegistry;
import org.eclipse.emf.cdo.dawn.gmf.notifications.impl.DawnGMFHandler;
import org.eclipse.emf.cdo.dawn.gmf.notifications.impl.DawnGMFLockingHandler;
import org.eclipse.emf.cdo.dawn.gmf.util.DawnDiagramUpdater;
import org.eclipse.emf.cdo.dawn.notifications.BasicDawnListener;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandlerBase;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @author Martin Fluegge
 */
public class DawnGMFEditorSupport extends DawnAbstractEditorSupport
{
  private DawnGMFHandler dawnGMFHandler;

  public DawnGMFEditorSupport(IDawnEditor editor)
  {
    super(editor);
    dawnGMFHandler = new DawnGMFHandler(editor);
  }

  public void close()
  {
    CDOView view = getView();
    if (view != null && !view.isClosed())
    {
      view.close();
    }
  }

  // @Override
  // public void registerListeners()
  // {
  // BasicDawnListener listener = new DawnGMFHandler(getEditor());
  // CDOView view = getView();
  // view.addListener(listener);
  //
  // if (view instanceof CDOTransaction)
  // {
  // CDOTransaction transaction = (CDOTransaction)view;
  // transaction.addTransactionHandler(listener);
  // transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.CDO);
  // }
  // }

  @Override
  protected BasicDawnListener getBasicHandler()
  {
    return dawnGMFHandler;
  }

  @Override
  protected BasicDawnListener getLockingHandler()
  {
    return new DawnGMFLockingHandler(getEditor());
  }

  @Override
  protected CDOTransactionHandlerBase getTransactionHandler()
  {
    return dawnGMFHandler;
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

  public void refresh()
  {
    final DiagramDocumentEditor diagramDocumentEditor = (DiagramDocumentEditor)getEditor();
    TransactionalEditingDomain editingDomain = diagramDocumentEditor.getEditingDomain();
    editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain)
    {
      @Override
      public void doExecute()
      {
        DawnDiagramUpdater.refreshEditPart(diagramDocumentEditor.getDiagramEditPart());
      }
    });
  }

  public void lockObject(Object objectToBeLocked)
  {
    if (objectToBeLocked instanceof EditPart)
    {
      EditPart editPart = (EditPart)objectToBeLocked;
      Object model = editPart.getModel();

      if (model instanceof EObject)
      {
        CDOUtil.getCDOObject((EObject)model).cdoWriteLock().lock();
        if (model instanceof View)
        {
          EObject element = ((View)model).getElement();
          CDOUtil.getCDOObject(element).cdoWriteLock().lock();
        }
      }
      DawnEditPartStylizer stylizer = DawnEditPartStylizerRegistry.instance.getStylizer(editPart);
      if (stylizer != null)
      {
        stylizer.setLocked(editPart, DawnAppearancer.TYPE_LOCKED_LOCALLY);
      }
    }
    refresh();
  }

  public void unlockObject(Object objectToBeUnlocked)
  {
    if (objectToBeUnlocked instanceof EditPart)
    {
      EditPart editPart = (EditPart)objectToBeUnlocked;
      Object model = editPart.getModel();

      if (model instanceof EObject)
      {
        CDOUtil.getCDOObject((EObject)model).cdoWriteLock().unlock();
        if (model instanceof View)
        {
          EObject element = ((View)model).getElement();
          CDOUtil.getCDOObject(element).cdoWriteLock().unlock();
        }
      }
      DawnEditPartStylizer stylizer = DawnEditPartStylizerRegistry.instance.getStylizer(editPart);
      if (stylizer != null)
      {
        stylizer.setDefault(editPart);
      }
    }
    refresh();
  }
}
