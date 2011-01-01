/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.ecore.EObject;

import java.util.Set;

/**
 * @author Caspar De Groot
 * @since 4.0
 */
public class CommitIntegrityException extends CommitException
{
  private static final long serialVersionUID = 3302652235940254454L;

  private Set<? extends EObject> missingObjects;

  public CommitIntegrityException(String msg, Set<? extends EObject> missingObjects)
  {
    super(msg);
    this.missingObjects = missingObjects;
  }

  public Set<? extends EObject> getMissingObjects()
  {
    return missingObjects;
  }
}
