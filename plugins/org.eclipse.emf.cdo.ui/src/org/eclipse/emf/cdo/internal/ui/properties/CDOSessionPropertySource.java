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
package org.eclipse.emf.cdo.internal.ui.properties;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;

import java.util.Date;

/**
 * @author Eike Stepper
 */
public class CDOSessionPropertySource extends CDOPropertySource<CDOSession>
{
  private static final String CATEGORY_SESSION = "Session"; //$NON-NLS-1$

  private static final String CATEGORY_REPOSITORY = "Repository"; //$NON-NLS-1$

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

  private static final String STORE_TYPE = "storeType"; //$NON-NLS-1$

  private static final String OBJECT_ID_TYPES = "objectIDTypes"; //$NON-NLS-1$

  public CDOSessionPropertySource(CDOSession object)
  {
    super(object);
    addPropertyDescriptor(CATEGORY_SESSION, SESSION_ID,
        Messages.getString("CDOSessionPropertySource_0"), Messages.getString("CDOSessionPropertySource_3")); //$NON-NLS-1$ //$NON-NLS-2$
    addPropertyDescriptor(CATEGORY_SESSION, USER_ID,
        Messages.getString("CDOSessionPropertySource_4"), Messages.getString("CDOSessionPropertySource_5")); //$NON-NLS-1$ //$NON-NLS-2$
    addPropertyDescriptor(CATEGORY_SESSION, PASSIVE_UPDATE_ENABLED, Messages.getString("CDOSessionPropertySource_6"), //$NON-NLS-1$
        Messages.getString("CDOSessionPropertySource_7")); //$NON-NLS-1$
    addPropertyDescriptor(CATEGORY_SESSION, PASSIVE_UPDATE_MODE, Messages.getString("CDOSessionPropertySource_8"), //$NON-NLS-1$
        Messages.getString("CDOSessionPropertySource_9")); //$NON-NLS-1$
    addPropertyDescriptor(CATEGORY_REPOSITORY, REPOSITORY_NAME,
        Messages.getString("CDOSessionPropertySource_10"), Messages.getString("CDOSessionPropertySource_11")); //$NON-NLS-1$ //$NON-NLS-2$
    addPropertyDescriptor(CATEGORY_REPOSITORY, REPOSITORY_UUID,
        Messages.getString("CDOSessionPropertySource_12"), Messages.getString("CDOSessionPropertySource_13")); //$NON-NLS-1$ //$NON-NLS-2$
    addPropertyDescriptor(CATEGORY_REPOSITORY, REPOSITORY_TYPE, Messages.getString("CDOSessionPropertySource_14"), //$NON-NLS-1$
        Messages.getString("CDOSessionPropertySource_15")); //$NON-NLS-1$
    addPropertyDescriptor(CATEGORY_REPOSITORY, REPOSITORY_STATE, Messages.getString("CDOSessionPropertySource_16"), //$NON-NLS-1$
        Messages.getString("CDOSessionPropertySource_17")); //$NON-NLS-1$
    addPropertyDescriptor(CATEGORY_REPOSITORY, REPOSITORY_CREATION_TIME,
        Messages.getString("CDOSessionPropertySource_18"), //$NON-NLS-1$
        Messages.getString("CDOSessionPropertySource_19")); //$NON-NLS-1$
    addPropertyDescriptor(CATEGORY_REPOSITORY, SUPPORTING_AUDITS, Messages.getString("CDOSessionPropertySource_20"), //$NON-NLS-1$
        Messages.getString("CDOSessionPropertySource_21")); //$NON-NLS-1$
    addPropertyDescriptor(CATEGORY_REPOSITORY, SUPPORTING_BRANCHES, Messages.getString("CDOSessionPropertySource_22"), //$NON-NLS-1$
        Messages.getString("CDOSessionPropertySource_23")); //$NON-NLS-1$
    addPropertyDescriptor(CATEGORY_REPOSITORY, STORE_TYPE, Messages.getString("CDOSessionPropertySource_24"), //$NON-NLS-1$
        Messages.getString("CDOSessionPropertySource_25")); //$NON-NLS-1$
    addPropertyDescriptor(CATEGORY_REPOSITORY, OBJECT_ID_TYPES, Messages.getString("CDOSessionPropertySource_26"), //$NON-NLS-1$
        Messages.getString("CDOSessionPropertySource_27")); //$NON-NLS-1$
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

    if (STORE_TYPE.equals(id))
    {
      return session.getRepositoryInfo().getStoreType();
    }

    if (OBJECT_ID_TYPES.equals(id))
    {
      StringBuilder builder = new StringBuilder();
      for (CDOID.ObjectType objectIDType : session.getRepositoryInfo().getObjectIDTypes())
      {
        if (builder.length() != 0)
        {
          builder.append(", "); //$NON-NLS-1$
        }

        builder.append(objectIDType);
      }

      return builder.toString();
    }

    return null;
  }

  @Override
  public void resetPropertyValue(Object id)
  {
    if (PASSIVE_UPDATE_ENABLED.equals(id))
    {
      getObject().options().setPassiveUpdateEnabled(true);
    }
    else if (PASSIVE_UPDATE_MODE.equals(id))
    {
      getObject().options().setPassiveUpdateMode(PassiveUpdateMode.INVALIDATIONS);
    }
    else
    {
      super.resetPropertyValue(id);
    }
  }

  @Override
  public void setPropertyValue(Object id, Object value)
  {
    if (PASSIVE_UPDATE_ENABLED.equals(id))
    {
      getObject().options().setPassiveUpdateEnabled((Boolean)value);
    }
    else if (PASSIVE_UPDATE_MODE.equals(id))
    {
      getObject().options().setPassiveUpdateMode((PassiveUpdateMode)value);
    }
    else
    {
      super.setPropertyValue(id, value);
    }
  }
}
