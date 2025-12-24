/*
 * Copyright (c) 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.ui;

import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.ui.StructuredContentProvider;

/**
 * A {@link StructuredContentProvider structured content provider} that shows the {@link IFactoryKey#getType() factory
 * types} of the {@link #getInput() input}.
 *
 * @author Eike Stepper
 * @since 4.0
 * @deprecated As of 4.1 use {@link org.eclipse.net4j.util.ui.FactoryTypeContentProvider}
 */
@Deprecated
public class FactoryTypeContentProvider extends org.eclipse.net4j.util.ui.FactoryTypeContentProvider
{
  @Deprecated
  public FactoryTypeContentProvider(String productGroup)
  {
    super(productGroup);
  }
}
