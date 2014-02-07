/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om.monitor;

/**
 * Enumerates the possible probing mode values {@link #OFF}, {@link #STANDARD} and {@link #FULL}.
 *
 * @see Progress#progress(org.eclipse.core.runtime.IProgressMonitor, Probing)
 * @see Progress#progress(org.eclipse.core.runtime.IProgressMonitor, int, Probing)
 *
 * @author Eike Stepper
 * @since 3.4
 */
public enum Probing
{
  OFF, STANDARD, FULL;

  public static final Probing DEFAULT = getDefault();

  private static Probing getDefault()
  {
    String mode = System.getProperty("progress.probing");
    if (FULL.toString().equalsIgnoreCase(mode))
    {
      return FULL;
    }

    if (STANDARD.toString().equalsIgnoreCase(mode) || Boolean.TRUE.toString().equalsIgnoreCase(mode))
    {
      return STANDARD;
    }

    return OFF;
  }
}
