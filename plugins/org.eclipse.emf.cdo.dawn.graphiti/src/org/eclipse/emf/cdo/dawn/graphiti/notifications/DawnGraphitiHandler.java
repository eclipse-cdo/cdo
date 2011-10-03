/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.graphiti.notifications;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.gmf.synchronize.DawnConflictHelper;
import org.eclipse.emf.cdo.dawn.gmf.util.DawnDiagramUpdater;
import org.eclipse.emf.cdo.dawn.notifications.BasicDawnTransactionHandler;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.InvalidObjectException;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import org.eclipse.draw2d.graph.Edge;
import org.eclipse.gef.RootEditPart;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.widgets.Display;

/**
 * @author Martin Fluegge
 */
public class DawnGraphitiHandler extends BasicDawnTransactionHandler
{
  public DawnGraphitiHandler(IDawnEditor editor)
  {
    super(editor);
  }

  /**
   * @since 1.0
   */
  @Override
  public void handleViewInvalidationEvent(CDOViewInvalidationEvent event)
  {

    editor.getDawnEditorSupport().refresh();
    // CDOViewInvalidationEvent e = event;
    //
    // adjustDeletedEdges(e);
    //
    // for (CDOObject dirtyObject : e.getDirtyObjects())
    // {
    // handleObject(dirtyObject);
    // }
    //
    // for (CDOObject detachedObject : e.getDetachedObjects())
    // {
    // handleObject(detachedObject);
    // }
  }

  /**
   * @since 1.0
   */
  @Override
  public void handleTransactionConflictEvent(CDOTransactionConflictEvent event)
  {
    CDOTransactionConflictEvent cdoTransactionConflictEvent = event;

    CDOObject cdoObject = cdoTransactionConflictEvent.getConflictingObject();

    @SuppressWarnings("unused")
    EObject element = CDOUtil.getEObject(cdoObject); // either semantic object or notational
    // View view = DawnDiagramUpdater.findView(element);
    //
    // if (DawnConflictHelper.isConflicted(cdoObject))
    // {
    // DawnConflictHelper.handleConflictedView(cdoObject, view, (DiagramDocumentEditor)editor);
    // return;
    // }
  }

  @Override
  public void modifyingObject(CDOTransaction transaction, final CDOObject object, CDOFeatureDelta featureDelta)
  {
    // refresh(object);
    // editor.setDirty();
    // object.eAdapters().add(new Adapter()
    // {
    // public void setTarget(Notifier newTarget)
    // {
    // }
    //
    // public void notifyChanged(Notification notification)
    // {
    // DawnDiagramUpdater.refreshEditPart(((DiagramEditor)editor).getGraphicalViewer().getRootEditPart());
    //
    // object.eAdapters().remove(this);
    // }
    //
    // public boolean isAdapterForType(Object type)
    // {
    // return false;
    // }
    //
    // public Notifier getTarget()
    // {
    // return null;
    // }
    // });
  }

  /**
   * @since 1.0
   */
  @Override
  public void attachingObject(CDOTransaction transaction, CDOObject object)
  {
    // super.attachingObject(transaction, object);
    // refresh(object);
  }

  /**
   * Edges must be adjusted because of the transience of the Node source/targetEdges CDO cannot see this because
   * removing an edges just removes the edge from the diagram. CDO just notices the change in the diagram but not in the
   * (detached) edge. The other site (node) is transient and will not be part of the notification. So I must adjust this
   * later. CDOLEgacy Wrapper breakes because it only adjusts the changes in the diagram and not the removed edge. So I
   * cannot adjust this in the Wrapper. Maybe there is another more generic way.
   */
  public void adjustDeletedEdges(final CDOViewInvalidationEvent e)
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      public void run()
      {
        for (CDOObject obj : e.getDetachedObjects())
        {
          final EObject view = CDOUtil.getEObject(obj);
          if (view instanceof Edge)
          {
            EditingDomain editingDomain = ((IEditingDomainProvider)view.eResource().getResourceSet())
                .getEditingDomain();
            editingDomain.getCommandStack().execute(new RecordingCommand((TransactionalEditingDomain)editingDomain)
            {
              @Override
              protected void doExecute()
              {
                try
                {
                  ((Edge)view).setTarget(null);
                }
                catch (InvalidObjectException ignore)
                {
                }

                try
                {
                  ((Edge)view).setSource(null);
                }
                catch (InvalidObjectException ignore)
                {
                }
              }
            });
          }
        }
      }
    });
  }

  protected void handleConflicts(CDOViewInvalidationEvent e)
  {
    // for (CDOObject obj : e.getDetachedObjects())
    // {
    // EObject element = CDOUtil.getEObject(obj);
    // View view = DawnDiagramUpdater.findViewByContainer(element);
    // DawnConflictHelper.handleConflictedView(CDOUtil.getCDOObject(element), view, (DiagramDocumentEditor)editor);
    // }
  }

  @SuppressWarnings("unused")
  private void handleObject(CDOObject dirtyObject)
  {
    if (dirtyObject.cdoInvalid())
    {
      return;
    }
    EObject element = CDOUtil.getEObject(dirtyObject); // either sementic object or notational

    // EditingDomain editingDomain = ((DiagramEditor)editor).getEditingDomain();
    // editingDomain.getCommandStack().execute(new RecordingCommand((TransactionalEditingDomain)editingDomain)
    // {
    // @Override
    // protected void doExecute()
    // {
    // RootEditPart rootEditPart = ((DiagramEditor)editor).getGraphicalViewer().getRootEditPart();
    // DawnDiagramUpdater.refreshEditPart(rootEditPart);
    // }
    // });

    Display.getDefault().asyncExec(new Runnable()
    {

      public void run()
      {

        @SuppressWarnings("restriction")
        RootEditPart rootEditPart = ((DiagramEditor)editor).getGraphicalViewer().getRootEditPart();
        DawnDiagramUpdater.refreshEditPart(rootEditPart);
      }
    });

    // View view = DawnDiagramUpdater.findView(element);

    if (DawnConflictHelper.isConflicted(dirtyObject))
    {
      // DawnConflictHelper.handleConflictedView(dirtyObject, view, editor);
      return;
    }

    // EditPart relatedEditPart = DawnDiagramUpdater.findEditPart(view, ((DiagramEditor)editor).getGraphicalViewer()
    // .getRootEditPart().getViewer());
    // if (relatedEditPart != null)
    // {
    // DawnDiagramUpdater.refreshEditPart(relatedEditPart.getParent());
    // }
    // else
    // {
    // if (TRACER.isEnabled())
    // {
    //        TRACER.format("Updating DiagramEditPart {0} ", ((DiagramEditor)editor).getDiagramEditPart()); //$NON-NLS-1$
    // }
    // DawnDiagramUpdater.refreshEditPart(((DiagramEditor)editor).getDiagramEditPart(), (DiagramDocumentEditor)editor);
    // }
  }

  @SuppressWarnings("restriction")
  protected void refresh(CDOObject object)
  {
    DawnDiagramUpdater.refreshEditPart(((DiagramEditor)editor).getGraphicalViewer().getRootEditPart());

    // View view = DawnDiagramUpdater.findViewByContainer(object);
    // if (view == null)
    // {
    // view = DawnDiagramUpdater.findViewForModel(object, (DiagramDocumentEditor)editor);
    // }
    // if (view == null)
    // {
    // DawnDiagramUpdater.findViewFromCrossReferences(object);
    // }
    //
    // EditPart relatedEditPart = DawnDiagramUpdater.findEditPart(view, ((DiagramDocumentEditor)editor)
    // .getDiagramEditPart().getViewer());
    //
    // if (relatedEditPart != null)
    // {
    // if (TRACER.isEnabled())
    // {
    //        TRACER.format("Updating EditPart {0} ", relatedEditPart); //$NON-NLS-1$
    // }
    // EditPart parent = relatedEditPart.getParent();
    // if (parent instanceof IGraphicalEditPart)
    // {
    // DawnDiagramUpdater.refresh((IGraphicalEditPart)parent);
    // }
    // else
    // {
    // DawnDiagramUpdater.refreshEditPart(parent, (DiagramDocumentEditor)editor);
    // }
    // }
    // else
    // {
    // DawnDiagramUpdater.refresh(((DiagramDocumentEditor)editor).getDiagramEditPart());
    // }
  }
}
