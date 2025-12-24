/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.container.FactoryNotFoundException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 * @since 3.19
 */
@FunctionalInterface
public interface OpenHandler
{
  public boolean handleOpen(IWorkbenchPage page, StructuredViewer viewer, Object selectedElement);

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.ui.openHandlers"; //$NON-NLS-1$

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public abstract OpenHandler create(String description) throws ProductCreationException;

    public static boolean handleOpen(IManagedContainer container, IWorkbenchPage page, StructuredViewer viewer, Object selectedElement)
    {
      for (String type : container.getFactoryTypes(PRODUCT_GROUP))
      {
        try
        {
          OpenHandler openHandler = container.getElementOrNull(PRODUCT_GROUP, type);
          if (openHandler != null)
          {
            if (openHandler.handleOpen(page, viewer, selectedElement))
            {
              return true;
            }
          }
        }
        catch (FactoryNotFoundException ex)
        {
          // Should not happen.
        }
        catch (ProductCreationException ex)
        {
          OM.LOG.error(ex);
        }
      }

      return false;
    }
  }
}
