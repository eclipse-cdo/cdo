/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainerProvider;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.om.log.OMLogger;

/**
 * @author Eike Stepper
 * @since 3.26
 */
@FunctionalInterface
public interface ExceptionHandler
{
  public void handleException(Object catcher, Throwable ex, String message, OMLogger logger);

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.exceptionHandlers";

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public abstract ExceptionHandler create(String description) throws ProductCreationException;

    public static ExceptionHandler get(IManagedContainer container, String type)
    {
      return container.getElementOrNull(PRODUCT_GROUP, type, NO_DESCRIPTION);
    }

    public static void handle(Object catcher, Throwable ex, String message, OMLogger logger)
    {
      IManagedContainer container = ContainerUtil.getContainer(catcher);
      handle(catcher, ex, message, logger, container);
    }

    public static void handle(IManagedContainerProvider catcher, Throwable ex, String message, OMLogger logger)
    {
      handle(catcher, ex, message, logger, catcher);
    }

    public static void handle(Object catcher, Throwable ex, String message, OMLogger logger, IManagedContainerProvider containerProvider)
    {
      IManagedContainer container = null;

      if (containerProvider != null)
      {
        container = containerProvider.getContainer();
      }

      handle(catcher, ex, message, logger, container);
    }

    public static void handle(Object catcher, Throwable ex, String message, OMLogger logger, IManagedContainer container)
    {
      ExceptionHandler handler = getForLogger(container, logger);
      if (handler != null)
      {
        try
        {
          handler.handleException(catcher, ex, message, logger);
        }
        catch (Throwable ignore)
        {
          //$FALL-THROUGH$
        }
      }
      else
      {
        logger.error(message, ex);
      }
    }

    private static ExceptionHandler getForLogger(IManagedContainer container, OMLogger logger)
    {
      if (container != null)
      {
        if (logger != null)
        {
          String bundleID = logger.getBundle().getBundleID();

          ExceptionHandler handler = get(container, bundleID);
          if (handler != null)
          {
            return handler;
          }
        }

        ExceptionHandler handler = get(container, null);
        if (handler != null)
        {
          return handler;
        }
      }

      return null;
    }
  }
}
