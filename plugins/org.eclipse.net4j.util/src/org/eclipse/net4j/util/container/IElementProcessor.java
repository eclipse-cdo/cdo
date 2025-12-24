/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2016, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.container;

/**
 * Processes elements of a {@link IManagedContainer managed container} when they're added to the container.
 *
 * @author Eike Stepper
 */
@FunctionalInterface
public interface IElementProcessor
{
  public Object process(IManagedContainer container, String productGroup, String factoryType, String description, Object element);
}
