/*
 * Copyright (c) 2015, 2019, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;

/**
 * Listens to {@link CDOViewTargetChangedEvent view target changed events} from the given {@link CDOView view} and
 * adjusts the {@link TreeViewer#setInput(Object) input} of a {@link Tree tree-based} UI accordingly.
 *
 * @author Eike Stepper
 * @since 4.4
 */
public abstract class CDOInvalidRootAgent implements IListener
{
  private static final Object EMPTY_INPUT = new Object();

  private final CDOView view;

  private CDOID inputID;

  public CDOInvalidRootAgent(CDOView view)
  {
    this.view = view;
    view.addListener(this);
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    if (event instanceof CDOViewTargetChangedEvent)
    {
      final CDOViewTargetChangedEvent e = (CDOViewTargetChangedEvent)event;
      UIUtil.getDisplay().asyncExec(new Runnable()
      {
        @Override
        public void run()
        {
          Object input = getRootFromUI();
          if (inputID != null)
          {
            try
            {
              InternalCDOObject object = (InternalCDOObject)view.getObject(inputID);
              if (!object.cdoInvalid())
              {
                setRootToUI(object.cdoInternalInstance());
                inputID = null;
              }
            }
            catch (Exception ex)
            {
              // Ignore
            }
          }
          else if (input instanceof EObject)
          {
            CDOObject object = CDOUtil.getCDOObject((EObject)input);
            if (object.cdoInvalid())
            {
              if (e.getBranchPoint().getTimeStamp() == e.getOldBranchPoint().getTimeStamp())
              {
                inputID = null;
                closeUI();
              }
              else
              {
                inputID = object.cdoID();
                Object emptyRoot = createEmptyRoot(object);
                setRootToUI(emptyRoot);
              }
            }
          }
        }
      });
    }
  }

  public void dispose()
  {
    view.removeListener(this);
  }

  protected Object createEmptyRoot(CDOObject invalidRoot)
  {
    return EMPTY_INPUT;
  }

  protected abstract Object getRootFromUI();

  protected abstract void setRootToUI(Object root);

  protected void closeUI()
  {
  }
}
