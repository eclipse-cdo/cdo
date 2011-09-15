/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.version;

import java.io.Serializable;

/**
 * @author Eike Stepper
 */
public class BuildState implements Serializable
{
  private static final long serialVersionUID = 1L;

  private String releaseTag;

  private boolean changedSinceRelease;

  private Serializable validatorState;

  BuildState()
  {
  }

  void setReleaseTag(String releaseTag)
  {
    this.releaseTag = releaseTag;
  }

  public String getReleaseTag()
  {
    return releaseTag;
  }

  public boolean isChangedSinceRelease()
  {
    return changedSinceRelease;
  }

  public void setChangedSinceRelease(boolean changedSinceRelease)
  {
    this.changedSinceRelease = changedSinceRelease;
  }

  public Serializable getValidatorState()
  {
    return validatorState;
  }

  public void setValidatorState(Serializable validatorState)
  {
    this.validatorState = validatorState;
  }
}
