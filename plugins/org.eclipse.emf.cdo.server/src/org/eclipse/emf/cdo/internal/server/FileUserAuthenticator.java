/*
 * Copyright (c) 2023, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IRepositoryProtector.UserAuthenticator;
import org.eclipse.emf.cdo.server.IRepositoryProtector.UserInfo;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.Entity;
import org.eclipse.net4j.util.factory.AnnotationFactory.InjectAttribute;
import org.eclipse.net4j.util.factory.AnnotationFactory.InjectElement;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.ICrypter;
import org.eclipse.net4j.util.security.IUserManagement;
import org.eclipse.net4j.util.security.SecurityUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public class FileUserAuthenticator extends UserAuthenticator implements IUserManagement, Entity.Store.Provider
{
  private static final Map<String, String> NO_ATTRIBUTES = Collections.emptyMap();

  private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("([a-zA-Z][a-zA-Z0-9_\\-.]*) *= *(.*)");

  private Path path;

  private boolean portable;

  private ICrypter passwordCrypter;

  private FileTime fileModifiedTime;

  private final Map<String, FileUserInfo> userInfos = new HashMap<>();

  private final FileEntityStore entityStore = new FileEntityStore();

  public FileUserAuthenticator()
  {
  }

  @Override
  public Class<? extends UserInfo> getUserInfoClass()
  {
    return FileUserInfo.class;
  }

  public final Path getPath()
  {
    return path;
  }

  @InjectAttribute(name = "path")
  public final void setPath(Path path)
  {
    checkInactive();
    this.path = path;
  }

  public final boolean isPortable()
  {
    return portable;
  }

  @InjectAttribute(name = "portable")
  public final void setPortable(boolean portable)
  {
    checkInactive();
    this.portable = portable;
  }

  public final ICrypter getPasswordCrypter()
  {
    return passwordCrypter;
  }

  @InjectElement(name = "passwordCrypter", productGroup = ICrypter.PRODUCT_GROUP, descriptionAttribute = "params")
  public final void setPasswordCrypter(ICrypter passwordCrypter)
  {
    checkInactive();
    this.passwordCrypter = passwordCrypter;
  }

  @Override
  public void addUser(String userID, char[] password)
  {
    checkActive();

    synchronized (userInfos)
    {
      if (userInfos.containsKey(userID))
      {
        throw new IllegalStateException("User " + userID + " does already exist");
      }

      String convertedPassword = convertPassword(password);
      userInfos.put(userID, new FileUserInfo(userID, convertedPassword, false));

      try
      {
        saveFile();
      }
      catch (Exception ex)
      {
        userInfos.remove(userID);
        throw WrappedException.wrap(ex);
      }
      catch (Error ex)
      {
        userInfos.remove(userID);
        throw ex;
      }
    }
  }

  @Override
  public void removeUser(String userID)
  {
    checkActive();

    synchronized (userInfos)
    {
      FileUserInfo userInfo = userInfos.remove(userID);
      if (userInfo == null)
      {
        throw new IllegalStateException("User " + userID + " does not exist");
      }

      try
      {
        saveFile();
      }
      catch (Exception ex)
      {
        userInfos.put(userID, userInfo);
        throw WrappedException.wrap(ex);
      }
      catch (Error ex)
      {
        userInfos.put(userID, userInfo);
        throw ex;
      }
    }
  }

  @Override
  public void setPassword(String userID, char[] newPassword)
  {
    checkActive();
    modifyUser(userID, oldUserInfo -> new FileUserInfo(userID, convertPassword(newPassword), oldUserInfo.administrator(), oldUserInfo.attributes()));
  }

  @Override
  public boolean isAdministrator(String userID)
  {
    checkActive();

    synchronized (userInfos)
    {
      try
      {
        reconcileFile();
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }

      FileUserInfo userInfo = userInfos.get(userID);
      return userInfo != null && userInfo.administrator();
    }
  }

  @Override
  public void setAdministrator(String userID, boolean administrator)
  {
    checkActive();
    modifyUser(userID, oldUserInfo -> new FileUserInfo(userID, oldUserInfo.convertedPassword, administrator, oldUserInfo.attributes()));
  }

  public Map<String, String> getAttributes(String userID)
  {
    checkActive();

    synchronized (userInfos)
    {
      try
      {
        reconcileFile();
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }

      FileUserInfo userInfo = userInfos.get(userID);
      if (userInfo != null)
      {
        return userInfo.attributes();
      }

      return NO_ATTRIBUTES;
    }
  }

  public void setAttributes(String userID, Map<String, String> attributes)
  {
    checkActive();
    modifyUser(userID, oldUserInfo -> new FileUserInfo(userID, oldUserInfo.convertedPassword, oldUserInfo.administrator(), attributes));
  }

  @Override
  public Entity.Store getEntityStore()
  {
    return entityStore;
  }

  @Override
  public FileUserInfo authenticateUser(String userID, char[] password)
  {
    checkActive();

    FileUserInfo userInfo;
    synchronized (userInfos)
    {
      try
      {
        reconcileFile();
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }

      userInfo = userInfos.get(userID);
      if (userInfo == null)
      {
        return null;
      }
    }

    String storedPassword = userInfo.convertedPassword;
    if (storedPassword == null)
    {
      return null;
    }

    ICrypter crypter = passwordCrypter;

    if (portable && storedPassword.startsWith("$"))
    {
      String config = storedPassword.substring(1);

      int firstDollar = config.indexOf('$');
      int lastDollar = config.lastIndexOf('$');
      if (firstDollar != -1 && lastDollar != -1)
      {
        String type = config.substring(0, firstDollar);
        String params = firstDollar == lastDollar ? null : config.substring(firstDollar + 1, lastDollar);

        crypter = (ICrypter)getContainer().getElement(ICrypter.PRODUCT_GROUP, type, params);
      }
    }

    String convertedPassword = convertPassword(password, crypter);
    if (convertedPassword == null)
    {
      return null;
    }

    if (!Objects.equals(convertedPassword, storedPassword))
    {
      return null;
    }

    return userInfo;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(path, "path"); //$NON-NLS-1$
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    LifecycleUtil.activate(passwordCrypter);
    reconcileFile();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    synchronized (userInfos)
    {
      userInfos.clear();
      fileModifiedTime = null;
    }

    LifecycleUtil.deactivate(passwordCrypter);
    super.doDeactivate();
  }

  protected final void saveFile() throws Exception
  {
    List<FileUserInfo> list = new ArrayList<>(userInfos.values());
    list.sort(null);

    try (BufferedWriter writer = Files.newBufferedWriter(path))
    {
      for (FileUserInfo userInfo : list)
      {
        String line = toLine(userInfo);
        writer.write(line);
        writer.write(StringUtil.NL);
      }
    }

    fileModifiedTime = Files.getLastModifiedTime(path);
  }

  protected final void reconcileFile() throws Exception
  {
    if (Files.exists(path))
    {
      FileTime lastModifiedTime = Files.getLastModifiedTime(path);
      if (fileModifiedTime == null || fileModifiedTime.compareTo(lastModifiedTime) < 0)
      {
        fileModifiedTime = lastModifiedTime;
        userInfos.clear();

        try (BufferedReader reader = Files.newBufferedReader(path))
        {
          String line;
          while ((line = reader.readLine()) != null)
          {
            try
            {
              FileUserInfo userInfo = parseLine(line);
              if (userInfo != null)
              {
                userInfos.put(userInfo.userID(), userInfo);
              }
            }
            catch (Exception ex)
            {
              OM.LOG.error(ex);
            }
          }
        }
      }
    }
    else
    {
      Files.createFile(path);
      fileModifiedTime = Files.getLastModifiedTime(path);

      userInfos.clear();
    }
  }

  private void modifyUser(String userID, Function<FileUserInfo, FileUserInfo> modifier)
  {
    synchronized (userInfos)
    {
      FileUserInfo oldUserInfo = userInfos.remove(userID);
      if (oldUserInfo == null)
      {
        throw new IllegalStateException("User " + userID + " does not exist");
      }

      FileUserInfo newUserInfo = modifier.apply(oldUserInfo);
      userInfos.put(userID, newUserInfo);

      try
      {
        saveFile();
      }
      catch (Exception ex)
      {
        userInfos.put(userID, oldUserInfo);
        throw WrappedException.wrap(ex);
      }
      catch (Error ex)
      {
        userInfos.put(userID, oldUserInfo);
        throw ex;
      }
    }
  }

  private String convertPassword(char[] password)
  {
    return convertPassword(password, passwordCrypter);
  }

  private String convertPassword(char[] password, ICrypter crypter)
  {
    String convertedPassword = SecurityUtil.toString(password);

    if (crypter != null)
    {
      byte[] data = convertedPassword.getBytes(StandardCharsets.UTF_8);
      byte[] crypted = crypter.apply(data);
      convertedPassword = Base64.getEncoder().encodeToString(crypted);

      if (portable)
      {
        convertedPassword = makePortable(convertedPassword, crypter);
      }
    }

    return convertedPassword;
  }

  private static String toLine(FileUserInfo userInfo)
  {
    StringBuilder builder = new StringBuilder();
    builder.append(StringUtil.escape(userInfo.userID(), ':'));
    builder.append(':');
    builder.append(StringUtil.escape(userInfo.convertedPassword, ':'));

    boolean administrator = userInfo.administrator();
    Map<String, String> attributes = userInfo.attributes();

    if (administrator || !attributes.isEmpty())
    {
      builder.append(':');

      if (administrator)
      {
        builder.append("true");
      }

      for (Entry<String, String> entry : attributes.entrySet())
      {
        builder.append(':');
        builder.append(StringUtil.escape(entry.getKey()));
        builder.append('=');
        builder.append(StringUtil.escape(entry.getValue()));
      }
    }

    return builder.toString();
  }

  private static FileUserInfo parseLine(String line)
  {
    line = line.trim();

    if (!StringUtil.isEmpty(line) && !line.startsWith("#"))
    {
      String[] tokens = line.split(":");
      int length = tokens.length;
      if (length > 0)
      {
        String userID = unescape(tokens[0]);
        String convertedPassword = length > 1 ? unescape(tokens[1]) : null;
        boolean administrator = length > 2 ? StringUtil.isTrue(unescape(tokens[2])) : false;
        Map<String, String> attributes = new HashMap<>();

        for (int i = 3; i < length; i++)
        {
          String attribute = unescape(tokens[i]);
          parseAttribute(attribute, attributes);
        }

        return new FileUserInfo(userID, convertedPassword, administrator, attributes);
      }
    }

    return null;
  }

  private static String unescape(String token)
  {
    return StringUtil.unescape(token, ':').trim();
  }

  private static void parseAttribute(String attribute, Map<String, String> attributes)
  {
    Matcher matcher = ATTRIBUTE_PATTERN.matcher(attribute);
    if (matcher.matches())
    {
      String name = matcher.group(1);
      String value = matcher.group(2);
      attributes.put(name, value);
    }
  }

  private static String makePortable(String convertedPassword, ICrypter crypter)
  {
    String prefix = "$" + StringUtil.escape(crypter.getType(), '$') + "$";

    String params = crypter.getParams();
    if (params != null)
    {
      prefix += StringUtil.escape(params, '$') + "$";
    }

    return prefix + convertedPassword;
  }

  public static void checkValidAttribute(String name, String value)
  {
    String attribute = name + "=" + value;
    if (!ATTRIBUTE_PATTERN.matcher(attribute).matches())
    {
      throw new IllegalArgumentException("Illegal attribute: " + attribute);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class FileUserInfo extends UserInfo
  {
    private final String convertedPassword;

    private final boolean administrator;

    private final Map<String, String> attributes;

    public FileUserInfo(String userID, String convertedPassword, boolean administrator)
    {
      this(userID, convertedPassword, administrator, null);
    }

    public FileUserInfo(String userID, String convertedPassword, boolean administrator, Map<String, String> attributes)
    {
      super(userID);
      this.convertedPassword = convertedPassword;
      this.administrator = administrator;
      this.attributes = ObjectUtil.isEmpty(attributes) ? NO_ATTRIBUTES : Collections.unmodifiableMap(new HashMap<>(attributes));

      for (Map.Entry<String, String> entry : this.attributes.entrySet())
      {
        checkValidAttribute(entry.getKey(), entry.getValue());
      }
    }

    public final boolean administrator()
    {
      return administrator;
    }

    public final Map<String, String> attributes()
    {
      return attributes;
    }

    @Override
    protected boolean isStructurallyEqual(UserInfo userInfo)
    {
      FileUserInfo other = (FileUserInfo)userInfo;
      return administrator == other.administrator && attributes.equals(other.attributes);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class FileEntityStore extends Entity.SingleNamespaceComputer
  {
    public FileEntityStore()
    {
      super(CDOProtocolConstants.USER_INFO_NAMESPACE);
    }

    @Override
    protected Collection<String> computeNames()
    {
      return Collections.emptySet();
    }

    @Override
    protected Entity computeEntity(String name)
    {
      FileUserInfo userInfo = userInfos.get(name);
      if (userInfo != null)
      {
        Map<String, String> attributes = userInfo.attributes();
        Entity.Builder builder = entityBuilder(name);

        for (String propertyName : CDOProtocolConstants.USER_INFO_PROPERTIES)
        {
          String attribute = attributes.get(propertyName);
          if (attribute != null)
          {
            builder.property(propertyName, attribute);
          }
        }

        return builder.build();
      }

      return null;
    }
  }
}
