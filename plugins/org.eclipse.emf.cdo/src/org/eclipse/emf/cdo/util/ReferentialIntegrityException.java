/*
 * Copyright (c) 2011-2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.view.CDOView;

import java.util.List;

/**
 * A {@link DataIntegrityException data integrity exception} that indicates an attempt to create <i>stale references</i>.
 * A stale reference is a reference that points to a target object that does not (or no longer) exist.
 * <p>
 * Detection of referential integrity violations must be explicitly enabled on the server side because it can be expensive:
 *
 * <pre>
    &lt;property name="ensureReferentialIntegrity" value="true"/&gt;
 * </pre>
 * The risk of referential integrity violations can be <b>reduced</b> (but not eliminated) by using local cross reference queries
 * before committing:
 * <p>
 * <ul>
 * <li> {@link CDOView#queryXRefs(org.eclipse.emf.cdo.CDOObject, org.eclipse.emf.ecore.EReference...) CDOView#queryXRefs()}
 * <li> {@link CDOView#queryXRefsAsync(java.util.Set, org.eclipse.emf.ecore.EReference...) CDOView#queryXRefsAsync()}
 * </ul>
 *
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class ReferentialIntegrityException extends DataIntegrityException
{
  private static final long serialVersionUID = 1L;

  private transient List<CDOObjectReference> xRefs;

  public ReferentialIntegrityException(String msg, List<CDOObjectReference> xRefs)
  {
    super(msg);
    this.xRefs = xRefs;
  }

  public List<CDOObjectReference> getXRefs()
  {
    return xRefs;
  }
}
