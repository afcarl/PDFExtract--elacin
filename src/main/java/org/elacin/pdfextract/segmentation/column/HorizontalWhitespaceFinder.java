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

package org.elacin.pdfextract.segmentation.column;

import org.elacin.pdfextract.util.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: elacin
 * Date: Sep 9, 2010
 * Time: 4:33:59 AM
 * To change this template use File | Settings | File Templates.
 */
class HorizontalWhitespaceFinder extends AbstractWhitespaceFinder {
// --------------------------- CONSTRUCTORS ---------------------------

    HorizontalWhitespaceFinder(final Collection<Rectangle> texts, final int numWhitespacesToBeFound, final int width, final int height) {
        super(texts, numWhitespacesToBeFound, width, height);
        allObstacles.addAll(texts);
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * This is the quality function by which we sort rectangles to choose the 'best' one first.
     * <p/>
     * The current function bases itself on the area of the rectangle, and then heavily prefers high and narrow ones
     *
     * @param r
     * @return
     */
    @Override
    protected float rectangleQuality(final Rectangle r) {
        return (float) (r.area() * (1.0 + 3.0*Math.log(r.getHeight())));
    }

    @Override
    protected List<Rectangle> selectUsefulWhitespace(final List<Rectangle> foundWhitespace) {
        List<Rectangle> ret = new ArrayList<Rectangle>();
        for (Rectangle whitespace : foundWhitespace) {
            if (whitespace.getHeight() > WHITESPACE_MIN_HEIGHT) {
                ret.add(whitespace);
            }
        }

        return ret;
    }
}