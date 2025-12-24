/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.common;

import org.eclipse.core.runtime.IAdaptable;

/**
 * @author Eike Stepper
 */
public interface ICollaboration extends IMembershipContainer, IBuddyProvider, IAdaptable
{
  public long getID();

  public String getTitle();

  public String getDescription();

  public Visibility getVisibility();

  public boolean isPublic();

  public void setPublic(String title, String description);

  public void setPrivate();

  public String[] getFacilityTypes();

  public IFacility[] getFacilities();

  public IFacility getFacility(String type);

  public enum Visibility
  {
    PRIVATE, PUBLIC
  }
}
