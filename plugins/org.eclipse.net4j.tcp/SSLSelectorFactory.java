/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - initial API and implementation
 *    Caspar De Groot (No Magic Asia Ltd.) - initial API and implementation
 */
package org.eclipse.net4j.internal.tcp.ssl;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.Factory;

/**
 * SSLSelectorFactory responses to create SSLSelector.
 * 
 * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
 * @author Caspar De Groot (No Magic Asia Ltd.)
 * @since 4.0
 */
public class SSLSelectorFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.selectors"; //$NON-NLS-1$

  public static final String TYPE = "ssl"; //$NON-NLS-1$

  public SSLSelectorFactory()
  {
    super(PRODUCT_GROUP, TYPE);
  }

  public SSLSelector create(String description)
  {
    return new SSLSelector();
  }

  public static SSLSelector get(IManagedContainer container, String description)
  {
    return (SSLSelector)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}
