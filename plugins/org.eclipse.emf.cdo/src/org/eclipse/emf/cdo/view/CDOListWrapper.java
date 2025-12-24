/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.common.util.EList;

/**
 * Can wrap given lists in order to augment the behavior of those given lists with custom behavior.
 *
 * @author Eike Stepper
 * @since 4.19
 * @see CDOView.Options#getClearAdapterPolicy()
 */
public interface CDOListWrapper<E>
{
  public EList<E> wrapList(EList<E> list);
}
