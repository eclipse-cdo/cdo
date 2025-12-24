/*
 * Copyright (c) 2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import java.io.Reader;

/**
 * An optional extension of the {@link IAppExtension} interface for app extensions that support invocation
 * on the XML configurations of dynamically-managed repositories.  These may be instantiated multiple
 * times, will only be given repository configurations (not Net4j acceptors etc.) and are stopped if and
 * when their associated repositories are deleted.
 *
 * @author Christian W. Damus (CEA LIST)
 * @since 4.3
 */
public interface IAppExtension2 extends IAppExtension
{
  public void startDynamic(Reader xmlConfigReader) throws Exception;
}
