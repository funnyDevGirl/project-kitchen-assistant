package io.project.kitchen_assistant.formatter;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class Formatter {

    public static String formatMarkdownToText(String text) {
        String html = formatMarkdownToHtml(text);
        String cleanText = formatHtmlToText(html);
        return cleanText.replaceAll("[\\n\\*]*", "");
    }

    private static String formatMarkdownToHtml(String text) {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(parser.parse(text));
    }

    private static String formatHtmlToText(String html) {
        Document doc = Jsoup.parse(html);
        return doc.body().text();
    }
}
