/*
 * Copyright (c) 2009-2012, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.session.CDOSessionConfiguration;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOSessionConfiguration extends CDOSessionConfiguration
{
  public InternalCDOSession getSession();

  public InternalCDOSession createSession();

  /**
   * @since 4.23
   */
  public boolean isLoginPeek();

  /**
   * @since 4.23
   */
  public void setLoginPeek(boolean loginPeek);

  /**
   * @since 4.0
   */
  public boolean isMainBranchLocal();

  /**
   * @since 4.0
   */
  public void setMainBranchLocal(boolean local);
}
