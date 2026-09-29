/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.internal.util.bundle.AbstractPlatform;
import org.eclipse.net4j.internal.util.om.LegacyPlatform;
import org.eclipse.net4j.internal.util.om.OSGiBundle;
import org.eclipse.net4j.internal.util.om.OSGiPlatform;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Tests platform selection before and after OSGi bundle activation.
 *
 * @author Eike Stepper
 */
@SuppressWarnings({ "restriction", "deprecation" })
public class OMPlatformBootstrapTest extends AbstractOMTest
{
  public void testClasspathWithOSGiAPIsSelectsLegacyPlatform()
  {
    assertNull(FrameworkUtil.getBundle(AbstractPlatform.class));

    OMPlatform platform = PlatformAccess.select(false, new Object());
    assertTrue(platform instanceof LegacyPlatform);
    assertFalse(platform.isOSGiRunning());
    assertTrue(OMPlatform.INSTANCE instanceof LegacyPlatform);
  }

  public void testOSGiOwnershipBeforeActivationAndLateContextBinding()
  {
    OSGiPlatform platform = (OSGiPlatform)PlatformAccess.select(true, null);
    assertTrue(platform.isOSGiRunning());
    assertNull(platform.getProperty("bootstrap.test")); //$NON-NLS-1$

    BundleContext context = createBundleContext();
    platform.setSystemContext(context);
    assertEquals("bound", platform.getProperty("bootstrap.test")); //$NON-NLS-1$ //$NON-NLS-2$

    OMBundle bundle = platform.bundle("test.bundle", getClass()); //$NON-NLS-1$
    assertTrue(bundle instanceof OSGiBundle);
    bundle.setBundleContext(context);
    assertSame(context, ((OSGiBundle)bundle).getBundleContext());
  }

  private BundleContext createBundleContext()
  {
    InvocationHandler handler = (proxy, method, args) -> {
      if ("getProperty".equals(method.getName())) //$NON-NLS-1$
      {
        return "bound"; //$NON-NLS-1$
      }

      throw new UnsupportedOperationException(method.getName());
    };

    return (BundleContext)Proxy.newProxyInstance(BundleContext.class.getClassLoader(), new Class<?>[] { BundleContext.class }, handler);
  }

  private static final class PlatformAccess extends OSGiPlatform
  {
    PlatformAccess()
    {
      super(null);
    }

    static OMPlatform select(boolean osgiBundle, Object context)
    {
      return PlatformAccess.createPlatform(osgiBundle, context);
    }
  }
}
