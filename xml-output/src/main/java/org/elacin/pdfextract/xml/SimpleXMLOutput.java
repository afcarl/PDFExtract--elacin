/*
 * Copyright 2010 Øyvind Berg (elacin@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elacin.pdfextract.xml;

import org.apache.log4j.Logger;
import org.elacin.pdfextract.Constants;
import org.elacin.pdfextract.geom.Rectangle;
import org.elacin.pdfextract.logical.Formulas;
import org.elacin.pdfextract.style.Style;
import org.elacin.pdfextract.tree.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Collections;
import java.util.List;

import static org.elacin.pdfextract.geom.Sorting.sortStylesById;

/**
 * Created by IntelliJ IDEA.
 * User: elacin
 * Date: 16.01.11
 * Time: 17.14
 * To change this template use File | Settings | File Templates.
 */
public class SimpleXMLOutput {
// ------------------------------ FIELDS ------------------------------

private static final Logger log = Logger.getLogger(SimpleXMLOutput.class);

// -------------------------- PUBLIC METHODS --------------------------

public static void printTree(@NotNull final DocumentNode root, @NotNull final File output) {
    /* write to file */
    log.info("LOG00110:Opening " + output + " for output");

    final PrintStream out;
    try {
        out = new PrintStream(new BufferedOutputStream(new FileOutputStream(output, false),
                8192 * 4), false, "UTF-8");
    } catch (Exception e) {
        throw new RuntimeException("Could not open output file", e);
    }

    try {
        writeDocument(out, root);
    } catch (IOException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }

    out.close();
}

private static void writeDocument(@NotNull final Appendable out,
                                  DocumentNode root) throws IOException {
    out.append("<document>\n");

    writeStyles(out, root.getStyles());

    for (PageNode node : root.getChildren()) {
        writePage(out, node);
    }
    out.append("</document");
}

private static void writeStyles(final Appendable out, List<Style> styles) throws IOException {
    out.append("<styles>\n");

    /* output the styles sorted by id */

    Collections.sort(styles, sortStylesById);
    for (Style style : styles) {
        out.append("<style");
        out.append(" id=\"").append(String.valueOf(style.id)).append("\"");
        out.append(" font=\"").append(style.fontName).append("\"");
        out.append(" size=\"").append(String.valueOf(style.xSize)).append("\"");
        if (style.isItalic()) {
            out.append(" italic=\"true\"");
        }
        if (style.isMathFont()) {
            out.append(" math=\"true\"");
        }
        if (style.isBold()) {
            out.append(" bold=\"true\"");
        }
        out.append("/>\n");
    }
    out.append("</styles>\n");
}

private static void writePage(Appendable out, PageNode page) throws IOException {
    out.append("<page");
    out.append(" num=\"").append(Integer.toString(page.getPageNumber())).append("\"");
    if (Constants.VERBOSE_OUTPUT) {
        writeRectangle(out, page.getPos());
    }
    out.append(">\n");

    for (LayoutRegionNode region : page.getChildren()) {
        writeRegion(out, region);
    }

    out.append("</page>\n");
}

private static void writeRegion(Appendable out, LayoutRegionNode region) throws IOException {
    if (region.isPictureRegion()) {
        out.append("<graphic");

        writeRectangle(out, region.getPos());
        out.append(">");

        out.append(">\n");

        for (AbstractParentNode node : region.getChildren()) {
            if (node instanceof LayoutRegionNode) {
                writeRegion(out, (LayoutRegionNode) node);
            } else if (node instanceof ParagraphNode) {
                writeParagraph(out, (ParagraphNode) node);
            }
        }

        if (region.isPictureRegion()) {
            out.append("</graphic>\n");
        }
    } else {
        for (AbstractParentNode node : region.getChildren()) {
            if (node instanceof LayoutRegionNode) {
                writeRegion(out, (LayoutRegionNode) node);
            } else if (node instanceof ParagraphNode) {
                writeParagraph(out, (ParagraphNode) node);
            }
        }
    }
}

private static void writeParagraph(@NotNull final Appendable out,
                                   final ParagraphNode paragraph) throws IOException {
    out.append("<paragraph");

    writeRectangle(out, paragraph.getPos());

    out.append(">\n");
    for (LineNode line : paragraph.getChildren()) {
        writeLine(out, line);
    }

    out.append("</paragraph>\n");
}

private static void writeLine(@NotNull final Appendable out, LineNode line) throws IOException {
    if (Formulas.textSeemsToBeFormula(line.getChildren())) {
        out.append("<formula>");
        out.append(line.getText());
        out.append("</formula>\n");
    } else {
        out.append("<line");
        out.append(" styleRef=\"").append(String.valueOf(line.findDominatingStyle().id)).append("\"");

        if (Constants.VERBOSE_OUTPUT) {
            writeRectangle(out, line.getPos());
            out.append(">\n");

            for (WordNode word : line.getChildren()) {
                writeWord(out, word);
            }
            out.append("</line>\n");
        } else {
            out.append(">");
            out.append(line.getText());
            out.append("</line>\n");
        }
    }
}

private static void writeRectangle(Appendable sb, Rectangle pos) throws IOException {
    sb.append(" x=\"").append(String.valueOf(pos.getX())).append("\"");
    sb.append(" y=\"").append(String.valueOf(pos.getY())).append("\"");
    sb.append(" w=\"").append(String.valueOf(pos.getWidth())).append("\"");
    sb.append(" h=\"").append(String.valueOf(pos.getHeight())).append("\"");
}

private static void writeWord(@NotNull final Appendable out, WordNode word) throws IOException {
    out.append("<word");
    out.append(" value=\"").append(word.getText()).append("\"");
    out.append(" styleRef=\"").append(String.valueOf(word.getStyle().id)).append("\" ");
    writeRectangle(out, word.getPos());
    out.append("/>\n");
}
}