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
