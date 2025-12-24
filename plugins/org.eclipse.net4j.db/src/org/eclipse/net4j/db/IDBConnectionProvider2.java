/*
 * Copyright (c) 2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db;

import org.eclipse.net4j.util.security.IUserAware;

import javax.sql.DataSource;

import java.sql.Connection;

/**
 * Provides a database {@link Connection connection}, roughly comparable with a {@link DataSource data source}.
 *
 * @author Eike Stepper
 * @since 4.3
 */
public interface IDBConnectionProvider2 extends IDBConnectionProvider, IUserAware
{
}
