/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.common.id.CDOID;

/**
 * An extension to {@link CDOEditorInput} that provides input object information.
 *
 * @author Eike Stepper
 * @since 4.5
 * @see org.eclipse.ui.IEditorInput
 * @see org.eclipse.emf.cdo.ui.CDOEditorUtil
 */
public interface CDOEditorInput2 extends CDOEditorInput
{
  public CDOID getObjectID();

  public void setObjectID(CDOID objectID);
}
