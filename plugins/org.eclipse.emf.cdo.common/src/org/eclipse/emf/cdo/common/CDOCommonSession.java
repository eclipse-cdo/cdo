/*
 * Copyright (c) 2009-2012, 2015, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.util.CDOClassNotFoundException;
import org.eclipse.emf.cdo.common.util.CDOPackageNotFoundException;

import org.eclipse.net4j.util.collection.Closeable;
import org.eclipse.net4j.util.options.IOptions;
import org.eclipse.net4j.util.options.IOptionsContainer;
import org.eclipse.net4j.util.options.IOptionsEvent;
import org.eclipse.net4j.util.properties.IPropertiesContainer;
import org.eclipse.net4j.util.security.IUserAware;

import org.eclipse.core.runtime.IAdaptable;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Abstracts the information about CDO sessions that is common to both client and server side.
 *
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOCommonSession extends IAdaptable, IUserAware, IOptionsContainer, IPropertiesContainer, Closeable
{
  public int getSessionID();

  /**
   * @since 4.13
   */
  public long getOpeningTime();

  public CDOCommonView[] getViews();

  public CDOCommonView getView(int viewID);

  /**
   * @since 4.15
   */
  public String[] authorizeOperations(AuthorizableOperation... operations);

  /**
   * Returns the {@link Options options} of this session.
   */
  @Override
  public Options options();

  /**
   * Encapsulates the configuration options of CDO sessions that are common to both client and server side.
   *
   * @author Simon McDuff
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface Options extends IOptions
  {
    /**
     * Returns the {@link CDOCommonSession session} of this options object.
     *
     * @since 4.0
     */
    @Override
    public CDOCommonSession getContainer();

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
     * @since 4.1
     */
    public LockNotificationMode getLockNotificationMode();

    /**
     * @since 4.1
     */
    public void setLockNotificationMode(LockNotificationMode mode);

    /**
     * Enumerates the possible {@link CDOCommonSession.Options#getPassiveUpdateMode() passive update modes} of a CDO
     * session.
     *
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
     * Enumerates the possible {@link CDOCommonSession.Options#getLockNotificationMode() lock notification modes} of a
     * CDO session.
     *
     * @since 4.1
     */
    public enum LockNotificationMode
    {
      /**
       * This mode delivers no lock notifications
       */
      OFF,

      /**
       * This mode delivers lock notifications if one or more views have enabled them.
       */
      IF_REQUIRED_BY_VIEWS,

      /**
       * This mode always delivers lock notifications, even if no views have them enabled, and even if no views are
       * open.
       */
      ALWAYS
    }

    /**
     * An {@link IOptionsEvent options event} fired when the {@link PassiveUpdateMode passive update mode} of a CDO
     * session has changed.
     *
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

    /**
     * An {@link IOptionsEvent options event} fired when the {@link LockNotificationMode lock notification mode} of a
     * CDO session has changed.
     *
     * @author Caspar De Groot
     * @since 4.1
     */
    public interface LockNotificationModeEvent extends IOptionsEvent
    {
      public LockNotificationMode getOldMode();

      public LockNotificationMode getNewMode();
    }
  }

  /**
   * @author Eike Stepper
   * @since 4.15
   */
  public static final class AuthorizableOperation
  {
    private final String id;

    private final Map<String, Object> parameters = new HashMap<>();

    public AuthorizableOperation(String id)
    {
      this.id = id;
    }

    public AuthorizableOperation(CDODataInput in) throws IOException
    {
      id = in.readString();

      int size = in.readXInt();
      for (int i = 0; i < size; i++)
      {
        String key = in.readString();

        try
        {
          Object object = in.readCDORevisionOrPrimitiveOrClassifier();
          parameters.put(key, object);
        }
        catch (CDOPackageNotFoundException e)
        {
          //$FALL-THROUGH$
        }
        catch (CDOClassNotFoundException e)
        {
          //$FALL-THROUGH$
        }
      }
    }

    public final String getID()
    {
      return id;
    }

    public AuthorizableOperation parameter(String key, Object value)
    {
      parameters.put(key, value);
      return this;
    }

    public Object getParameter(String key)
    {
      return parameters.get(key);
    }

    public Map<String, Object> getParameters()
    {
      return Collections.unmodifiableMap(parameters);
    }

    public void write(CDODataOutput out) throws IOException
    {
      out.writeString(id);
      out.writeXInt(parameters.size());

      for (Entry<String, Object> entry : parameters.entrySet())
      {
        out.writeString(entry.getKey());
        out.writeCDORevisionOrPrimitiveOrClassifier(entry.getValue());
      }
    }
  }
}
