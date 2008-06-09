/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.common;

/**
 * @author Eike Stepper
 */
public interface CDOProtocolView
{
  public int getViewID();

  public Type getViewType();

  public CDOProtocolSession getSession();

  /**
   * @author Eike Stepper
   */
  public enum Type
  {
    TRANSACTION, READONLY, AUDIT
  }
}
