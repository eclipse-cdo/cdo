/*
 * Copyright (c) 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;
import java.io.Serializable;

/**
 * The result of an authentication operation. Carries a userID and a crypted token.
 *
 * @author Eike Stepper
 * @since 4.0
 * @deprecated As of 4.2
 */
@Deprecated
public final class CDOAuthenticationResult implements Serializable
{
  private static final long serialVersionUID = 1L;

  private String userID;

  private byte[] cryptedToken;

  @Deprecated
  public CDOAuthenticationResult(String userID, byte[] cryptedToken)
  {
    this.userID = userID;
    this.cryptedToken = cryptedToken;
  }

  @Deprecated
  public CDOAuthenticationResult(ExtendedDataInput in) throws IOException
  {
    userID = in.readString();
    cryptedToken = in.readByteArray();
  }

  @Deprecated
  public void write(ExtendedDataOutput out) throws IOException
  {
    out.writeString(userID);
    out.writeByteArray(cryptedToken);
  }

  @Deprecated
  public String getUserID()
  {
    return userID;
  }

  @Deprecated
  public byte[] getCryptedToken()
  {
    return cryptedToken;
  }
}
