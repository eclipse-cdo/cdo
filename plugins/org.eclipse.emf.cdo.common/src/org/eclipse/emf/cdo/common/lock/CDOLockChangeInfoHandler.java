/*
 * Copyright (c) 2011, 2012, 2015, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.common.lock;

/**
 * A call-back interface that indicates the ability to <i>handle</i> {@link CDOLockChangeInfo lock-change infos} that
 * are passed from other entities.
 *
 * @author Caspar De Groot
 * @since 4.1
 */
@FunctionalInterface
public interface CDOLockChangeInfoHandler
{
  public void handleLockChangeInfo(CDOLockChangeInfo lockChangeInfo);
}
