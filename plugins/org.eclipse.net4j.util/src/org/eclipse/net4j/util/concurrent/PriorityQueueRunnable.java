/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
  public int compareTo(PriorityQueueRunnable o)
  {
    return getPriority().compareTo(o.getPriority());
  }

  protected abstract Integer getPriority();
}
