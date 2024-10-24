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
import org.eclipse.net4j.util.ui.chat.ChatMessage.Author;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
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
  private static final String MESSAGES = "${MESSAGES}";

  private static final int MESSAGES_LENGTH = MESSAGES.length();

  private static final ZoneId ZONE_ID = TimeZone.getDefault().toZoneId();

  private static final String SEND_MESSAGE = OM.BUNDLE.getTranslationSupport().getString("chat.send.message");

  private static Image sendMessageImage;

  private static Image sendMessageHoverImage;

  private final ChatAdvisor<?> advisor;

  private final String ownUserID;

  private final ChatMessage.Provider messageProvider;

  private final Consumer<String> sendHandler;

  private final String htmlTemplate;

  private final int htmlMessagesPos;

  private final Browser messageBrowser;

  private final EntryField entryField;

  private final Label sendButton;

  private Object advisorData;

  public ChatComposite(Composite parent, int style, //
      ChatAdvisor<?> advisor, //
      String ownUserID, //
      ChatMessage.Provider messageProvider, //
      Consumer<String> sendHandler)
  {
    super(parent, style);
    this.advisor = advisor;
    this.ownUserID = ownUserID;
    this.messageProvider = messageProvider;
    this.sendHandler = sendHandler;

    htmlTemplate = advisor.getHtmlTemplate();
    htmlMessagesPos = htmlTemplate.indexOf(MESSAGES);

    setLayout(GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 10).create());
    setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));

    messageBrowser = createMessageBrowser();
    messageBrowser.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, true).create());

    if (sendHandler != null)
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
    advisor.setEntry(this, "");
  }

  public ChatAdvisor<?> getAdvisor()
  {
    return advisor;
  }

  public ChatMessage.Provider getMessageProvider()
  {
    return messageProvider;
  }

  public Browser getMessageBrowser()
  {
    return messageBrowser;
  }

  public void refreshMessageBrowser()
  {
    String html = renderChatHTML();
    messageBrowser.setText(html, true);
  }

  public EntryField getEntryField()
  {
    return entryField;
  }

  public Label getSendButton()
  {
    return sendButton;
  }

  public Consumer<String> getSendHandler()
  {
    return sendHandler;
  }

  @Override
  public boolean setFocus()
  {
    return entryField == null ? messageBrowser.setFocus() : entryField.setFocus();
  }

  protected Browser createMessageBrowser()
  {
    Browser browser = new Browser(this, SWT.EDGE);
    browser.addLocationListener(LocationListener.changedAdapter(e -> messageBrowser.execute("window.scrollTo(0, document.body.scrollHeight)")));
    return browser;
  }

  protected EntryField createEntryField()
  {
    return new EntryField(this, SWT.NONE);
  }

  protected Label createSendButton()
  {
    Label label = new Label(this, SWT.NONE);
    label.setImage(getSendMessageImage());
    label.setToolTipText(SEND_MESSAGE);
    label.addMouseListener(MouseListener.mouseDownAdapter(e -> advisor.sendEntry(this)));
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

  Object getAdvisorData()
  {
    return advisorData;
  }

  void setAdvisorData(Object advisorData)
  {
    this.advisorData = advisorData;
  }

  private String renderChatHTML()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("\n<table width=\"100%\">\n");

    for (Renderable renderable : getRenderables())
    {
      renderable.render(builder);
    }

    builder.append("</table width=\"100%\">\n");

    StringBuilder html = new StringBuilder(htmlTemplate);
    html.replace(htmlMessagesPos, htmlMessagesPos + MESSAGES_LENGTH, builder.toString());
    return html.toString();
  }

  private List<Renderable> getRenderables()
  {
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
  public abstract class Renderable
  {
    private Renderable()
    {
    }

    public final void render(StringBuilder html)
    {
      html.append("<tr><td>");
      doRender(html);
      html.append("</td></tr>");
    }

    protected abstract void doRender(StringBuilder html);
  }

  /**
   * @author Eike Stepper
   */
  public final class DateLine extends Renderable
  {
    private final LocalDate date;

    private DateLine(LocalDate date)
    {
      this.date = date;
    }

    public final LocalDate getDate()
    {
      return date;
    }

    @Override
    protected void doRender(StringBuilder html)
    {
      advisor.render(html, this);
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class BubbleGroup extends Renderable
  {
    private final Author author;

    private final boolean own;

    private final LinkedList<Bubble> bubbles = new LinkedList<>()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public Bubble getFirst()
      {
        return isEmpty() ? null : super.getFirst();
      }

      @Override
      public Bubble getLast()
      {
        return isEmpty() ? null : super.getLast();
      }
    };

    private BubbleGroup(Author author, boolean own)
    {
      this.author = author;
      this.own = own;
    }

    public Author getAuthor()
    {
      return author;
    }

    public boolean isOwn()
    {
      return own;
    }

    public LinkedList<Bubble> getBubbles()
    {
      return bubbles;
    }

    public LocalTime getFirstBubbleTime()
    {
      Bubble bubble = bubbles.getFirst();
      return bubble == null ? null : bubble.getTime();
    }

    public LocalTime getLastBubbleTime()
    {
      Bubble bubble = bubbles.getLast();
      return bubble == null ? null : bubble.getTime();
    }

    public Bubble addBubble(LocalTime time, String text)
    {
      Bubble bubble = new Bubble(this, time, text);
      bubbles.add(bubble);
      return bubble;
    }

    @Override
    protected void doRender(StringBuilder html)
    {
      advisor.render(html, this);
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class Bubble extends Renderable
  {
    private final BubbleGroup groupRenderable;

    private final LocalTime time;

    private final String text;

    private Bubble(BubbleGroup groupRenderable, LocalTime time, String text)
    {
      this.groupRenderable = groupRenderable;
      this.time = time;
      this.text = text;
    }

    public BubbleGroup getGroupRenderable()
    {
      return groupRenderable;
    }

    public LocalTime getTime()
    {
      return time;
    }

    public String getText()
    {
      return text;
    }

    @Override
    protected void doRender(StringBuilder html)
    {
      advisor.render(html, this);
    }

    public boolean isFirst()
    {
      return groupRenderable.bubbles.getFirst() == this;
    }

    public boolean isLast()
    {
      return groupRenderable.bubbles.getLast() == this;
    }
  }
}
