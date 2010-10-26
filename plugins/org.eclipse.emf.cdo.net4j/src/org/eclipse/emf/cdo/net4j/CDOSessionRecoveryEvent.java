/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Caspar De Groot - maintenance
 */
package org.eclipse.emf.cdo.net4j;

import org.eclipse.emf.cdo.session.CDOSessionEvent;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public interface CDOSessionRecoveryEvent extends CDOSessionEvent
{
  public Type getType();

  /**
   * @author Eike Stepper
   */
  public enum Type
  {
    STARTED, FINISHED
  }
}
