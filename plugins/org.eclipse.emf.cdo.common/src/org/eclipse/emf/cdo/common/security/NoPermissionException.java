/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.security;

import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

/**
 * A {@link SecurityException security exception} indicating the lack of permission required to do something.
 *
 * @author Eike Stepper
 * @since 4.1
 */
public class NoPermissionException extends SecurityException
{
  private static final long serialVersionUID = 1L;

  private Object protectableObject;

  /**
   * @since 4.3
   */
  public NoPermissionException(Object protectableObject, CDORevisionProvider revisionProvider)
  {
    this(protectableObject, "No permission to access " + format(protectableObject, revisionProvider));
  }

  public NoPermissionException(Object protectableObject)
  {
    this(protectableObject, (CDORevisionProvider)null);
  }

  public NoPermissionException(Object protectableObject, String message)
  {
    super(message);
    this.protectableObject = protectableObject;
  }

  public Object getProtectableObject()
  {
    return protectableObject;
  }

  private static String format(Object object, CDORevisionProvider revisionProvider)
  {
    String result = object.toString();

    if (object instanceof InternalCDORevision)
    {
      InternalCDORevision revision = (InternalCDORevision)object;
      if (revision.isResourceNode())
      {
        if (revisionProvider != null)
        {
          result += "(" + CDORevisionUtil.getResourceNodePath(revision, revisionProvider) + ")";
        }
        else
        {
          result += "(" + revision.getResourceNodeName() + ")";
        }
      }
    }

    return result;
  }
}
