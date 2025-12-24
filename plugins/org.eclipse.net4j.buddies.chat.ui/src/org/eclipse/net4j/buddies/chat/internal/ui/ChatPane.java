/*
 * Copyright (c) 2008-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.chat.internal.ui;

import org.eclipse.net4j.buddies.chat.IChat;
import org.eclipse.net4j.buddies.chat.IComment;
import org.eclipse.net4j.buddies.chat.internal.ui.messages.Messages;
import org.eclipse.net4j.buddies.internal.chat.CommentEvent;
import org.eclipse.net4j.buddies.internal.ui.views.CollaborationsPane;
import org.eclipse.net4j.buddies.internal.ui.views.FacilityPane;
import org.eclipse.net4j.ui.shared.SharedIcons;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.ui.actions.SafeAction;
import org.eclipse.net4j.util.ui.widgets.SashComposite;

import org.eclipse.jface.action.IContributionManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 */
public class ChatPane extends FacilityPane
{
  private SashComposite sashComposite;

  private Text input;

  private Text output;

  public ChatPane(CollaborationsPane collaborationsPane, int style)
  {
    super(collaborationsPane, style);
  }

  @Override
  protected void handleEvent(IEvent event) throws Exception
  {
    if (event instanceof CommentEvent)
    {
      CommentEvent e = (CommentEvent)event;
      IComment comment = e.getComment();
      String text = comment.getText();
      output.append(comment.getSenderID() + ": " + text + StringUtil.NL); //$NON-NLS-1$
    }
  }

  @Override
  protected Control createUI(Composite parent)
  {
    sashComposite = new SashComposite(parent, SWT.NONE, 16, 80)
    {
      @Override
      protected Control createControl1(Composite parent)
      {
        output = new Text(parent, SWT.MULTI);
        return output;
      }

      @Override
      protected Control createControl2(Composite parent)
      {
        input = new Text(parent, SWT.MULTI);
        input.addKeyListener(new KeyAdapter()
        {
          @Override
          public void keyPressed(KeyEvent e)
          {
            if ((e.character == SWT.CR || e.character == SWT.LF) && e.stateMask == 0)
            {
              ((IChat)getFacility()).sendComment(input.getText());
              input.setText(""); //$NON-NLS-1$
              e.doit = false;
            }
          }
        });

        return input;
      }
    };

    sashComposite.setVertical(true);
    return sashComposite;
  }

  @Override
  protected void fillCoolBar(IContributionManager manager)
  {
    manager.add(new SafeAction(Messages.getString("ChatPane.2"), Messages.getString("ChatPane.3"), SharedIcons //$NON-NLS-1$ //$NON-NLS-2$
        .getDescriptor(SharedIcons.ETOOL_VERTICAL))
    {
      @Override
      protected void safeRun() throws Exception
      {
        sashComposite.setVertical(true);
      }
    });

    manager.add(new SafeAction(Messages.getString("ChatPane.4"), Messages.getString("ChatPane.5"), SharedIcons //$NON-NLS-1$ //$NON-NLS-2$
        .getDescriptor(SharedIcons.ETOOL_HORIZONTAL))
    {
      @Override
      protected void safeRun() throws Exception
      {
        sashComposite.setVertical(false);
      }
    });
  }
}
