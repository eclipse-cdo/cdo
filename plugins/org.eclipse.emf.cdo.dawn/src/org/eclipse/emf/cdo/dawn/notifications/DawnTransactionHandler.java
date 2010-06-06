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
package org.eclipse.emf.cdo.dawn.notifications;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.util.DawnDiagramUpdater;
import org.eclipse.emf.cdo.internal.dawn.bundle.OM;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @author Martin Fluegge
 */
public class DawnTransactionHandler implements CDOTransactionHandler
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DawnElementChangeListener.class);

  private final IDawnEditor editor;

  public DawnTransactionHandler(IDawnEditor editor)
  {
    this.editor = editor;
  }

  public void attachingObject(CDOTransaction transaction, CDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("attachingObject {0}", object); //$NON-NLS-1$
    }
    refresh(object);
    editor.setDirty();

  }

  public void detachingObject(CDOTransaction transaction, CDOObject object)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("detachingObject {0}", object); //$NON-NLS-1$
    }

    editor.setDirty();
  }

  public void modifyingObject(CDOTransaction transaction, final CDOObject object, CDOFeatureDelta featureDelta)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("modifyingObject {0}", object); //$NON-NLS-1$
    }

    refresh(object);
    editor.setDirty();
    object.eAdapters().add(new Adapter()
    {
      public void setTarget(Notifier newTarget)
      {
      }

      public void notifyChanged(Notification notification)
      {
        DawnDiagramUpdater.refreshEditPart(((DiagramDocumentEditor)editor).getDiagramEditPart(),
            (DiagramDocumentEditor)editor);
        object.eAdapters().remove(this);
      }

      public boolean isAdapterForType(Object type)
      {
        return false;
      }

      public Notifier getTarget()
      {
        return null;
      }
    });
  }

  private void refresh(CDOObject object)
  {
    View view = DawnDiagramUpdater.findViewByContainer(object);
    if (view == null)
    {
      view = DawnDiagramUpdater.findViewForModel(object, (DiagramDocumentEditor)editor);
    }
    if (view == null)
    {
      DawnDiagramUpdater.findViewFromCrossReferences(object);
    }

    EditPart relatedEditPart = DawnDiagramUpdater.findEditPart(view, ((DiagramDocumentEditor)editor)
        .getDiagramEditPart().getViewer());

    if (relatedEditPart != null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Updating EditPart {0} ", relatedEditPart); //$NON-NLS-1$
      }
      EditPart parent = relatedEditPart.getParent();
      if (parent instanceof IGraphicalEditPart)
      {
        DawnDiagramUpdater.refresh((IGraphicalEditPart)parent);
      }
      else
      {
        DawnDiagramUpdater.refreshEditPart(parent, (DiagramDocumentEditor)editor);
      }
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Updating DiagramEditPart {0} ", ((DiagramDocumentEditor)editor).getDiagramEditPart()); //$NON-NLS-1$
      }

      DawnDiagramUpdater.refresh(((DiagramDocumentEditor)editor).getDiagramEditPart());
    }
  }

  public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("committingTransaction"); //$NON-NLS-1$
    }
  }

  public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("committedTransaction"); //$NON-NLS-1$
    }
  }

  public void rolledBackTransaction(CDOTransaction transaction)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("rolledBackTransaction"); //$NON-NLS-1$
    }
  }
}
