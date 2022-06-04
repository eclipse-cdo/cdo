/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionRequest;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionRequest.GlobalRequestHandler;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.OMPlatform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * @author Eike Stepper
 * @since 4.13
 */
public final class UserInfo
{
  private static final String UNKNOWN = "Unknown";

  private String firstName;

  private String lastName;

  private String displayName;

  UserInfo()
  {
    this(null, null, null);
  }

  public UserInfo(String firstName, String lastName, String displayName)
  {
    change(firstName, lastName, displayName);
  }

  public String getFirstName()
  {
    return firstName;
  }

  public String getLastName()
  {
    return lastName;
  }

  public String getDisplayName()
  {
    return displayName;
  }

  @Override
  public String toString()
  {
    return "UserInfo[" + displayName + "]";
  }

  void change(String firstName, String lastName, String displayName)
  {
    this.firstName = StringUtil.isEmpty(firstName) ? UNKNOWN : firstName;
    this.lastName = StringUtil.isEmpty(lastName) ? UNKNOWN : lastName;
    this.displayName = StringUtil.isEmpty(displayName) ? UNKNOWN : displayName;
  }

  void change(UserInfo userInfo)
  {
    change(userInfo.getFirstName(), userInfo.getLastName(), userInfo.getDisplayName());
  }

  byte[] serialize()
  {
    String string = firstName + StringUtil.NL + lastName + StringUtil.NL + displayName;
    return string.getBytes(StandardCharsets.UTF_8);
  }

  void deserialize(byte[] data)
  {
    String string = new String(data, StandardCharsets.UTF_8);
    StringTokenizer tokenizer = new StringTokenizer(string, StringUtil.NL);
    change(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken());
  }

  /**
   * @author Eike Stepper
   */
  public static final class Manager extends Lifecycle
  {
    public static final UserInfo.Manager INSTANCE = new Manager();

    private final Map<CDORemoteSession, UserInfo> remoteUsers = new HashMap<>();

    private UserInfoStorage localUserInfoStorage;

    private UserInfo localUser;

    private GlobalRequestHandler userInfoRequestHandler;

    private GlobalRequestHandler userInfoNotificationHandler;

    private Manager()
    {
    }

    public synchronized UserInfo getLocalUser()
    {
      return localUser;
    }

    public void changeLocalUser(String firstName, String lastName, String displayName)
    {
      UserInfo userInfo;
      CDORemoteSession[] remoteSessions;

      synchronized (this)
      {
        if (localUser != null)
        {
          localUser.change(firstName, lastName, displayName);
        }

        userInfo = localUser;
        remoteSessions = remoteUsers.keySet().toArray(new CDORemoteSession[remoteUsers.size()]);
      }

      if (userInfo != null)
      {
        if (localUserInfoStorage instanceof UserInfoStorage.Writable)
        {
          UserInfoStorage.Writable writable = (UserInfoStorage.Writable)localUserInfoStorage;

          try
          {
            writable.saveUserInfo(userInfo);
          }
          catch (Exception ex)
          {
            OM.LOG.error(ex);
          }
        }

        fireEvent(new UserChangedEvent(null, userInfo));

        byte[] data = userInfo.serialize();
        for (CDORemoteSession remoteSession : remoteSessions)
        {
          new CDORemoteSessionRequest(UserInfoNotificationHandler.TYPE, data) //
              .send(remoteSession);
        }
      }
    }

    public synchronized UserInfo getRemoteUser(CDORemoteSession remoteSession)
    {
      return remoteUsers.computeIfAbsent(remoteSession, k -> {
        UserInfo userInfo = new UserInfo();

        new CDORemoteSessionRequest(UserInfoRequestHandler.TYPE) //
            .onResponse(data -> changeRemoteUser(remoteSession, data)) //
            .send(remoteSession);

        return userInfo;
      });
    }

    void changeRemoteUser(CDORemoteSession remoteSession, byte[] data)
    {
      UserInfo userInfo;
      synchronized (this)
      {
        userInfo = remoteUsers.get(remoteSession);
        if (userInfo != null)
        {
          userInfo.deserialize(data);
        }
      }

      if (userInfo != null)
      {
        fireEvent(new UserChangedEvent(remoteSession, userInfo));
      }
    }

    @Override
    protected void doActivate() throws Exception
    {
      super.doActivate();

      localUserInfoStorage = UserInfoStorage.Factory.get(IPluginContainer.INSTANCE);
      localUser = localUserInfoStorage == null ? null : localUserInfoStorage.loadUserInfo();

      if (localUser == null)
      {
        localUser = new UserInfo();
      }

      userInfoRequestHandler = new UserInfoRequestHandler();
      userInfoNotificationHandler = new UserInfoNotificationHandler();
    }

    @Override
    protected void doDeactivate() throws Exception
    {
      userInfoNotificationHandler.deactivate();
      userInfoNotificationHandler = null;

      userInfoRequestHandler.deactivate();
      userInfoRequestHandler = null;

      localUser = null;
      super.doDeactivate();
    }

    /**
     * @author Eike Stepper
     */
    public final class UserChangedEvent extends Event
    {
      private static final long serialVersionUID = 1L;

      private final CDORemoteSession remoteSession;

      private final UserInfo userInfo;

      protected UserChangedEvent(CDORemoteSession remoteSession, UserInfo userInfo)
      {
        super(Manager.this);
        this.remoteSession = remoteSession;
        this.userInfo = userInfo;
      }

      @Override
      public UserInfo.Manager getSource()
      {
        return (UserInfo.Manager)super.getSource();
      }

      public CDORemoteSession getRemoteSession()
      {
        return remoteSession;
      }

      public UserInfo getUserInfo()
      {
        return userInfo;
      }
    }

    /**
     * @author Eike Stepper
     */
    private static final class UserInfoRequestHandler extends GlobalRequestHandler
    {
      private static final String TYPE = "org.eclipse.emf.cdo.ui.UserInfo";

      public UserInfoRequestHandler()
      {
        super(TYPE);
      }

      @Override
      protected byte[] createResponse(CDORemoteSession sender, byte[] request)
      {
        UserInfo userInfo = INSTANCE.getLocalUser();
        return userInfo.serialize();
      }
    }

    /**
     * @author Eike Stepper
     */
    private static final class UserInfoNotificationHandler extends GlobalRequestHandler
    {
      private static final String TYPE = "org.eclipse.emf.cdo.ui.UserInfoChanged";

      public UserInfoNotificationHandler()
      {
        super(TYPE);
      }

      @Override
      protected byte[] createResponse(CDORemoteSession sender, byte[] request)
      {
        INSTANCE.changeRemoteUser(sender, request);
        return null;
      }
    }

    /**
     * @author Eike Stepper
     */
    public interface UserInfoStorage
    {
      public UserInfo loadUserInfo() throws IOException;

      /**
       * @author Eike Stepper
       */
      public interface Writable extends UserInfoStorage
      {
        public void saveUserInfo(UserInfo userInfo) throws IOException;
      }

      /**
       * @author Eike Stepper
       */
      public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
      {
        public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.ui.userInfoStorages";

        private static final String DEFAULT_TYPE = OMPlatform.INSTANCE.getProperty("org.eclipse.emf.cdo.ui.UserInfoStorageFactory.DEFAULT_TYPE",
            PreferencesUserInfoStorage.Factory.TYPE);

        private static final String DEFAULT_DESCRIPTION = OMPlatform.INSTANCE.getProperty("org.eclipse.emf.cdo.ui.UserInfoStorageFactory.DEFAULT_DESCRIPTION");

        public Factory(String type)
        {
          super(PRODUCT_GROUP, type);
        }

        @Override
        public abstract UserInfoStorage create(String description) throws ProductCreationException;

        public static UserInfoStorage get(IManagedContainer container)
        {
          return get(container, null);
        }

        public static UserInfoStorage get(IManagedContainer container, String type)
        {
          return get(container, type, null);
        }

        public static UserInfoStorage get(IManagedContainer container, String type, String description)
        {
          if (type == null)
          {
            type = DEFAULT_TYPE;
          }

          if (description == null)
          {
            description = DEFAULT_DESCRIPTION;
          }

          return container.getElementOrNull(PRODUCT_GROUP, type, description);
        }
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class PreferencesUserInfoStorage implements UserInfoStorage.Writable
    {
      public PreferencesUserInfoStorage()
      {
      }

      @Override
      public UserInfo loadUserInfo() throws IOException
      {
        String firstName = OM.PREF_USER_FIRST_NAME.getValue();
        String lastName = OM.PREF_USER_LAST_NAME.getValue();
        String displayName = OM.PREF_USER_DISPLAY_NAME.getValue();
        return new UserInfo(firstName, lastName, displayName);
      }

      @Override
      public void saveUserInfo(UserInfo userInfo) throws IOException
      {
        OM.PREF_USER_FIRST_NAME.setValue(userInfo.getFirstName());
        OM.PREF_USER_LAST_NAME.setValue(userInfo.getLastName());
        OM.PREF_USER_DISPLAY_NAME.setValue(userInfo.getDisplayName());
      }

      /**
       * @author Eike Stepper
       */
      public static final class Factory extends UserInfoStorage.Factory
      {
        public static final String TYPE = "preferences";

        public Factory()
        {
          super(TYPE);
        }

        @Override
        public UserInfoStorage create(String description) throws ProductCreationException
        {
          return new PreferencesUserInfoStorage();
        }
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class HomeUserInfoStorage implements UserInfoStorage.Writable
    {
      private static final String PROP_FIRST_NAME = "firstName";

      private static final String PROP_LAST_NAME = "lastName";

      private static final String PROP_DISPLAY_NAME = "displayName";

      private static final File FILE = new File(OM.BUNDLE.getUserLocation(), "user.properties");

      public HomeUserInfoStorage()
      {
        FILE.getParentFile().mkdirs();
      }

      @Override
      public UserInfo loadUserInfo() throws IOException
      {
        if (FILE.isFile())
        {
          FileInputStream in = null;

          try
          {
            in = new FileInputStream(FILE);

            Properties properties = new Properties();
            properties.load(in);

            String firstName = properties.getProperty(PROP_FIRST_NAME);
            String lastName = properties.getProperty(PROP_LAST_NAME);
            String displayName = properties.getProperty(PROP_DISPLAY_NAME);

            return new UserInfo(firstName, lastName, displayName);
          }
          catch (Exception ex)
          {
            OM.LOG.error(ex);
          }
          finally
          {
            IOUtil.close(in);
          }
        }

        return null;
      }

      @Override
      public void saveUserInfo(UserInfo userInfo) throws IOException
      {
        OutputStream out = null;

        try
        {
          out = new FileOutputStream(FILE);

          Properties properties = new Properties();
          properties.setProperty(PROP_FIRST_NAME, userInfo.getFirstName());
          properties.setProperty(PROP_LAST_NAME, userInfo.getLastName());
          properties.setProperty(PROP_DISPLAY_NAME, userInfo.getDisplayName());
          properties.store(out, "Local user information");
        }
        catch (IOException ex)
        {
          OM.LOG.error(ex);
        }
        finally
        {
          IOUtil.close(out);
        }
      }

      public static void saveProperties(File folder, String fileName, Properties properties, String comment)
      {
        OutputStream out = null;

        try
        {
          folder.mkdirs();

          File file = new File(folder, fileName);
          out = new FileOutputStream(file);

          properties.store(out, comment);
        }
        catch (IOException ex)
        {
          OM.LOG.error(ex);
        }
        finally
        {
          IOUtil.close(out);
        }
      }

      /**
       * @author Eike Stepper
       */
      public static final class Factory extends UserInfoStorage.Factory
      {
        public static final String TYPE = "home";

        public Factory()
        {
          super(TYPE);
        }

        @Override
        public UserInfoStorage create(String description) throws ProductCreationException
        {
          return new HomeUserInfoStorage();
        }
      }
    }
  }
}
