/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.spi;

import org.eclipse.emf.cdo.dawn.editors.IDawnEditorSupport;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * @author Martin Fluegge
 * @since 2.0
 */
public interface IDawnUIElement
{
  public CDOView getView();

  public void setDirty();

  public IDawnEditorSupport getDawnEditorSupport();
}
