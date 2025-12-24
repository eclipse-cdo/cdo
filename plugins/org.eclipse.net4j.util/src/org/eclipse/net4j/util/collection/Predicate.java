/*
 * Copyright (c) 2012, 2015, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.collection;

/**
 * @author Eike Stepper
 * @since 3.3
 * @deprecated As of 3.5 use {@link org.eclipse.net4j.util.Predicate}.
 */
@Deprecated
public interface Predicate<T>
{
  @Deprecated
  public boolean apply(T element);
}
