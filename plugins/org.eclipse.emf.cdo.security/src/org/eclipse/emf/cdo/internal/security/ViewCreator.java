/*
 * Copyright (c) 2013, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.security;

import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * @author Eike Stepper
 */
@FunctionalInterface
public interface ViewCreator
{
  public CDOView createView(CDORevisionProvider revisionProvider);
}
