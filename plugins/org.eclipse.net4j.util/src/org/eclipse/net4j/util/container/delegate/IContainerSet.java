/*
 * Copyright (c) 2007, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.util.container.SetContainer;

import java.util.Set;

/**
 * A {@link IContainer container} that is a {@link Set}.
 *
 * @author Eike Stepper
 * @see SetContainer
 */
public interface IContainerSet<E> extends IContainerCollection<E>, Set<E>
{
  @Override
  public Set<E> getDelegate();
}
