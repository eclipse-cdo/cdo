/*
 * Copyright (c) 2008, 2010-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.chat.internal.ui;

import org.eclipse.net4j.buddies.chat.IChat;
import org.eclipse.net4j.buddies.internal.ui.views.CollaborationsPane;
import org.eclipse.net4j.buddies.internal.ui.views.FacilityPane;
import org.eclipse.net4j.buddies.ui.IFacilityPaneCreator;
import org.eclipse.net4j.ui.shared.SharedIcons;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Eike Stepper
 */
public class ChatPaneCreator implements IFacilityPaneCreator
{
  public ChatPaneCreator()
  {
  }

  @Override
  public String getType()
  {
    return IChat.TYPE;
  }

  @Override
  public ImageDescriptor getImageDescriptor()
  {
    return SharedIcons.getDescriptor(SharedIcons.OBJ_CHAT);
  }

  @Override
  public FacilityPane createPane(CollaborationsPane collaborationsPane, int style)
  {
    return new ChatPane(collaborationsPane, style);
  }
}
