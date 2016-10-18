/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.net4j.util.security;

/**
 * Interface for providers of password credentials updates.
 *
 * @since 3.4
 * @author Christian W. Damus (CEA LIST)
 */
public interface IPasswordCredentialsUpdateProvider
{
  public IPasswordCredentialsUpdate getCredentialsUpdate(String userID, CredentialsUpdateOperation operation);

  public IPasswordCredentialsUpdate getCredentialsUpdate(String realm, String userID, CredentialsUpdateOperation operation);
}
