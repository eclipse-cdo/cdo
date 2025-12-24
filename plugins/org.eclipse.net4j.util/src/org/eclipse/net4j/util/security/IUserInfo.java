/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.security;

import java.net.URI;

/**
 * @author Eike Stepper
 * @since 3.27
 */
public interface IUserInfo extends IUserAware
{
  public String getFirstName();

  public String getLastName();

  public default String getShortName()
  {
    String firstName = getFirstName();
    String lastName = getLastName();
    return firstName == null ? lastName : firstName;
  }

  public default String getFullName()
  {
    String firstName = getFirstName();
    String lastName = getLastName();
    return (firstName == null ? "" : firstName + " ") + lastName;
  }

  public default String getInitials()
  {
    String firstName = getFirstName();
    String lastName = getLastName();
    return ((firstName == null ? "" : firstName.substring(0, 1)) + lastName.substring(0, 1)).toUpperCase();
  }

  public URI getAvatar();
}
