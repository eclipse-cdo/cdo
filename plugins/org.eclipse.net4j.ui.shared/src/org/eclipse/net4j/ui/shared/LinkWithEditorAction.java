/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.ui.shared;

import org.eclipse.net4j.ui.internal.shared.messages.Messages;

import org.eclipse.jface.action.Action;

/**
 * @author Eike Stepper
 * @since 4.7
 */
public abstract class LinkWithEditorAction extends Action
{
  public LinkWithEditorAction()
  {
    super(Messages.getString("LinkWithEditorAction_name"), AS_CHECK_BOX);//$NON-NLS-1$
    setToolTipText(Messages.getString("LinkWithEditorAction_tooltip")); //$NON-NLS-1$
    setImageDescriptor(SharedIcons.getDescriptor(SharedIcons.ETOOL_LINK_WITH_EDITOR));
  }

  @Override
  public final void run()
  {
    linkWithEditorChanged(isChecked());
  }

  protected abstract void linkWithEditorChanged(boolean linkWithEditor);
}
