/*
 * Copyright (c) 2007-2012, 2014, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.ui.ItemsProcessor;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionFinishedEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionStartedEvent;
import org.eclipse.emf.cdo.view.CDOObjectHandler;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;
import org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent;

import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.om.pref.OMPreferencesChangeEvent;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

import java.util.List;
import java.util.Set;

/**
 * A class that handles {@link org.eclipse.net4j.util.event.IEvent events} on
 * {@link org.eclipse.jface.viewers.TreeViewer TreeViewer}-based editors or views.
 *
 * @author Eike Stepper
 * @see org.eclipse.net4j.util.event.IEvent
 * @see org.eclipse.jface.viewers.TreeViewer
 */
public class CDOEventHandler
{
  private CDOView view;

  private TreeViewer treeViewer;

  private IListener sessionListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof IContainerEvent<?>)
      {
        IContainerEvent<?> e = (IContainerEvent<?>)event;
        if (e.getDeltaElement() == view && e.getDeltaKind() == IContainerDelta.Kind.REMOVED)
        {
          viewClosed();
        }
      }
      else if (event instanceof ILifecycleEvent)
      {
        ILifecycleEvent e = (ILifecycleEvent)event;
        if (e.getKind() == ILifecycleEvent.Kind.DEACTIVATED)
        {
          viewClosed();
        }
      }
    }
  };

  private IListener viewListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOViewTargetChangedEvent)
      {
        CDOViewTargetChangedEvent e = (CDOViewTargetChangedEvent)event;
        viewTargetChanged(e.getBranchPoint());
      }
      else if (event instanceof CDOViewInvalidationEvent)
      {
        CDOViewInvalidationEvent e = (CDOViewInvalidationEvent)event;
        // Remove detached object from selection, could incur into unwanted exceptions
        checkDetachedSelection(e.getDetachedObjects());
        viewInvalidated(e.getDirtyObjects());
      }
      else if (event instanceof CDOViewLocksChangedEvent)
      {
        CDOViewLocksChangedEvent e = (CDOViewLocksChangedEvent)event;
        EObject[] objects = e.getAffectedObjects();
        if (objects.length != 0)
        {
          updateElement(objects);
        }
      }
      else if (event instanceof CDOTransactionFinishedEvent)
      {
        // CDOTransactionFinishedEvent e = (CDOTransactionFinishedEvent)event;
        // if (e.getType() == CDOTransactionFinishedEvent.Type.COMMITTED)
        // {
        // Map<CDOID, CDOID> idMappings = e.getIDMappings();
        // HashSet<CDOID> newOIDs = new HashSet<CDOID>(idMappings.values());
        // new ItemsProcessor(view)
        // {
        // @Override
        // protected void processCDOObject(TreeViewer viewer, InternalCDOObject cdoObject)
        // {
        // viewer.update(cdoObject.cdoInternalInstance(), null);
        // }
        // }.processCDOObjects(treeViewer, newOIDs);
        // }
        // else
        {
          refreshTreeViewer();
        }

        viewDirtyStateChanged();
      }
      else if (event instanceof CDOTransactionStartedEvent)
      {
        viewDirtyStateChanged();
      }
      else if (event instanceof CDOTransactionConflictEvent)
      {
        CDOTransactionConflictEvent e = (CDOTransactionConflictEvent)event;
        viewConflict(e.getConflictingObject(), e.isFirstConflict());
      }
    }
  };

  private CDOObjectHandler objectHandler = new CDOObjectHandler()
  {
    @Override
    public void objectStateChanged(CDOView view, CDOObject object, CDOState oldState, CDOState newState)
    {
      CDOEventHandler.this.objectStateChanged(object);
    }
  };

  private IListener preferenceListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      @SuppressWarnings("unchecked")
      OMPreferencesChangeEvent<Boolean> preferenceChangeEvent = (OMPreferencesChangeEvent<Boolean>)event;
      if (OM.PREF_EDITOR_AUTO_RELOAD.getName().equals(preferenceChangeEvent.getPreference().getName()))
      {
        if (preferenceChangeEvent.getNewValue().booleanValue())
        {
          refreshTreeViewer();
        }
      }
      else if (OM.PREF_LABEL_DECORATION.getName().equals(preferenceChangeEvent.getPreference().getName()))
      {
        // Fire a LabelProviderChangedEvent in case user changed decoration pattern
        try
        {
          treeViewer.getControl().getDisplay().asyncExec(new Runnable()
          {
            @Override
            public void run()
            {
              try
              {
                PlatformUI.getWorkbench().getDecoratorManager().update(CDOLabelDecorator.DECORATOR_ID);
              }
              catch (Exception ignore)
              {
              }
            }
          });
        }
        catch (Exception ignore)
        {
        }
      }
    }
  };

  private void checkDetachedSelection(final Set<?> detachedObjects)
  {
    UIUtil.getDisplay().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
          List<?> selectedElements = selection.toList();
          for (Object object : selectedElements)
          {
            if (detachedObjects.contains(object))
            {
              treeViewer.setSelection(StructuredSelection.EMPTY);
            }
          }
        }
        catch (Exception ignore)
        {
        }
      }
    });
  }

  /**
   * @since 2.0
   */
  public CDOEventHandler(CDOView view, TreeViewer treeViewer)
  {
    this.view = view;
    this.treeViewer = treeViewer;
    wirePreferences();
    view.getSession().addListener(sessionListener);
    view.addListener(viewListener);
    view.addObjectHandler(objectHandler);
  }

  /**
   * @since 2.0
   */
  public void dispose()
  {
    if (view != null)
    {
      view.removeObjectHandler(objectHandler);
      view.removeListener(viewListener);
      CDOSession session = view.getSession();
      if (session != null)
      {
        session.removeListener(sessionListener);
      }
    }

    unwirePreferences();
    view = null;
    treeViewer = null;
  }

  /**
   * @since 2.0
   */
  public CDOView getView()
  {
    return view;
  }

  public TreeViewer getTreeViewer()
  {
    return treeViewer;
  }

  /**
   * @since 2.0
   */
  public void setTreeViewer(TreeViewer viewer)
  {
    treeViewer = viewer;
  }

  /**
   * @since 2.0
   */
  public void refreshTreeViewer()
  {
    try
    {
      Control control = treeViewer.getControl();
      if (!control.isDisposed())
      {
        control.getDisplay().asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            try
            {
              treeViewer.refresh(true);
            }
            catch (Exception ignore)
            {
            }
          }
        });
      }
    }
    catch (Exception ignore)
    {
    }
  }

  /**
   * @since 4.4
   */
  public void updateElement(final Object element)
  {
    UIUtil.updateElements(treeViewer, element);
  }

  /**
   * @since 2.0
   */
  public boolean isAutoReloadEnabled()
  {
    return OM.PREF_EDITOR_AUTO_RELOAD.getValue();
  }

  /**
   * @since 2.0
   */
  protected void wirePreferences()
  {
    OM.PREFS.addListener(preferenceListener);
  }

  /**
   * @since 2.0
   */
  protected void unwirePreferences()
  {
    OM.PREFS.removeListener(preferenceListener);
  }

  /**
   * @since 4.3
   */
  protected void objectStateChanged(CDOObject cdoObject)
  {
    updateElement(cdoObject);
  }

  /**
   * @since 2.0
   */
  protected void objectInvalidated(InternalCDOObject cdoObject)
  {
  }

  /**
   * @since 2.0
   */
  protected void viewInvalidated(Set<? extends CDOObject> dirtyObjects)
  {
    new ItemsProcessor(view)
    {
      @Override
      protected void processCDOObject(TreeViewer viewer, InternalCDOObject cdoObject)
      {
        objectInvalidated(cdoObject);
        if (isAutoReloadEnabled())
        {
          viewer.refresh(cdoObject.cdoInternalInstance(), true);
        }
      }
    }.processCDOObjects(treeViewer, dirtyObjects);
  }

  protected void viewDirtyStateChanged()
  {
  }

  protected void viewConflict(CDOObject conflictingObject, boolean firstConflict)
  {
  }

  /**
   * @since 4.12
   */
  protected void viewTargetChanged(CDOBranchPoint branchPoint)
  {
  }

  protected void viewClosed()
  {
  }
}
