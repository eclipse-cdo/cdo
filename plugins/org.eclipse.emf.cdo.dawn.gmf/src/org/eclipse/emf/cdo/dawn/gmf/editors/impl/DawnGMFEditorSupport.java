/*
 * Copyright (c) 2011-2013, 2015, 2019, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 *    Christian W. Damus (CEA) - bug 399285 support IDawnEditor adapters
 */
package org.eclipse.emf.cdo.dawn.gmf.editors.impl;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.dawn.appearance.DawnElementStylizer;
import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.editors.impl.DawnAbstractEditorSupport;
import org.eclipse.emf.cdo.dawn.gmf.appearance.DawnAppearancer;
import org.eclipse.emf.cdo.dawn.gmf.notifications.impl.DawnGMFHandler;
import org.eclipse.emf.cdo.dawn.gmf.notifications.impl.DawnGMFLockingHandler;
import org.eclipse.emf.cdo.dawn.gmf.synchronize.DawnChangeHelper;
import org.eclipse.emf.cdo.dawn.gmf.util.DawnDiagramUpdater;
import org.eclipse.emf.cdo.dawn.notifications.BasicDawnListener;
import org.eclipse.emf.cdo.dawn.spi.DawnState;
import org.eclipse.emf.cdo.dawn.ui.stylizer.DawnElementStylizerRegistry;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandlerBase;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.gmf.runtime.notation.View;

import java.util.Map;

/**
 * @author Martin Fluegge
 */
public class DawnGMFEditorSupport extends DawnAbstractEditorSupport
{
  private DawnGMFHandler dawnGMFHandler;

  public DawnGMFEditorSupport(IDawnEditor editor)
  {
    super(editor);
    dawnGMFHandler = createDawnGMFHandler(editor);
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
    return dawnGMFHandler;
  }

  /**
   * Creates a GMF-specific handler for the specified {@code editor}.
   * <p>
   * <strong>Note</strong> that this method is called in the constructor, so subclasses must
   * be aware that {@code this} has not been fully initialized.
   *
   * @since 2.1
   */
  protected DawnGMFHandler createDawnGMFHandler(IDawnEditor editor)
  {
    return new DawnGMFHandler(editor);
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
    final DiagramDocumentEditor diagramDocumentEditor = getDiagramEditor(getEditor());
    TransactionalEditingDomain editingDomain = diagramDocumentEditor.getEditingDomain();
    CommandStack commandStack = editingDomain.getCommandStack();
    commandStack.execute(new RecordingCommand(editingDomain)
    {
      @Override
      public void doExecute()
      {
        DawnAppearancer.setEditPartDefaultAllChildren(diagramDocumentEditor.getDiagramEditPart());
        DawnDiagramUpdater.refreshEditPart(diagramDocumentEditor.getDiagramEditPart());
      }
    });
  }

  /**
   * Obtains the GMF diagram editor from the given Dawn {@code editor}.  If the
   * {@code editor} is a {@link DiagramDocumentEditor}, then that is returned as
   * is.  Otherwise, try to get an {@code DiagramDocumentEditor}
   * {@linkplain IAdaptable#getAdapter(Class) adapter} from the {@code editor}.
   *
   * @param editor the Dawn editor from which to get the GMF editor
   * @since 2.1
   */
  public static DiagramDocumentEditor getDiagramEditor(IDawnEditor editor)
  {
    if (editor instanceof DiagramDocumentEditor)
    {
      return (DiagramDocumentEditor)editor;
    }

    return editor.getAdapter(DiagramDocumentEditor.class);
  }

  @Override
  public void refresh()
  {
    final DiagramDocumentEditor diagramDocumentEditor = getDiagramEditor(getEditor());
    TransactionalEditingDomain editingDomain = diagramDocumentEditor.getEditingDomain();
    CommandStack commandStack = editingDomain.getCommandStack();
    commandStack.execute(new RecordingCommand(editingDomain)
    {
      @Override
      public void doExecute()
      {
        DawnDiagramUpdater.refreshEditPart(diagramDocumentEditor.getDiagramEditPart());
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
        if (model instanceof View)
        {
          EObject element = ((View)model).getElement();
          CDOUtil.getCDOObject(element).cdoWriteLock().lock();
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
        if (model instanceof View)
        {
          EObject element = ((View)model).getElement();
          CDOUtil.getCDOObject(element).cdoWriteLock().unlock();
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
  public void handleRemoteLockChanges(Map<Object, DawnState> changedObjects)
  {
    if (!changedObjects.isEmpty())
    {
      CDOView view = getView();

      for (Object object : changedObjects.keySet())
      {
        if (object instanceof CDOObject)
        {
          handleLock((CDOObject)object, view);
        }
      }

      refresh();
    }
  }

  private void handleLock(CDOObject object, CDOView cdoView)
  {
    EObject element = CDOUtil.getEObject(object); // either semantic object or notational
    View view = DawnDiagramUpdater.findView(element);
    if (view != null)
    {
      // if there is no view, the semantic object is not displayed.
      EditPart editPart = DawnDiagramUpdater.createOrFindEditPartIfViewExists(view, getDiagramEditor(getEditor()));
      if (editPart != null)
      {
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
}
