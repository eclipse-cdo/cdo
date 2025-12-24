/*
 * Copyright (c) 2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.util;

import org.eclipse.emf.common.command.Command;

/**
 * A protocol for the configuration of newly created elements in the editor.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public interface INewObjectConfigurator
{
  public Command createConfigureCommand(Object newObject);
}
