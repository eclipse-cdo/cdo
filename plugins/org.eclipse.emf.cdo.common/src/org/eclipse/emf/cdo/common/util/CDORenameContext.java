/*
 * Copyright (c) 2015, 2021, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.util;

/**
 * Represents the context of a rename operation as implemented by <code>RenameDialog</code>
 * or <code>RenameHandler</code>.
 *
 * @author Eike Stepper
 * @since 4.4
 */
public interface CDORenameContext
{
  public String getType();

  public String getName();

  public void setName(String name);

  public String validateName(String name);

  /**
   * A {@link CDORenameContext rename context} that provides access to the {@link #getElement() element}
   * to be renamed.
   *
   * @since 4.15
   */
  public interface WithElement extends CDORenameContext
  {
    public Object getElement();
  }
}
