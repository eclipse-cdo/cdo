/*
 * Copyright (c) 2015, 2016, 2019, 2020, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.handlers;

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.handlers.AbstractBaseHandler;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class AbstractObjectHandler extends AbstractBaseHandler<EObject>
{
  private CDOCheckout checkout;

  public AbstractObjectHandler(Boolean multi)
  {
    super(EObject.class, multi);
  }

  public final CDOCheckout getCheckout()
  {
    return checkout;
  }

  @Override
  protected boolean updateSelection(ISelection selection)
  {
    checkout = null;
    if (super.updateSelection(selection))
    {
      for (EObject eObject : elements)
      {
        CDOCheckout objectCheckout = CDOExplorerUtil.getCheckout(eObject);
        if (objectCheckout == null || !objectCheckout.isOpen())
        {
          checkout = null;
          return false;
        }

        if (checkout == null)
        {
          checkout = objectCheckout;
        }
        else if (checkout != objectCheckout)
        {
          checkout = null;
          return false;
        }
      }

      return true;
    }

    return false;
  }

  @Override
  protected List<EObject> collectElements(ISelection selection)
  {
    List<Object> elements = UIUtil.getElements(selection);
    if (elements != null)
    {
      List<Object> result = new ArrayList<>();

      for (Object element : elements)
      {
        if (element instanceof CDOElement)
        {
          CDOElement checkoutElement = (CDOElement)element;
          for (Object child : checkoutElement.getChildren())
          {
            result.add(child);
          }
        }
        else
        {
          result.add(element);
        }
      }

      selection = new StructuredSelection(result);
    }

    return super.collectElements(selection);
  }

  @Override
  protected void doExecute(ExecutionEvent event, IProgressMonitor monitor) throws Exception
  {
    CDOView originalView = CDOUtil.getView(elements.get(0));
    boolean newTransaction = originalView.isReadOnly();
    CDOTransaction transaction = newTransaction ? checkout.openTransaction() : (CDOTransaction)originalView;

    try
    {
      List<EObject> transactionalElements;

      if (newTransaction)
      {
        transactionalElements = new ArrayList<>();

        for (EObject element : elements)
        {
          ConcurrencyUtil.checkCancelation(monitor);

          EObject transactionalElement = transaction.getObject(element);
          transactionalElements.add(transactionalElement);
        }
      }
      else
      {
        transactionalElements = elements;
      }

      if (doExecute(event, transactionalElements, monitor))
      {
        if (newTransaction)
        {
          transaction.commit(monitor);
        }
      }
    }
    finally
    {
      if (newTransaction)
      {
        transaction.close();
      }
    }
  }

  protected abstract boolean doExecute(ExecutionEvent event, List<EObject> transactionalElements, IProgressMonitor monitor);
}
