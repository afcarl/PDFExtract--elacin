/*
 * Copyright 2010 Øyvind Berg (elacin@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.elacin.pdfextract.builder;

import org.apache.pdfbox.util.TextPosition;
import org.elacin.pdfextract.text.Style;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.elacin.pdfextract.util.MathUtils.round;

/**
 * Created by IntelliJ IDEA.
 * User: elacin
 * Date: Mar 18, 2010
 * Time: 2:32:39 PM
 * <p/>
 * <p/>
 * This class is meant to belong to a document, so that all the styles used in a document
 * will be available here. There is some optimization to avoid excessive object creation.
 */
public class DocumentStyles {
    // ------------------------------ FIELDS ------------------------------

    final Map<Float, Style> styles = new HashMap<Float, Style>();

    final Collection<Style> stylesCollection = styles.values();

    // --------------------- GETTER / SETTER METHODS ---------------------

    public Map<Float, Style> getStyles() {
        return styles;
    }

    // -------------------------- PUBLIC METHODS --------------------------

    public Style getStyleForTextPosition(TextPosition position) {
        float result = position.getFontSize();
        result = 31 * result + position.getXScale();
        result = 31 * result + position.getYScale();
        result = 31 * result + position.getFont().hashCode();
        result = 31 * result + position.getWidthOfSpace();
        result = 31 * result + position.getWordSpacing();

        Style existing = styles.get(result);
        if (existing == null) {
            float realFontSizeX = position.getFontSize() * position.getXScale();
            float realFontSizeY = position.getFontSize() * position.getYScale();

            /* build a string with fontname / type */
            final String baseFontName = position.getFont().getBaseFont() == null ? "null" : position.getFont().getBaseFont();
            final String fontname = baseFontName + " (" + position.getFont().getSubType() + ")";

            existing = getStyle(realFontSizeX, realFontSizeY, position.getWidthOfSpace(), fontname);
            styles.put(result, existing);
        }


        return existing;
    }

    private Style getStyle(float xSize, float ySize, final float widthOfSpace, String font) {
        return new Style(font, round(xSize), round(ySize), round(widthOfSpace));
    }
}
