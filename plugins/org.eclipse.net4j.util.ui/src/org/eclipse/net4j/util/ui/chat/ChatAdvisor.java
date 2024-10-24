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

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.chat.ChatComposite.Bubble;
import org.eclipse.net4j.util.ui.chat.ChatComposite.BubbleGroup;
import org.eclipse.net4j.util.ui.chat.ChatComposite.DateLine;
import org.eclipse.net4j.util.ui.chat.ChatMessage.Author;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.LinkedList;

/**
 * @author Eike Stepper
 * @since 3.19
 */
public class ChatAdvisor<DATA>
{
  public static final DateTimeFormatter WEEKDAY_FORMATTER = DateTimeFormatter.ofPattern("EEEE");

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);

  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

  private static final String YESTERDAY = OM.BUNDLE.getTranslationSupport().getString("chat.yesterday");

  private static final String TODAY = OM.BUNDLE.getTranslationSupport().getString("chat.today");

  private final String configuration;

  public ChatAdvisor(String configuration)
  {
    this.configuration = configuration;
  }

  public final String getConfiguration()
  {
    return configuration;
  }

  @SuppressWarnings("unchecked")
  public Scrollable createEntryControl(EntryField entryField)
  {
    Text text = new Text(entryField, SWT.MULTI | SWT.V_SCROLL);
    text.setBackground(entryField.getEntryBackground());

    ChatComposite chatComposite = entryField.getParent();

    text.addKeyListener(KeyListener.keyPressedAdapter(e -> {
      if ((e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) && (e.stateMask & SWT.MODIFIER_MASK) == 0)
      {
        sendEntry(chatComposite);
        e.doit = false;
      }
    }));

    setData(chatComposite, (DATA)text);
    return text;
  }

  public String getEntry(ChatComposite chatComposite)
  {
    Text text = (Text)getData(chatComposite);
    return text.getText();
  }

  public void setEntry(ChatComposite chatComposite, String entry)
  {
    Text text = (Text)getData(chatComposite);
    text.setText(entry);
  }

  public void handleEntryModify(ChatComposite chatComposite, String entry)
  {
    UIUtil.asyncExec(chatComposite.getDisplay(), () -> {
      EntryField entryField = chatComposite.getEntryField();
      Point oldSize = entryField.getLastComputedSize();
      if (!entryField.computeSize(SWT.DEFAULT, SWT.DEFAULT).equals(oldSize))
      {
        chatComposite.layout(true);
      }

      chatComposite.getSendButton().setVisible(entry.length() != 0);
    });
  }

  public void sendEntry(ChatComposite chatComposite)
  {
    String entry = getEntry(chatComposite);
    if (entry != null)
    {
      entry = entry.trim();
      if (entry.length() != 0)
      {
        chatComposite.getSendHandler().accept(entry);
        UIUtil.asyncExec(chatComposite.getDisplay(), () -> setEntry(chatComposite, ""));
      }
    }
  }

  public String getDateString(LocalDate date)
  {
    LocalDate today = LocalDate.now();
    if (today.equals(date))
    {
      return TODAY;
    }

    if (today.minusDays(1L).equals(date))
    {
      return YESTERDAY;
    }

    String str = date.format(WEEKDAY_FORMATTER) + ", " + date.format(DATE_FORMATTER);
    return str.replace(" ", "&nbsp;");
  }

  public String getHtmlTemplate()
  {
    try (InputStream in = OM.BUNDLE.getInputStream("html/chat.html"))
    {
      return IOUtil.readText(new InputStreamReader(in));
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public void render(StringBuilder html, DateLine dateLine)
  {
    String dateString = getDateString(dateLine.getDate());

    html.append("<table class=\"date-line\"><tr><td><hr></td><td class=\"date\">&nbsp;");
    html.append(dateString);
    html.append("&nbsp;</td><td><hr></td></tr></table>\n");
  }

  public void render(StringBuilder html, BubbleGroup bubbleGroup)
  {
    Author author = bubbleGroup.getAuthor();
    boolean own = bubbleGroup.isOwn();
    LinkedList<Bubble> bubbles = bubbleGroup.getBubbles();

    html.append("<table class=\"container");
    if (own)
    {
      html.append("-own");
    }

    html.append("\"");
    if (own)
    {
      html.append(" align=\"right\"");
    }
    else
    {
      html.append(" align=\"left\"");
    }

    html.append("><tr><td class=\"avatar-column\" rowspan=\"");
    html.append(1 + bubbles.size());
    html.append("\">");

    URI avatar = author.getAvatar();
    if (own || avatar == null)
    {
      html.append("&nbsp;");
    }
    else
    {
      html.append("<img class=\"avatar\" src=\"" + avatar + "\">");
    }

    html.append("</td><td><div class=\"info\">");

    if (!own)
    {
      String name = author.getShortName();
      if (StringUtil.isEmpty(name))
      {
        name = StringUtil.cap(author.getUserID());
      }

      html.append(name);
      html.append(", ");
    }

    LocalTime time = bubbleGroup.getFirstBubbleTime();
    html.append(time.format(TIME_FORMATTER));
    html.append("</div></td></tr>");

    for (Bubble bubbleRenderable : bubbles)
    {
      html.append("<tr><td class=\"pl\">");
      bubbleRenderable.doRender(html);
      html.append("</td></tr>");
    }

    html.append("</table>\n");
  }

  public void render(StringBuilder html, Bubble bubble)
  {
    BubbleGroup groupRenderable = bubble.getGroupRenderable();
    boolean own = groupRenderable.isOwn();

    html.append("<div class=\"bubble");
    if (own)
    {
      html.append("-own");
    }

    if (bubble.isLast())
    {
      html.append(" bubble-last");
      if (own)
      {
        html.append("-own");
      }
    }

    html.append("\">");
    render(html, bubble.getText());
    html.append("</div>");
  }

  public void render(StringBuilder html, String markup)
  {
    html.append("<p>");
    html.append(markup);
    html.append("</p>");
  }

  @SuppressWarnings("unchecked")
  protected final DATA getData(ChatComposite chatComposite)
  {
    return (DATA)chatComposite.getAdvisorData();
  }

  protected final void setData(ChatComposite chatComposite, DATA data)
  {
    chatComposite.setAdvisorData(data);
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.ui.chatAdvisors";

    public static final String DEFAULT_TYPE = "default";

    protected Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    public Factory()
    {
      this(DEFAULT_TYPE);
    }

    @Override
    public ChatAdvisor<? extends Object> create(String configuration) throws ProductCreationException
    {
      return new ChatAdvisor<>(configuration);
    }
  }
}
