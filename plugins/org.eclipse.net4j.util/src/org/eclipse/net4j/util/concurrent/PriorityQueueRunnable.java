/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

/**
 * @author Eike Stepper
 * @since 3.3
 */
public abstract class PriorityQueueRunnable implements Runnable, Comparable<PriorityQueueRunnable>
{
  @Override
  public int compareTo(PriorityQueueRunnable o)
  {
    return getPriority().compareTo(o.getPriority());
  }

  protected abstract Integer getPriority();
}
