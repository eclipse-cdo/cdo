/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.transaction;

/**
 * A participant whose locally accumulated changes can be rolled back.
 *
 * @author Eike Stepper
 * @since 4.30
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORollbackable
{
  /**
   * Rolls back local changes.
   */
  public void rollback();
}
