/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.adapters;

import org.eclipse.emf.cdo.session.CDOSession;

import java.util.Date;

/**
 * @author Eike Stepper
 */
public class CDOSessionPropertySource extends CDOPropertySource<CDOSession>
{
  private static final String CATEGORY_SESSION = "Session";

  private static final String CATEGORY_REPOSITORY = "Repository";

  private static final String SESSION_ID = "sessionID"; //$NON-NLS-1$

  private static final String USER_ID = "userID"; //$NON-NLS-1$

  private static final String PASSIVE_UPDATE_ENABLED = "passiveUpdateEnabled"; //$NON-NLS-1$

  private static final String PASSIVE_UPDATE_MODE = "passiveUpdateMode"; //$NON-NLS-1$

  private static final String REPOSITORY_NAME = "repositoryName"; //$NON-NLS-1$

  private static final String REPOSITORY_UUID = "repositoryUUID"; //$NON-NLS-1$

  private static final String REPOSITORY_TYPE = "repositoryType"; //$NON-NLS-1$

  private static final String REPOSITORY_STATE = "repositoryState"; //$NON-NLS-1$

  private static final String REPOSITORY_CREATION_TIME = "repositoryCreationTime"; //$NON-NLS-1$

  private static final String SUPPORTING_AUDITS = "supportingAudits"; //$NON-NLS-1$

  private static final String SUPPORTING_BRANCHES = "supportingBranches"; //$NON-NLS-1$

  public CDOSessionPropertySource(CDOSession object)
  {
    super(object);
    addPropertyDescriptor(CATEGORY_SESSION, SESSION_ID, "ID", "The ID of this session.");
    addPropertyDescriptor(CATEGORY_SESSION, USER_ID, "User", "The ID of the authenticated user of this session.");
    addPropertyDescriptor(CATEGORY_SESSION, PASSIVE_UPDATE_ENABLED, "Passive Updates Enabled",
        "Whether this session is receiving passive updates from the repository.");
    addPropertyDescriptor(CATEGORY_SESSION, PASSIVE_UPDATE_MODE, "Passive Updates Mode",
        "One of INVALIDATIONS, CHANGES, ADDITIONS.");
    addPropertyDescriptor(CATEGORY_REPOSITORY, REPOSITORY_NAME, "Name", "The name of the repository of this session.");
    addPropertyDescriptor(CATEGORY_REPOSITORY, REPOSITORY_UUID, "UUID", "The UUID of the repository of this session.");
    addPropertyDescriptor(CATEGORY_REPOSITORY, REPOSITORY_TYPE, "Type",
        "The type of the repository of this session. One of MASTER, BACKUP, CLONE.");
    addPropertyDescriptor(CATEGORY_REPOSITORY, REPOSITORY_STATE, "State",
        "The state of the repository of this session. One of OFFLINE, SYNCING, ONLINE.");
    addPropertyDescriptor(CATEGORY_REPOSITORY, REPOSITORY_CREATION_TIME, "Creation Time",
        "The creation time of the repository of this session.");
    addPropertyDescriptor(CATEGORY_REPOSITORY, SUPPORTING_AUDITS, "Supporting Audits",
        "Whether the repository of this session is supporting auditing.");
    addPropertyDescriptor(CATEGORY_REPOSITORY, SUPPORTING_BRANCHES, "Supporting Branches",
        "Whether the repository of this session is supporting branching.");
  }

  public Object getPropertyValue(Object id)
  {
    CDOSession session = getObject();
    if (SESSION_ID.equals(id))
    {
      return session.getSessionID();
    }

    if (USER_ID.equals(id))
    {
      return session.getUserID();
    }

    if (PASSIVE_UPDATE_ENABLED.equals(id))
    {
      return session.options().isPassiveUpdateEnabled();
    }

    if (PASSIVE_UPDATE_MODE.equals(id))
    {
      return session.options().getPassiveUpdateMode().toString();
    }

    if (REPOSITORY_NAME.equals(id))
    {
      return session.getRepositoryInfo().getName();
    }

    if (REPOSITORY_UUID.equals(id))
    {
      return session.getRepositoryInfo().getUUID();
    }

    if (REPOSITORY_TYPE.equals(id))
    {
      return session.getRepositoryInfo().getType().toString();
    }

    if (REPOSITORY_STATE.equals(id))
    {
      return session.getRepositoryInfo().getState().toString();
    }

    if (REPOSITORY_CREATION_TIME.equals(id))
    {
      return new Date(session.getRepositoryInfo().getCreationTime());
    }

    if (SUPPORTING_AUDITS.equals(id))
    {
      return session.getRepositoryInfo().isSupportingAudits();
    }

    if (SUPPORTING_BRANCHES.equals(id))
    {
      return session.getRepositoryInfo().isSupportingBranches();
    }

    return null;
  }
}
