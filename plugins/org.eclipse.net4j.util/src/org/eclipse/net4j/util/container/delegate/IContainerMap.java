/*
 * Copyright (c) 2007, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.container.delegate;

import org.eclipse.net4j.util.container.IContainer;

import java.util.Map;

/**
 * A {@link IContainer container} (of {@link java.util.Map.Entry map entries}) that is a {@link Map}.
 *
 * @author Eike Stepper
 */
public interface IContainerMap<K, V> extends IContainer<Map.Entry<K, V>>, Map<K, V>
{
  public Map<K, V> getDelegate();
}
