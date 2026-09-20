/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.common.model;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;

import java.io.Serializable;

/**
 * A symbolic reference to an EMF model element.
 *
 * <p>The reference contains only the symbolic identity of its model element.
 * It does not retain a package registry and does not resolve anything as a
 * side effect of construction, comparison, hashing, or serialization.
 * Resolution is performed explicitly by {@link #resolve(EPackage.Registry)}.
 *
 * @param <T> the EMF type of the referenced model element
 * @author Eike Stepper
 * @since 4.29
 */
public interface CDOModelElementRef<T extends EModelElement> extends Serializable
{
  /**
   * Resolves this reference against the supplied package registry.
   *
   * @param packageRegistry the registry to use for explicit resolution
   * @return the referenced model element, or {@code null} if the symbolic
   *         element is not present in the resolved package
   */
  public T resolve(EPackage.Registry packageRegistry);
}
