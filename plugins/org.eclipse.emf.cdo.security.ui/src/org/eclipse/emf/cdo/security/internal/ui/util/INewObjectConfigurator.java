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
