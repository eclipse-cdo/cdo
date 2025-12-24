/*
 * Copyright (c) 2008, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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

import java.util.Set;

/**
 * A default implementation of a {@link IContainerSet container set}.
 *
 * @author Eike Stepper
 */
public class ContainerSet<E> extends ContainerCollection<E> implements IContainerSet<E>
{
  public ContainerSet(Set<E> delegate)
  {
    super(delegate);
  }

  @Override
  public Set<E> getDelegate()
  {
    return (Set<E>)super.getDelegate();
  }
}
