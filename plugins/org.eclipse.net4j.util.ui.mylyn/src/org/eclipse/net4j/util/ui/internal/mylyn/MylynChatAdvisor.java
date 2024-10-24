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
import org.eclipse.net4j.util.ui.chat.ChatAdvisor;
import org.eclipse.net4j.util.ui.chat.ChatComposite;
import org.eclipse.net4j.util.ui.chat.EntryField;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.mylyn.internal.wikitext.ui.editor.MarkupProjectionViewer;
import org.eclipse.mylyn.internal.wikitext.ui.editor.syntax.FastMarkupPartitioner;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.ui.WikiText;
import org.eclipse.mylyn.wikitext.ui.editor.MarkupSourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.ui.editors.text.EditorsUI;

import java.util.Objects;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public final class MylynChatAdvisor extends ChatAdvisor<IDocument>
{
  private final MarkupLanguage markupLanguage;

  public MylynChatAdvisor(String configuration)
  {
    super(configuration);

    String name = Objects.requireNonNullElse(configuration, "Markdown");
    markupLanguage = WikiText.getMarkupLanguage(name);
  }

  @Override
  public String getEntry(ChatComposite chatComposite)
  {
    IDocument document = getData(chatComposite);
    return document.get();
  }

  @Override
  public void setEntry(ChatComposite chatComposite, String entry)
  {
    IDocument document = getData(chatComposite);
    document.set(entry);
  }

  @Override
  public Scrollable createEntryControl(EntryField entryField)
  {
    ChatComposite chatComposite = entryField.getParent();

    IDocument document = new Document();
    setData(chatComposite, document);

    FastMarkupPartitioner partitioner = new FastMarkupPartitioner();
    partitioner.setMarkupLanguage(markupLanguage == null ? null : markupLanguage.clone());
    partitioner.connect(document);
    document.setDocumentPartitioner(partitioner);

    MarkupProjectionViewer viewer = new MarkupProjectionViewer(entryField, null, null, false, SWT.V_SCROLL | SWT.WRAP);
    viewer.configure(new MarkupSourceViewerConfiguration(EditorsUI.getPreferenceStore()));

    StyledText text = viewer.getTextWidget();
    text.setData(MarkupLanguage.class.getName(), markupLanguage);
    text.setData(ISourceViewer.class.getName(), viewer);
    text.setBackground(entryField.getEntryBackground());

    text.addVerifyKeyListener(e -> {
      if ((e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) && (e.stateMask & SWT.MODIFIER_MASK) == 0)
      {
        sendEntry(chatComposite);
        e.doit = false;
      }
    });

    // ISharedTextColors sharedTextColors = EditorsUI.getSharedTextColors();
    // ProjectionSupport projectionSupport = new ProjectionSupport(viewer, null, sharedTextColors);
    // projectionSupport.install();

    document.addDocumentListener(new IDocumentListener()
    {
      @Override
      public void documentAboutToBeChanged(DocumentEvent event)
      {
        // Do nothing.
      }

      @Override
      public void documentChanged(DocumentEvent event)
      {
        String entry = document.get().trim();
        handleEntryModify(chatComposite, entry);
      }
    });

    viewer.setDocument(document);
    return text;
  }

  @Override
  public void render(StringBuilder html, String markup)
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
  public static final class Factory extends ChatAdvisor.Factory
  {
    public static final String TYPE = "mylyn";

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public ChatAdvisor<IDocument> create(String configuration) throws ProductCreationException
    {
      return new MylynChatAdvisor(configuration);
    }
  }
}
