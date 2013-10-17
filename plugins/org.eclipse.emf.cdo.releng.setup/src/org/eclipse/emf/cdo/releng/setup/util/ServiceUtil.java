/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.util;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;

import org.eclipse.core.runtime.Plugin;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class ServiceUtil extends Plugin
{
  private static BundleContext bundleContext = Activator.getBundleContext();

  private static Map<Object, ServiceReference<?>> services = new IdentityHashMap<Object, ServiceReference<?>>();

  public static <T> T getService(Class<T> serviceClass)
  {
    String serviceName = serviceClass.getName();
    ServiceReference<?> serviceRef = bundleContext.getServiceReference(serviceName);
    if (serviceRef == null)
    {
      throw new IllegalStateException("Missing OSGi service " + serviceName);
    }

    @SuppressWarnings("unchecked")
    T service = (T)bundleContext.getService(serviceRef);
    services.put(service, serviceRef);
    return service;
  }

  public static void ungetService(Object service)
  {
    if (service != null)
    {
      ServiceReference<?> serviceRef = services.remove(service);
      if (serviceRef != null)
      {
        bundleContext.ungetService(serviceRef);
      }
    }
  }
}
