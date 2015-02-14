/*
 * Copyright (c) 2009-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout.ObjectType;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.actions.OpenTransactionAction;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutDropAdapterAssistant extends CommonDropAdapterAssistant
{
  private static final EObject[] NO_OBJECTS = {};

  public CDOCheckoutDropAdapterAssistant()
  {
  }

  @Override
  public boolean isSupportedType(TransferData transferType)
  {
    return super.isSupportedType(transferType) || FileTransfer.getInstance().isSupportedType(transferType);
  }

  @Override
  public IStatus validateDrop(Object target, int dropOperation, TransferData transferType)
  {
    Operation operation = Operation.getFor(target, transferType);
    if (operation != null)
    {
      return Status.OK_STATUS;
    }

    return Status.CANCEL_STATUS;
  }

  @Override
  public IStatus handleDrop(CommonDropAdapter dropAdapter, DropTargetEvent dropTargetEvent, Object target)
  {
    if (target == null || dropTargetEvent.data == null)
    {
      return Status.CANCEL_STATUS;
    }

    TransferData transferType = dropAdapter.getCurrentTransfer();
    Operation operation = Operation.getFor(target, transferType);
    if (operation != null)
    {
      boolean copy = dropAdapter.getCurrentOperation() == DND.DROP_COPY;
      operation.drop(copy);
      return Status.OK_STATUS;
    }

    return Status.CANCEL_STATUS;
  }

  private static EObject[] getSelectedObjects()
  {
    ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
    if (selection instanceof IStructuredSelection)
    {
      return getSelectedObjects((IStructuredSelection)selection);
    }

    return NO_OBJECTS;
  }

  private static EObject[] getSelectedObjects(IStructuredSelection selection)
  {
    List<EObject> selectedObjects = new ArrayList<EObject>();
    ObjectType firstObjectType = null;

    for (Iterator<?> it = selection.iterator(); it.hasNext();)
    {
      Object object = it.next();
      if (object instanceof EObject)
      {
        EObject eObject = (EObject)object;
        ObjectType objectType = ObjectType.valueFor(eObject);
        if (objectType == null || objectType == ObjectType.Root)
        {
          return NO_OBJECTS;
        }

        if (firstObjectType == null)
        {
          firstObjectType = objectType;
        }
        else
        {
          boolean firstIsObject = firstObjectType == ObjectType.Object;
          boolean isObject = objectType == ObjectType.Object;
          if (firstIsObject != isObject)
          {
            return NO_OBJECTS;
          }
        }

        selectedObjects.add(eObject);
      }
    }

    return selectedObjects.toArray(new EObject[selectedObjects.size()]);
  }

  /**
   * @author Eike Stepper
   */
  private static abstract class Operation
  {
    private final EObject[] objects;

    private final Object target;

    public Operation(EObject[] objects, Object target)
    {
      this.objects = objects;
      this.target = target;
    }

    public final void drop(final boolean copy)
    {
      final String title = (copy ? "Copy " : "Move ")
          + (ObjectType.valueFor(objects[0]) == ObjectType.Object ? "objects" : "resource nodes");

      new Job(title)
      {
        @Override
        protected IStatus run(IProgressMonitor monitor)
        {
          monitor.beginTask(title, 110 + (copy ? objects.length + 1 : 0));

          CDOView view = CDOUtil.getCDOObject(objects[0]).cdoView();
          CDOTransaction transaction = view.getSession().openTransaction(view.getBranch());
          OpenTransactionAction.configureTransaction(transaction);
          monitor.worked(1);

          try
          {
            EcoreUtil.Copier copier = null;
            if (copy)
            {
              copier = new EcoreUtil.Copier();
            }

            List<EObject> transactionalObjects = new ArrayList<EObject>();
            for (int i = 0; i < objects.length; i++)
            {
              EObject object = objects[i];
              EObject transactionalObject = transaction.getObject(object);

              if (copier != null)
              {
                transactionalObject = copier.copy(transactionalObject);
                monitor.worked(1);
              }

              transactionalObjects.add(transactionalObject);
            }

            if (copier != null)
            {
              copier.copyReferences();
              monitor.worked(1);
            }

            insert(transaction, transactionalObjects, target, new SubProgressMonitor(monitor, 10));
            transaction.commit(new SubProgressMonitor(monitor, 100));
          }
          catch (Exception ex)
          {
            OM.LOG.error(ex);
          }
          finally
          {
            monitor.done();
            transaction.close();
          }

          return Status.OK_STATUS;
        }
      }.schedule();
    }

    public abstract void insert(CDOTransaction transaction, List<EObject> transactionalObjects, Object target,
        IProgressMonitor monitor);

    public static Operation getFor(Object target, TransferData transferType)
    {
      // Drag within Eclipse?
      if (LocalSelectionTransfer.getTransfer().isSupportedType(transferType))
      {
        EObject[] selectedObjects = getSelectedObjects();
        if (selectedObjects.length != 0)
        {
          ObjectType objectType = ObjectType.valueFor(selectedObjects[0]);
          if (objectType == ObjectType.Object)
          {
            if (target instanceof CDOResource && !((CDOResource)target).isRoot())
            {
              return new ObjectToResource(selectedObjects, target);
            }

            if (target instanceof EObject && !(target instanceof CDOResourceNode))
            {
              return new ObjectToObject(selectedObjects, target);
            }
          }
          else
          {
            if (target instanceof CDOCheckout)
            {
              return new ResourceNodeToCheckout(selectedObjects, target);
            }

            if (target instanceof CDOResourceFolder)
            {
              return new ResourceNodeToFolder(selectedObjects, target);
            }
          }
        }
      }

      return null;
    }

    /**
     * @author Eike Stepper
     */
    private static final class ObjectToResource extends Operation
    {
      public ObjectToResource(EObject[] objects, Object target)
      {
        super(objects, target);
      }

      @Override
      public void insert(CDOTransaction transaction, List<EObject> transactionalObjects, Object target,
          IProgressMonitor monitor)
      {
        CDOResource transactionalTarget = transaction.getObject((CDOResource)target);
        transactionalTarget.getContents().addAll(transactionalObjects);
      }
    }

    /**
     * @author Eike Stepper
     */
    private static final class ObjectToObject extends Operation
    {
      public ObjectToObject(EObject[] objects, Object target)
      {
        super(objects, target);
      }

      @Override
      public void insert(CDOTransaction transaction, List<EObject> transactionalObjects, Object target,
          IProgressMonitor monitor)
      {
        // ((CDOResource)target).getContents().addAll(transactionalObjects);
      }
    }

    /**
     * @author Eike Stepper
     */
    private static final class ResourceNodeToCheckout extends Operation
    {
      public ResourceNodeToCheckout(EObject[] objects, Object target)
      {
        super(objects, target);
      }

      @Override
      public void insert(CDOTransaction transaction, List<EObject> transactionalObjects, Object target,
          IProgressMonitor monitor)
      {
      }
    }

    /**
     * @author Eike Stepper
     */
    private static final class ResourceNodeToFolder extends Operation
    {
      public ResourceNodeToFolder(EObject[] objects, Object target)
      {
        super(objects, target);
      }

      @Override
      public void insert(CDOTransaction transaction, List<EObject> transactionalObjects, Object target,
          IProgressMonitor monitor)
      {
        CDOResourceFolder transactionalTarget = transaction.getObject((CDOResourceFolder)target);
        EList<CDOResourceNode> nodes = transactionalTarget.getNodes();

        for (EObject transactionalObject : transactionalObjects)
        {
          nodes.add((CDOResourceNode)transactionalObject);
        }
      }
    }
  }
}
