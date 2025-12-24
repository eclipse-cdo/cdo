/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.properties;

/**
 * Contains a list of {@link Property properties}.
 *
 * @author Eike Stepper
 * @since 3.2
 */
public interface IProperties<RECEIVER> extends IPropertyProvider<RECEIVER>
{
  public Class<RECEIVER> getReceiverType();

  public Property<RECEIVER> getProperty(String name);

  public void add(Property<RECEIVER> property);
}
