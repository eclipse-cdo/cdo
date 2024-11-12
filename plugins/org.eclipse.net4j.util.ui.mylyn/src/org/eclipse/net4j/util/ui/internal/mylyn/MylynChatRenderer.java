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
package org.eclipse.net4j.util.ui.internal.mylyn;

import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.StringBuilderWriter;
import org.eclipse.net4j.util.ui.chat.ChatRenderer;

import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.ui.WikiText;

import java.util.Map;
import java.util.Objects;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public final class MylynChatRenderer extends ChatRenderer
{
  private final MarkupLanguage markupLanguage;

  public MylynChatRenderer(String description)
  {
    String name = Objects.requireNonNullElse(description, "Markdown");
    markupLanguage = WikiText.getMarkupLanguage(name);
  }

  @Override
  public void renderHTML(String markup, StringBuilder html, Map<String, Object> properties)
  {
    StringBuilderWriter out = new StringBuilderWriter(html);
    HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out, false);
    builder.setEmitAsDocument(false);

    MarkupParser parser = new MarkupParser(markupLanguage, builder);
    parser.parse(markup);
  }

  /**
   * @author Eike Stepper
   */
  public static final class Factory extends ChatRenderer.Factory
  {
    public static final String TYPE = "mylyn";

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public ChatRenderer create(String description) throws ProductCreationException
    {
      return new MylynChatRenderer(description);
    }
  }
}
