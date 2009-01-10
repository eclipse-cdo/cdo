/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.model.CDOClass;

import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDORevisionFactory
{
  public CDORevision createRevision(CDOClass cdoClass, CDOID id);

  public CDORevision createRevision(CDODataInput in) throws IOException;
}
