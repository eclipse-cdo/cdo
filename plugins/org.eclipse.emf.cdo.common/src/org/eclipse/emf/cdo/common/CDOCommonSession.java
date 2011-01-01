/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common;

import org.eclipse.net4j.util.collection.Closeable;
import org.eclipse.net4j.util.options.IOptions;
import org.eclipse.net4j.util.options.IOptionsContainer;
import org.eclipse.net4j.util.options.IOptionsEvent;
import org.eclipse.net4j.util.security.IUserAware;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDOCommonSession extends IUserAware, IOptionsContainer, Closeable
{
  public int getSessionID();

  public CDOCommonView[] getViews();

  public CDOCommonView getView(int viewID);

  /**
   * Returns the {@link Options options} of this session.
   */
  public Options options();

  /**
   * @author Simon McDuff
   */
  public interface Options extends IOptions
  {
    public boolean isPassiveUpdateEnabled();

    /**
     * Specifies whether objects will be invalidated due by other users changes.
     * <p>
     * Example:
     * <p>
     * <code>session.setPassiveUpdateEnabled(false);</code>
     * <p>
     * By default this property is enabled. If this property is disabled the latest versions of objects can still be
     * obtained by calling refresh().
     * <p>
     * Passive update can be disabled in cases where more performance is needed and/or more control over when objects
     * will be refreshed.
     * <p>
     * When enabled again, a refresh will be automatically performed to be in sync with the server.
     * 
     * @since 3.0
     */
    public void setPassiveUpdateEnabled(boolean enabled);

    /**
     * @since 3.0
     */
    public PassiveUpdateMode getPassiveUpdateMode();

    /**
     * @since 3.0
     */
    public void setPassiveUpdateMode(PassiveUpdateMode mode);

    /**
     * @author Eike Stepper
     * @since 3.0
     */
    public enum PassiveUpdateMode
    {
      /**
       * This mode delivers change deltas only for change subscriptions, invalidation information for all other objects.
       */
      INVALIDATIONS,

      /**
       * This mode delivers change deltas for all changed objects, whether they have change subscriptions or not.
       * Revisions for new objects are not delivered.
       */
      CHANGES,

      /**
       * This mode delivers change deltas for all changed objects, whether they have change subscriptions or not. In
       * addition full revisions for new objects are delivered.
       */
      ADDITIONS
    }

    /**
     * @author Eike Stepper
     * @since 3.0
     */
    public interface PassiveUpdateEvent extends IOptionsEvent
    {
      public boolean getOldEnabled();

      public boolean getNewEnabled();

      public PassiveUpdateMode getOldMode();

      public PassiveUpdateMode getNewMode();
    }
  }
}
