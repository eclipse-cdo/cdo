/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
