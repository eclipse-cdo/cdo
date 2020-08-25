/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om;

import org.eclipse.net4j.internal.util.bundle.AbstractBundle;

import org.osgi.framework.BundleContext;

/**
 * Various static helper methods for dealing with {@link OMBundle bundles} if OSGi {@link OMPlatform#isOSGiRunning() is
 * running}.
 *
 * @author Eike Stepper
 * @since 3.12
 */
public final class OSGiUtil
{
  private OSGiUtil()
  {
  }

  public static BundleContext getBundleContext(OMBundle bundle)
  {
    return (BundleContext)((AbstractBundle)bundle).getBundleContext();
  }
}
