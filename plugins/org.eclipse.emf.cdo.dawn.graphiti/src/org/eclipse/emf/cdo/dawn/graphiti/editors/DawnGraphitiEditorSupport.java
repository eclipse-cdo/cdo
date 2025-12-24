/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.graphiti.editors;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.dawn.appearance.DawnElementStylizer;
import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.editors.impl.DawnAbstractEditorSupport;
import org.eclipse.emf.cdo.dawn.gmf.appearance.DawnAppearancer;
import org.eclipse.emf.cdo.dawn.gmf.synchronize.DawnChangeHelper;
import org.eclipse.emf.cdo.dawn.gmf.util.DawnDiagramUpdater;
import org.eclipse.emf.cdo.dawn.graphiti.notifications.DawnGraphitiHandler;
import org.eclipse.emf.cdo.dawn.graphiti.notifications.DawnGraphitiLockingHandler;
import org.eclipse.emf.cdo.dawn.graphiti.util.DawnGraphitiUtil;
import org.eclipse.emf.cdo.dawn.helper.DawnEditorHelper;
import org.eclipse.emf.cdo.dawn.notifications.BasicDawnListener;
import org.eclipse.emf.cdo.dawn.spi.DawnState;
import org.eclipse.emf.cdo.dawn.ui.stylizer.DawnElementStylizerRegistry;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandlerBase;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.RootEditPart;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;

import java.util.List;
import java.util.Map;

/**
 * @author Martin Fluegge
 */
/*
 * TODO remove this suppress warning as soon as I have found a way to workaround the problem that the Graphiti editor
 * which is extended is internal
 */
public class DawnGraphitiEditorSupport extends DawnAbstractEditorSupport
{
  private DawnGraphitiHandler dawnGraphitiHandler;

  public DawnGraphitiEditorSupport(IDawnEditor editor)
  {
    super(editor);
    dawnGraphitiHandler = new DawnGraphitiHandler(getEditor());
  }

  @Override
  public void close()
  {
    CDOView view = getView();
    if (view != null && !view.isClosed())
    {
      view.close();
    }
  }

  @Override
  protected BasicDawnListener getBasicHandler()
  {
    return dawnGraphitiHandler;
  }

  @Override
  protected BasicDawnListener getLockingHandler()
  {
    return new DawnGraphitiLockingHandler(getEditor());
  }

  @Override
  protected CDOTransactionHandlerBase getTransactionHandler()
  {
    return dawnGraphitiHandler;
  }

  @Override
  // TODO: try to move this method to a common base class for Graphiti and GMF
  public void rollback()
  {
    super.rollback();
    final DiagramEditor diagramDocumentEditor = (DiagramEditor)getEditor();
    TransactionalEditingDomain editingDomain = diagramDocumentEditor.getEditingDomain();
    editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain)
    {
      @Override
      public void doExecute()
      {
        RootEditPart rootEditPart = diagramDocumentEditor.getGraphicalViewer().getRootEditPart();
        DawnAppearancer.setEditPartDefaultAllChildren(rootEditPart);
        DawnDiagramUpdater.refreshEditPart(rootEditPart);
      }
    });
  }

  @Override
  public void refresh()
  {
    DawnEditorHelper.getDisplay().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        RootEditPart rootEditPart = ((DiagramEditor)getEditor()).getGraphicalViewer().getRootEditPart();
        DawnDiagramUpdater.refreshEditPart(rootEditPart);
      }
    });
  }

  @Override
  public void lockObject(Object objectToBeLocked)
  {
    if (objectToBeLocked instanceof EditPart)
    {
      EditPart editPart = (EditPart)objectToBeLocked;
      Object model = editPart.getModel();

      if (model instanceof EObject)
      {
        CDOUtil.getCDOObject((EObject)model).cdoWriteLock().lock();
        if (model instanceof PictogramElement)
        {
          EList<EObject> businessObjects = ((PictogramElement)model).getLink().getBusinessObjects();
          for (EObject element : businessObjects)
          {
            CDOUtil.getCDOObject(element).cdoWriteLock().lock();
          }
        }
      }
      DawnElementStylizer stylizer = DawnElementStylizerRegistry.instance.getStylizer(editPart);
      if (stylizer != null)
      {
        stylizer.setLocked(editPart, DawnAppearancer.TYPE_LOCKED_LOCALLY);
      }
    }
    refresh();
  }

  @Override
  public void unlockObject(Object objectToBeUnlocked)
  {
    if (objectToBeUnlocked instanceof EditPart)
    {
      EditPart editPart = (EditPart)objectToBeUnlocked;

      Object model = editPart.getModel();

      if (model instanceof EObject)
      {
        CDOUtil.getCDOObject((EObject)model).cdoWriteLock().unlock();
        if (model instanceof PictogramElement)
        {
          if (model instanceof PictogramElement)
          {
            EList<EObject> businessObjects = ((PictogramElement)model).getLink().getBusinessObjects();
            for (EObject element : businessObjects)
            {
              CDOUtil.getCDOObject(element).cdoWriteLock().unlock();
            }
          }
        }
      }
      DawnElementStylizer stylizer = DawnElementStylizerRegistry.instance.getStylizer(editPart);
      if (stylizer != null)
      {
        stylizer.setDefault(editPart);
      }
    }
    refresh();
  }

  @Override
  public void handleRemoteLockChanges(final Map<Object, DawnState> changedObjects)
  {
    DawnEditorHelper.getDisplay().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        for (Object o : changedObjects.keySet())
        {
          handleLock((CDOObject)o, getView());
        }
      }
    });

    refresh();
  }

  private void handleLock(CDOObject object, CDOView cdoView)
  {
    EObject element = CDOUtil.getEObject(object); // either semantic object or notational

    DiagramEditor editor = (DiagramEditor)getEditor();

    List<PictogramElement> pictogramElements = DawnGraphitiUtil.getPictgramElements(editor.getDiagramTypeProvider().getDiagram(), element);

    for (PictogramElement pictogramElement : pictogramElements)
    {
      EditPart editPart = DawnGraphitiUtil.getEditpart(pictogramElement, editor.getGraphicalViewer().getRootEditPart());

      if (editPart == null)
      {
        continue;
      }

      if (object.cdoWriteLock().isLocked())
      {
        DawnAppearancer.setEditPartLocked(editPart, DawnAppearancer.TYPE_LOCKED_LOCALLY);
      }
      else if (object.cdoWriteLock().isLockedByOthers())
      {
        DawnAppearancer.setEditPartLocked(editPart, DawnAppearancer.TYPE_LOCKED_GLOBALLY);
        DawnChangeHelper.deactivateEditPart(editPart);
      }
      else
      {
        DawnAppearancer.setEditPartDefault(editPart);
        DawnChangeHelper.activateEditPart(editPart);
      }
    }
  }
}
