/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;

import org.eclipse.net4j.util.container.IContainer;

import java.util.Map;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IRepository extends IContainer<IRepositoryElement>, IQueryHandlerProvider
{
  public String getName();

  public void setName(String name);

  public IStore getStore();

  public void setStore(IStore store);

  public Map<String, String> getProperties();

  public void setProperties(Map<String, String> properties);

  public String getUUID();

  public boolean isSupportingRevisionDeltas();

  public boolean isSupportingAudits();

  public boolean isVerifyingRevisions();

  public IPackageManager getPackageManager();

  public ISessionManager getSessionManager();

  public IRevisionManager getRevisionManager();

  /**
   * @since 2.0
   */
  public INotificationManager getNotificationManager();

  /**
   * @since 2.0
   */
  public IQueryHandlerProvider getQueryHandlerProvider();

  public long getLastMetaID();

  public CDOIDMetaRange getMetaIDRange(int count);

  /**
   * Returns the repository creation time.
   * 
   * @since 2.0
   */
  public long getCreationTime();

  /**
   * Validates the given timeStamp against the repository time.
   * 
   * @throws IllegalArgumentException
   *           if the given timeStamp is less than the repository creation time or greater than the current repository
   *           time.
   * @since 2.0
   */
  public void validateTimeStamp(long timeStamp) throws IllegalArgumentException;

  /**
   * @author Eike Stepper
   */
  public interface Props
  {
    public static final String PROP_OVERRIDE_UUID = "overrideUUID";

    public static final String PROP_SUPPORTING_REVISION_DELTAS = "supportingRevisionDeltas";

    public static final String PROP_SUPPORTING_AUDITS = "supportingAudits";

    public static final String PROP_VERIFYING_REVISIONS = "verifyingRevisions";

    public static final String PROP_CURRENT_LRU_CAPACITY = "currentLRUCapacity";

    public static final String PROP_REVISED_LRU_CAPACITY = "revisedLRUCapacity";
  }
}
