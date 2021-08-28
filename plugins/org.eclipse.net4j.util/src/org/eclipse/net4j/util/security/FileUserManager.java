/*
 * Copyright (c) 2008-2012, 2016, 2017, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.security;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class FileUserManager extends UserManager
{
  private static final boolean FALL_BACK_TO_CONFIG_FOLDER = OMPlatform.INSTANCE.isProperty("net4j.security.FileUserManager.fallBackToConfigFolder");

  private static final boolean FAIL_IF_FILE_DOES_NOT_EXIST = OMPlatform.INSTANCE.isProperty("net4j.security.FileUserManager.failIfFileDoesNotExist");

  private static final String CONFIG_FOLDER_PREFIX = "@config/";

  protected String fileName;

  private File file;

  public FileUserManager()
  {
  }

  public String getFileName()
  {
    return fileName;
  }

  /**
   * Sets the name of the file to be used by this user manager.
   * <p>
   * The {@link #getFile() file} is resolved in the following order:
   * <ol>
   * <li> If it starts with the path segment &quot;@config&quot; the subsequent path segments are interpreted as relative to the {@link OMPlatform#getConfigFolder() config folder}.
   * <li> If it is relative it is interpreted as relative to the application's current directory.
   * <li> Otherwise it is interpreted as absolute.
   * </ol>
   * Unless &quot;-Dnet4j.security.FileUserManager.failIfFileDoesNotExist=true&quot; is specified the resolved file is not required to exist when this user manager is activated.
   * In this case it will be created when {@link #addUser(String, char[]) addUser()} or {@link #removeUser(String) removeUser()} are called.
   * <p>
   * With &quot;-Dnet4j.security.FileUserManager.fallBackToConfigFolder=true&quot; a relative path is resolved in both the application's current folder
   * and the config folder (in this order).
   */
  public void setFileName(String fileName)
  {
    checkInactive();
    this.fileName = fileName;
  }

  /**
   * @since 3.7
   */
  public final File getFile()
  {
    return file;
  }

  /**
   * @since 3.7
   */
  protected File resolveFile(String fileName) throws Exception
  {
    if (StringUtil.isEmpty(fileName))
    {
      return null;
    }

    if (fileName.replace('\\', '/').startsWith(CONFIG_FOLDER_PREFIX))
    {
      return OMPlatform.INSTANCE.getConfigFile(fileName.substring(CONFIG_FOLDER_PREFIX.length()));
    }

    File file = new File(fileName);

    if (FALL_BACK_TO_CONFIG_FOLDER && !file.isFile())
    {
      File configFile = OMPlatform.INSTANCE.getConfigFile(fileName);
      if (configFile != null && configFile.isFile())
      {
        return configFile;
      }
    }

    return file;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();

    file = resolveFile(fileName);
    if (file != null)
    {
      file = file.getCanonicalFile();
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    super.doDeactivate();
    file = null;
  }

  @Override
  protected void load(Map<String, char[]> users) throws IORuntimeException
  {
    if (file != null)
    {
      if (FAIL_IF_FILE_DOES_NOT_EXIST || file.isFile())
      {
        FileInputStream stream = IOUtil.openInputStream(file);

        try
        {
          load(users, stream);
        }
        catch (IOException ex)
        {
          throw new IORuntimeException(ex);
        }
        finally
        {
          IOUtil.closeSilent(stream);
        }
      }
    }
  }

  protected void load(Map<String, char[]> users, InputStream stream) throws IOException
  {
    Properties properties = new Properties();
    properties.load(stream);

    for (Entry<Object, Object> entry : properties.entrySet())
    {
      String userID = (String)entry.getKey();
      char[] password = SecurityUtil.toCharArray((String)entry.getValue());
      users.put(userID, password);
    }
  }

  @Override
  protected void save(Map<String, char[]> users) throws IORuntimeException
  {
    if (file != null)
    {
      file.getParentFile().mkdirs();
      FileOutputStream stream = IOUtil.openOutputStream(file);

      try
      {
        save(users, stream);
      }
      catch (IOException ex)
      {
        throw new IORuntimeException(ex);
      }
      finally
      {
        IOUtil.closeSilent(stream);
      }
    }
  }

  protected void save(Map<String, char[]> users, FileOutputStream stream) throws IOException
  {
    Properties properties = new Properties();

    for (Entry<String, char[]> entry : users.entrySet())
    {
      properties.put(entry.getKey(), SecurityUtil.toString(entry.getValue()));
    }

    String comment = MessageFormat.format("User database {0,date} {0,time,HH:mm:ss:SSS}", System.currentTimeMillis());
    properties.store(stream, comment);
  }
}
