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
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.ui.chat.ChatMessage.Author;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * @author Eike Stepper
 * @since 3.19
 */
public class ChatRenderer implements UnaryOperator<String>
{
  public static final DateTimeFormatter WEEKDAY_FORMATTER = DateTimeFormatter.ofPattern("EEEE");

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);

  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

  private static final String YESTERDAY = OM.BUNDLE.getTranslationSupport().getString("chat.yesterday");

  private static final String TODAY = OM.BUNDLE.getTranslationSupport().getString("chat.today");

  private static final String AVATAR_GENERATOR = "generator";

  private static final String NBSP = "&nbsp;";

  private static final String MESSAGES = "${MESSAGES}";

  private static final int MESSAGES_LENGTH = MESSAGES.length();

  private static final boolean ALWAYS_LOAD_HTML_TEMPLATE = OMPlatform.INSTANCE
      .isProperty("org.eclipse.net4j.util.ui.chat.ChatRenderer.ALWAYS_LOAD_HTML_TEMPLATE");

  private Pair<String, String> htmlEnclosure;

  public ChatRenderer()
  {
  }

  public String renderHTML(Iterable<Renderable> renderables, Map<String, Object> properties)
  {
    return build(html -> renderHTML(renderables, html, properties));
  }

  public void renderHTML(Iterable<Renderable> renderables, StringBuilder html, Map<String, Object> properties)
  {
    Pair<String, String> htmlEnclosure = getHTMLEnclosure(properties);
    html.append(htmlEnclosure.getElement1());
    html.append("\n<table width=\"100%\">\n");

    for (Renderable renderable : renderables)
    {
      html.append("<tr><td>");
      renderable.renderHTML(this, html, properties);
      html.append("</td></tr>");
    }

    html.append("</table width=\"100%\">\n");
    html.append(htmlEnclosure.getElement2());
  }

  public void renderHTML(DateLine dateLine, StringBuilder html, Map<String, Object> properties)
  {
    LocalDate date = dateLine.getDate();
    String dateString = getDateString(date, properties);
    dateString = getNonBreakableString(dateString);

    html.append("<table class=\"date-line\"><tr><td><hr></td><td class=\"date\">&nbsp;");
    html.append(dateString);
    html.append("&nbsp;</td><td><hr></td></tr></table>\n");
  }

  public void renderHTML(Author author, StringBuilder html, Map<String, Object> properties)
  {
    URI avatar = author.getAvatar();
    if (avatar == null)
    {
      avatar = getAvatar(author, properties);
    }

    if (avatar != null)
    {
      String scheme = avatar.getScheme();
      if (AVATAR_GENERATOR.equals(scheme))
      {
        String type = avatar.getAuthority();
        String description = StringUtil.safe(avatar.getPath());
        if (description.length() > 0 && description.charAt(0) == '/')
        {
          description = description.substring(1);
        }

        AvatarGenerator generator = IPluginContainer.INSTANCE.getElementOrNull(AvatarGenerator.PRODUCT_GROUP, type, description);
        if (generator != null)
        {
          generator.generateAvatar(author, html, properties);
          return;
        }
      }
      else
      {
        html.append("<img class=\"avatar\" src=\"");
        html.append(avatar);
        html.append("\"");
        appendToolTip(author, html);
        html.append(">");
        return;
      }
    }

    html.append(NBSP);
  }

  public void renderHTML(BubbleGroup bubbleGroup, StringBuilder html, Map<String, Object> properties)
  {
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

    if (own)
    {
      html.append(NBSP);
    }
    else
    {
      Author author = bubbleGroup.getAuthor();
      renderHTML(author, html, properties);
    }

    html.append("</td><td><div class=\"info\">");

    if (!own)
    {
      Author author = bubbleGroup.getAuthor();
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
      bubbleRenderable.renderHTML(this, html, properties);
      html.append("</td></tr>");
    }

    html.append("</table>\n");
  }

  public void renderHTML(Bubble bubble, StringBuilder html, Map<String, Object> properties)
  {
    BubbleGroup group = bubble.getGroup();
    boolean own = group.isOwn();

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
    renderHTML(bubble.getText(), html, properties);
    html.append("</div>");
  }

  public void renderHTML(String markup, StringBuilder html, Map<String, Object> properties)
  {
    html.append("<p>");
    html.append(markup);
    html.append("</p>");
  }

  public String renderHTML(String markup, Map<String, Object> properties)
  {
    return build(html -> renderHTML(markup, html, properties));
  }

  @Override
  public String apply(String markup)
  {
    return build(html -> {
      html.append("<html><head><style>* { padding: 0; margin: 0; border: 0; }</style></html><body>\n");
      renderHTML(markup, html, null);
      html.append("\n</body></html>");
    });
  }

  public String stripMarkup(String markup)
  {
    String html = renderHTML(markup, null);
    return StringUtil.stripHTML(html);
  }

  public String getDateString(LocalDate date, Map<String, Object> properties)
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

    return date.format(WEEKDAY_FORMATTER) + ", " + date.format(DATE_FORMATTER);
  }

  public String getNonBreakableString(String str)
  {
    return str.replace(" ", NBSP);
  }

  public URI getAvatar(Author author, Map<String, Object> properties)
  {
    int colorClassID = Math.abs(author.getUserID().hashCode()) % 4 + 1;
    return URI.create(AVATAR_GENERATOR + "://default/gen" + colorClassID);
  }

  protected Pair<String, String> getHTMLEnclosure(Map<String, Object> properties)
  {
    if (htmlEnclosure == null || ALWAYS_LOAD_HTML_TEMPLATE)
    {
      String htmlTemplate = loadHTMLTemplate();
      int htmlMessagesPos = htmlTemplate.indexOf(MESSAGES);

      htmlEnclosure = Pair.create( //
          htmlTemplate.substring(0, htmlMessagesPos), //
          htmlTemplate.substring(htmlMessagesPos + MESSAGES_LENGTH));
    }

    return htmlEnclosure;
  }

  protected String loadHTMLTemplate()
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

  private static String build(Consumer<StringBuilder> htmlConsumer)
  {
    StringBuilder html = new StringBuilder();
    htmlConsumer.accept(html);
    return html.toString();
  }

  private static void appendToolTip(Author author, StringBuilder html)
  {
    String fullName = author.getFullName();
    if (!StringUtil.isEmpty(fullName))
    {
      html.append(" title=\"");
      html.append(fullName);
      html.append("\"");
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface AvatarGenerator
  {
    public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.ui.avatarGenerators";

    public void generateAvatar(Author author, StringBuilder html, Map<String, Object> properties);

    /**
     * @author Eike Stepper
     */
    public static final class Default implements AvatarGenerator
    {
      private final String colorClass;

      public Default(String colorClass)
      {
        this.colorClass = colorClass;
      }

      @Override
      public void generateAvatar(Author author, StringBuilder html, Map<String, Object> properties)
      {
        html.append("<div class=\"avatar ");
        html.append(colorClass);
        html.append("\"");
        appendToolTip(author, html);
        html.append("><p>");
        html.append(author.getInitials());
        html.append("</p></div>");
      }

      @Override
      public String toString()
      {
        return "AvatarGenerator[default, " + colorClass + "]";
      }

      /**
       * @author Eike Stepper
       */
      public static class Factory extends org.eclipse.net4j.util.factory.Factory
      {
        public static final String TYPE = "default";

        public Factory()
        {
          super(PRODUCT_GROUP, TYPE);
        }

        @Override
        public AvatarGenerator create(String description) throws ProductCreationException
        {
          return new AvatarGenerator.Default(description);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Renderable
  {
    private Renderable()
    {
    }

    protected abstract void renderHTML(ChatRenderer renderer, StringBuilder html, Map<String, Object> properties);
  }

  /**
   * @author Eike Stepper
   */
  public static final class DateLine extends Renderable
  {
    private final LocalDate date;

    DateLine(LocalDate date)
    {
      this.date = date;
    }

    public final LocalDate getDate()
    {
      return date;
    }

    @Override
    protected void renderHTML(ChatRenderer renderer, StringBuilder html, Map<String, Object> properties)
    {
      renderer.renderHTML(this, html, properties);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class BubbleGroup extends Renderable
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

    BubbleGroup(Author author, boolean own)
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
    protected void renderHTML(ChatRenderer renderer, StringBuilder html, Map<String, Object> properties)
    {
      renderer.renderHTML(this, html, properties);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Bubble extends Renderable
  {
    private final BubbleGroup group;

    private final LocalTime time;

    private final String text;

    private Bubble(BubbleGroup group, LocalTime time, String text)
    {
      this.group = group;
      this.time = time;
      this.text = text;
    }

    public BubbleGroup getGroup()
    {
      return group;
    }

    public LocalTime getTime()
    {
      return time;
    }

    public String getText()
    {
      return text;
    }

    public boolean isFirst()
    {
      return group.bubbles.getFirst() == this;
    }

    public boolean isLast()
    {
      return group.bubbles.getLast() == this;
    }

    @Override
    protected void renderHTML(ChatRenderer renderer, StringBuilder html, Map<String, Object> properties)
    {
      renderer.renderHTML(this, html, properties);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.ui.chatRenderers";

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
    public ChatRenderer create(String description) throws ProductCreationException
    {
      return new ChatRenderer();
    }
  }
}
