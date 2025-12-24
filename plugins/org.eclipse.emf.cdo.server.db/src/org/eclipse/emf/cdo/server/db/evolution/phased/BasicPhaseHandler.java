/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db.evolution.phased;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

/**
 * Basic implementation of a {@link Phase.Handler phase handler}.
 *
 * @author Eike Stepper
 * @since 4.14
 * @noreference This package is currently considered <i>provisional</i>.
 * @noimplement This package is currently considered <i>provisional</i>.
 * @noextend This package is currently considered <i>provisional</i>.
 */
public abstract class BasicPhaseHandler extends Lifecycle implements Phase.Handler
{
  private Phase phase;

  /**
   * Creates a basic phase handler.
   */
  public BasicPhaseHandler()
  {
  }

  /**
   * Returns the phase this handler is associated with.
   */
  @Override
  public final Phase getPhase()
  {
    return phase;
  }

  /**
   * Sets the phase this handler is associated with.
   */
  @Override
  public final void setPhase(Phase phase)
  {
    this.phase = phase;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    CheckUtil.checkState(phase, "phase");
  }
}
