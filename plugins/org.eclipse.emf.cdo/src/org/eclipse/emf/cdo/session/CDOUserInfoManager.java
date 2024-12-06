/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.session;

import org.eclipse.net4j.util.collection.Entity;

import java.util.Arrays;
import java.util.Map;

/**
 * Provides additional user information for given user IDs in the context of a {@link CDOSession session}.
 * User information {@link Entity entities} are requested from the repository of the {@link #getSession() session}
 * and cached in this user info manager.
 *
 * @author Eike Stepper
 * @since 4.26
 */
public interface CDOUserInfoManager
{
  public CDOSession getSession();

  public default Entity getUserInfo(String userID)
  {
    Map<String, Entity> userInfos = getUserInfos(userID);
    return userInfos.get(userID);
  }

  public default Map<String, Entity> getUserInfos(String... userIDs)
  {
    return getUserInfos(Arrays.asList(userIDs));
  }

  public Map<String, Entity> getUserInfos(Iterable<String> userIDs);
}
