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
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.internal.protocol.id.CDOIDObjectFactoryImpl;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.protocol.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.protocol.id.CDOIDUtil;
import org.eclipse.emf.cdo.util.ServerException;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class OpenSessionRequest extends RequestWithConfirmation<OpenSessionResult>
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, OpenSessionRequest.class);

  private String repositoryName;

  private boolean disableLegacyObjects;

  public OpenSessionRequest(IChannel channel, String repositoryName, boolean disableLegacyObjects)
  {
    super(channel);
    this.repositoryName = repositoryName;
    this.disableLegacyObjects = disableLegacyObjects;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_OPEN_SESSION;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing repositoryName: {0}", repositoryName);
    }
    out.writeString(repositoryName);

    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Writing disableLegacyObjects: {0}", disableLegacyObjects);
    }
    out.writeBoolean(disableLegacyObjects);
  }

  @Override
  protected OpenSessionResult confirming(ExtendedDataInputStream in) throws IOException
  {
    int sessionID = in.readInt();
    if (sessionID == CDOProtocolConstants.ERROR_REPOSITORY_NOT_FOUND)
    {
      String msg = MessageFormat.format("Repository {0} not found", repositoryName);
      throw new ServerException(msg);
    }

    if (sessionID == CDOProtocolConstants.ERROR_NO_SESSION)
    {
      String msg = MessageFormat.format("Failed to open session for repository {0}", repositoryName);
      throw new ServerException(msg);
    }

    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Read sessionID: {0}", sessionID);
    }

    String repositoryUUID = in.readString();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Read repositoryUUID: {0}", repositoryUUID);
    }

    OpenSessionResult result = new OpenSessionResult(sessionID, repositoryUUID);

    for (;;)
    {
      String packageURI = in.readString();
      if (packageURI == null)
      {
        break;
      }

      boolean dynamic = in.readBoolean();
      CDOIDMetaRange metaIDRange = CDOIDUtil.readMetaRange(in);
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Read package info: uri={0}, dynamic={1}, metaIDRange={2}", packageURI, dynamic, metaIDRange);
      }

      result.addPackageInfo(packageURI, dynamic, metaIDRange);
    }

    int classes = in.readInt();
    if (classes == 0)
    {
      result.setCDOIDObjectFactoryClass(CDOIDObjectFactoryImpl.class);
    }
    else
    {
      result.setCDOIDObjectFactoryClass(deserializeClass(in));
      for (int i = 1; i < classes; i++)
      {
        result.addCDOIDObjectClass(deserializeClass(in));
      }
    }

    return result;
  }

  private Class<?> deserializeClass(InputStream in) throws IOException
  {
    ObjectInputStream ois = new ObjectInputStream(in)
    {
      @Override
      protected Class<?> resolveClass(ObjectStreamClass v) throws IOException, ClassNotFoundException
      {
        String className = v.getName();
        ClassLoader loader = OM.class.getClassLoader();

        try
        {
          return loader.loadClass(className);
        }
        catch (ClassNotFoundException ex)
        {
          ex.printStackTrace();
          return super.resolveClass(v);
        }
      }
    };

    try
    {
      return (Class<?>)ois.readObject();
    }
    catch (ClassNotFoundException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }
}
