/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.util.container;

/**
 * Initializes standalone managed-container contributions discovered through
 * {@link java.util.ServiceLoader}.
 * <p>
 * Providers must only register factories and element processors during this
 * callback. They must not assume that {@link IManagedContainer#INSTANCE} has
 * been published while initialization is in progress.
 *
 * @author Eike Stepper
 * @since 3.30
 */
public interface IManagedContainerInitializer
{
  /**
   * Initializes the supplied standalone managed container.
   *
   * @param container the container to initialize
   */
  public void initialize(IManagedContainer container);
}
