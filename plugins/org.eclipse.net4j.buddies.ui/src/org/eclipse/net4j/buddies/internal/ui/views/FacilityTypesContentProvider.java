/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.buddies.internal.ui.views;

import org.eclipse.net4j.buddies.BuddiesUtil;
import org.eclipse.net4j.util.ui.StaticContentProvider;

/**
 * @author Eike Stepper
 */
public class FacilityTypesContentProvider extends StaticContentProvider
{
  public FacilityTypesContentProvider()
  {
    super(BuddiesUtil.getFacilityTypes());
  }
}
