/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.chat;

import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.ui.EntryControlAdvisor;
import org.eclipse.net4j.util.ui.EntryControlAdvisor.ControlConfig;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.chat.ChatMessage.Author;
import org.eclipse.net4j.util.ui.chat.ChatMessage.Provider;
import org.eclipse.net4j.util.ui.chat.ChatRenderer.BubbleGroup;
import org.eclipse.net4j.util.ui.chat.ChatRenderer.DateLine;
import org.eclipse.net4j.util.ui.chat.ChatRenderer.Renderable;
import org.eclipse.net4j.util.ui.widgets.RoundedEntryField;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 * @since 3.19
 */
public final class ChatComposite extends Composite
{
  private static final ZoneId ZONE_ID = TimeZone.getDefault().toZoneId();

  private static final String SEND_MESSAGE = OM.BUNDLE.getTranslationSupport().getString("chat.send.message");

  private static Image sendMessageImage;

  private static Image sendMessageHoverImage;

  private final Config config;

  private final Browser messageBrowser;

  private final RoundedEntryField entryField;

  private final Label sendButton;

  public ChatComposite(Composite parent, int style, Config config)
  {
    super(parent, style);
    this.config = new Config(config);

    setLayout(GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 10).create());
    setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));

    messageBrowser = createMessageBrowser();
    messageBrowser.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, true).create());

    if (config.getSendHandler() != null)
    {
      entryField = createEntryField();
      entryField.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

      sendButton = createSendButton();
      sendButton.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.END).create());
    }
    else
    {
      entryField = null;
      sendButton = null;
    }

    refreshMessageBrowser();
  }

  public Config getConfig()
  {
    return new Config(config);
  }

  public Browser getMessageBrowser()
  {
    return messageBrowser;
  }

  public RoundedEntryField getEntryField()
  {
    return entryField;
  }

  public Label getSendButton()
  {
    return sendButton;
  }

  @Override
  public boolean setFocus()
  {
    return entryField == null ? messageBrowser.setFocus() : entryField.setFocus();
  }

  public void refreshMessageBrowser()
  {
    List<Renderable> renderables = getRenderables();
    ChatRenderer renderer = config.getChatRenderer();
  
    String html = renderer.renderHTML(renderables);
    messageBrowser.setText(html, true);
  }

  private Browser createMessageBrowser()
  {
    Browser browser = new Browser(this, SWT.EDGE);
    browser.addLocationListener(LocationListener.changedAdapter(e -> messageBrowser.execute("window.scrollTo(0, document.body.scrollHeight)")));
    return browser;
  }

  private RoundedEntryField createEntryField()
  {
    Color entryBackground = new Color(getDisplay(), 241, 241, 241);
    addDisposeListener(e -> entryBackground.dispose());

    EntryControlAdvisor entryControlAdvisor = config.getEntryControlAdvisor();

    ControlConfig controlConfig = new ControlConfig();
    controlConfig.setModifyHandler(control -> handleEntryModify());
    controlConfig.setOkHandler(control -> sendEntry());

    return new RoundedEntryField(this, SWT.NONE, entryBackground, entryControlAdvisor, controlConfig);
  }

  private Label createSendButton()
  {
    Label label = new Label(this, SWT.NONE);
    label.setImage(getSendMessageImage());
    label.setToolTipText(SEND_MESSAGE);
    label.addMouseListener(MouseListener.mouseDownAdapter(e -> sendEntry()));
    label.addMouseTrackListener(new MouseTrackAdapter()
    {
      @Override
      public void mouseEnter(MouseEvent e)
      {
        if (label.isEnabled())
        {
          label.setImage(getSendMessageHoverImage());
        }
      }

      @Override
      public void mouseExit(MouseEvent e)
      {
        if (label.isEnabled())
        {
          label.setImage(getSendMessageImage());
        }
      }
    });

    return label;
  }

  private void handleEntryModify()
  {
    String entry = entryField.getEntry();
    if (entry != null)
    {
      UIUtil.asyncExec(getDisplay(), () -> {
        Point oldSize = entryField.getLastComputedSize();
        if (!entryField.computeSize(SWT.DEFAULT, SWT.DEFAULT).equals(oldSize))
        {
          layout(true);
          refreshMessageBrowser();
        }

        sendButton.setVisible(entry.length() != 0);
      });
    }
  }

  private void sendEntry()
  {
    String entry = entryField.getEntry();
    if (entry != null)
    {
      entry = entry.trim();
      if (entry.length() != 0)
      {
        config.getSendHandler().accept(entry);
        UIUtil.asyncExec(getDisplay(), () -> entryField.setEntry(""));
      }
    }
  }

  private List<Renderable> getRenderables()
  {
    Provider messageProvider = config.getMessageProvider();
    ChatMessage[] messages = messageProvider.getMessages();
    Arrays.sort(messages);

    List<Renderable> renderables = new ArrayList<>();
    DateLine lastDateLine = null;
    BubbleGroup lastBubbleGroup = null;

    for (ChatMessage message : messages)
    {
      LocalDateTime creationTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(message.getCreationTime()), ZONE_ID);

      LocalDate date = creationTime.toLocalDate();
      if (lastDateLine == null || lastDateLine.getDate().isBefore(date))
      {
        lastDateLine = new DateLine(date);
        renderables.add(lastDateLine);
        lastBubbleGroup = null;
      }

      Author author = message.getAuthor();
      String ownUserID = config.getOwnUserID();
      boolean own = Objects.equals(author.getUserID(), ownUserID);

      LocalTime time = creationTime.toLocalTime();
      if (lastBubbleGroup == null || !lastBubbleGroup.getAuthor().equals(author) || time.minusMinutes(5).isAfter(lastBubbleGroup.getLastBubbleTime()))
      {
        lastBubbleGroup = new BubbleGroup(author, own);
        renderables.add(lastBubbleGroup);
      }

      lastBubbleGroup.addBubble(time, message.getContent());
    }

    return renderables;
  }

  private static Image getSendMessageImage()
  {
    if (sendMessageImage == null)
    {
      sendMessageImage = OM.getImageDescriptor("icons/send_message.png").createImage();
    }

    return sendMessageImage;
  }

  private static Image getSendMessageHoverImage()
  {
    if (sendMessageHoverImage == null)
    {
      sendMessageHoverImage = OM.getImageDescriptor("icons/send_message_hover.png").createImage();
    }

    return sendMessageHoverImage;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Config
  {
    private String ownUserID;

    private ChatMessage.Provider messageProvider;

    private ChatRenderer chatRenderer;

    private EntryControlAdvisor entryControlAdvisor;

    private Consumer<String> sendHandler;

    private Config(Config source)
    {
      ownUserID = source.ownUserID;
      messageProvider = source.messageProvider;
      chatRenderer = source.chatRenderer;
      entryControlAdvisor = source.entryControlAdvisor;
      sendHandler = source.sendHandler;
    }

    public Config()
    {
    }

    public String getOwnUserID()
    {
      return ownUserID;
    }

    public void setOwnUserID(String ownUserID)
    {
      this.ownUserID = ownUserID;
    }

    public ChatMessage.Provider getMessageProvider()
    {
      return messageProvider;
    }

    public void setMessageProvider(ChatMessage.Provider messageProvider)
    {
      this.messageProvider = messageProvider;
    }

    public ChatRenderer getChatRenderer()
    {
      return chatRenderer;
    }

    public void setChatRenderer(ChatRenderer chatRenderer)
    {
      this.chatRenderer = chatRenderer;
    }

    public EntryControlAdvisor getEntryControlAdvisor()
    {
      return entryControlAdvisor;
    }

    public void setEntryControlAdvisor(EntryControlAdvisor entryControlAdvisor)
    {
      this.entryControlAdvisor = entryControlAdvisor;
    }

    public Consumer<String> getSendHandler()
    {
      return sendHandler;
    }

    public void setSendHandler(Consumer<String> sendHandler)
    {
      this.sendHandler = sendHandler;
    }
  }
}
