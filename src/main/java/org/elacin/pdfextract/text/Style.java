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

package org.elacin.pdfextract.text;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: elacin
 * Date: Mar 18, 2010
 * Time: 2:32:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class Style implements Serializable {
    // ------------------------------ FIELDS ------------------------------

    public final float xSize, ySize, widthOfSpace, wordSpacing;
    public final String font;

    // --------------------------- CONSTRUCTORS ---------------------------

    public Style(final String font, final float xSize, final float ySize, final float widthOfSpace, final float wordSpacing) {
        this.font = font;
        this.xSize = xSize;
        this.ySize = ySize;
        this.widthOfSpace = widthOfSpace;
        this.wordSpacing = wordSpacing;
    }

    // ------------------------ CANONICAL METHODS ------------------------

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Style style = (Style) o;

        if (Float.compare(style.xSize, xSize) != 0) return false;
        if (Float.compare(style.ySize, ySize) != 0) return false;
        if (font != null ? !font.equals(style.font) : style.font != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (xSize != +0.0f ? Float.floatToIntBits(xSize) : 0);
        result = 31 * result + (ySize != +0.0f ? Float.floatToIntBits(ySize) : 0);
        result = 31 * result + (font != null ? font.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(font);
        sb.append(", x=").append(xSize);
        sb.append(", y=").append(ySize);
        //        sb.append(", widthOfSpace=").append(widthOfSpace);
        //        sb.append(", wordSpacing=").append(wordSpacing);
        sb.append('}');
        return sb.toString();
    }
}