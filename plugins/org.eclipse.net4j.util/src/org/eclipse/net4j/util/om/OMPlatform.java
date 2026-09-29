/*
 * Copyright (c) 2007-2012, 2015, 2016, 2018, 2019, 2022, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.internal.util.bundle.AbstractPlatform;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainerInitializer;
import org.eclipse.net4j.util.om.log.OMLogFilter;
import org.eclipse.net4j.util.om.log.OMLogHandler;
import org.eclipse.net4j.util.om.trace.OMTraceHandler;

import java.io.File;
import java.util.Properties;
import java.util.ServiceLoader;

/**
 * Represents the platform that {@link OMBundle bundles} are deployed into, whether OSGi {@link #isOSGiRunning() is
 * running} or not.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface OMPlatform
{
  // @Singleton
  public static final OMPlatform INSTANCE = AbstractPlatform.createPlatform();

  public OMBundle bundle(String bundleID, Class<?> accessor);

  public boolean isOSGiRunning();

  /**
   * @since 2.0
   */
  public boolean isExtensionRegistryAvailable();

  public boolean isDebugging();

  public void setDebugging(boolean debugging);

  /**
   * @since 3.2
   */
  public void addLogFilter(OMLogFilter logFilter);

  /**
   * @since 3.2
   */
  public void removeLogFilter(OMLogFilter logFilter);

  public void addLogHandler(OMLogHandler logHandler);

  public void removeLogHandler(OMLogHandler logHandler);

  public void addTraceHandler(OMTraceHandler traceHandler);

  public void removeTraceHandler(OMTraceHandler traceHandler);

  /**
   * @since 3.18
   */
  public File getUserFolder();

  public File getStateFolder();

  public File getConfigFolder();

  public File getConfigFile(String name);

  public Properties getConfigProperties(String name);

  /**
   * @since 3.0
   */
  public String getProperty(String key);

  /**
   * @since 3.0
   */
  public String getProperty(String key, String defaultValue);

  /**
   * @since 3.8
   */
  public int getProperty(String key, int defaultValue);

  /**
   * @since 3.8
   */
  public long getProperty(String key, long defaultValue);

  /**
   * @since 3.8
   */
  public <T extends Enum<T>> T getProperty(String key, Class<T> enumType);

  /**
   * @since 3.8
   */
  public <T extends Enum<T>> T getProperty(String key, T defaultValue);

  /**
   * @since 3.7
   */
  public boolean isProperty(String key);

  /**
   * @since 3.7
   */
  public boolean isProperty(String key, boolean defaultValue);

  /**
   * @since 3.2
   */
  public String[] getCommandLineArgs() throws IllegalStateException;

  /**
   * Creates a new independent managed container appropriate for the current runtime platform, initializes it with
   * the platform and declarative contributions available to it, activates it, and returns it ready for use. In OSGi
   * this is a {@code PluginContainer}; outside OSGi it is a {@code StandaloneContainer}, which discovers
   * {@link IManagedContainerInitializer} providers through {@link ServiceLoader} during activation. Each
   * invocation creates a new container; it is distinct from the canonical global {@link IManagedContainer#INSTANCE}.
   * See also {@link ContainerUtil#createInitializedContainer()} and the raw
   * {@link ContainerUtil#createContainer()}.
   *
   * @param name
   *          the name to assign to the container, or {@code null} to use the platform default name.
   * @return a newly created active container, independent of {@link IManagedContainer#INSTANCE}.
   * @since 3.31
   */
  public IManagedContainer createManagedContainer(String name);

  /**
   * Creates a new independent managed container appropriate for the current runtime platform, initializes it with
   * available platform and declarative contributions, activates it, and returns it ready for use. In OSGi this is a
   * {@code PluginContainer}; outside OSGi it is a {@code StandaloneContainer} whose activation discovers
   * {@link IManagedContainerInitializer} providers through {@link ServiceLoader}. Each invocation returns
   * a new container distinct from the canonical global {@link IManagedContainer#INSTANCE}. See also
   * {@link ContainerUtil#createInitializedContainer()} and the raw {@link ContainerUtil#createContainer()}.
   *
   * @return a newly created active container, independent of {@link IManagedContainer#INSTANCE}.
   * @since 3.31
   */
  public default IManagedContainer createManagedContainer()
  {
    return createManagedContainer(null);
  }
}
