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

package org.elacin.pdfextract.physical.segmentation.line;

import org.elacin.pdfextract.physical.content.GraphicContent;
import org.elacin.pdfextract.physical.content.PhysicalContent;
import org.elacin.pdfextract.physical.content.PhysicalPageRegion;
import org.elacin.pdfextract.physical.content.PhysicalText;
import org.elacin.pdfextract.tree.LineNode;
import org.elacin.pdfextract.tree.WordNode;
import org.elacin.pdfextract.util.Rectangle;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by IntelliJ IDEA. User: elacin Date: 12.11.10 Time: 20.30 To change this template use
 * File | Settings | File Templates.
 */
public class LineSegmentator {

final float tolerance;

public LineSegmentator(final float tolerance) {
	this.tolerance = tolerance;
}

// -------------------------- STATIC METHODS --------------------------

@NotNull
private static WordNode createWordNode(@NotNull final PhysicalText text, int pageNumber) {
	return new WordNode(text.getPosition(), pageNumber, text.style, text.content, text.charSpacing);
}

// -------------------------- PUBLIC METHODS --------------------------

@NotNull
public List<LineNode> segmentLines(@NotNull PhysicalPageRegion region) {
	final Rectangle pos = region.getPosition();
	final List<LineNode> ret = new ArrayList<LineNode>();

	final Set<PhysicalContent> workingSet = new HashSet<PhysicalContent>();

	for (float y = pos.getY(); y < pos.getEndY(); y++) {
		final List<PhysicalContent> row = region.findContentAtYIndex(y);


		/* remove the text from row which has already been assigned a line*/
		Iterator<PhysicalContent> iterator = row.iterator();
		while (iterator.hasNext()) {
			final PhysicalContent content = iterator.next();
			if (content.isAssignablePhysicalContent()) {
				if (content.getAssignablePhysicalContent().isAssignedBlock()) {
					iterator.remove();
				}
			}
		}


		workingSet.addAll(row);

		if ((isProbableLineBoundary(region, workingSet, y) || y + 1.0f > pos.getEndY())
				&& !workingSet.isEmpty()) {
			LineNode lineNode = createLineFrom(region, workingSet);
			if (!lineNode.getChildren().isEmpty()) {
				ret.add(lineNode);
			}
			workingSet.clear();

		}

	}

	return ret;
}

private boolean isProbableLineBoundary(@NotNull PhysicalPageRegion region,
                                       @NotNull final Set<PhysicalContent> set,
                                       final float yBoundary)
{

	/**
	 * Finds all content on lines above and below the proposed boundary, and sorts it on ascending x
	 */
	Set<PhysicalContent> contents = new TreeSet<PhysicalContent>(new Comparator<PhysicalContent>() {

		@Override
		public int compare(@NotNull final PhysicalContent o1, @NotNull final PhysicalContent o2) {
			return Float.compare(o1.getPosition().getX(), o2.getPosition().getX());
		}
	});

	final float startLooking = yBoundary;
	final float endLooking = Math.min(region.getPosition().getEndY(), yBoundary + tolerance);
	for (float y = startLooking; y <= endLooking; y += 1.0f) {
		contents.addAll(region.findContentAtYIndex(y));
	}

	for (PhysicalContent content : contents) {
		if (!content.isText()) {
			continue;
		}
		final Rectangle pos = content.getPosition();
		if (pos.getY() < startLooking && pos.getEndY() > endLooking) {
			return false;
		}
		if (set.contains(content)) {
			return false;
		}
	}


	return true;
}


@NotNull
private LineNode createLineFrom(@NotNull final PhysicalPageRegion region,
                                @NotNull final Set<PhysicalContent> workingSet)
{
	LineNode lineNode = new LineNode();
	for (PhysicalContent content : workingSet) {

		if (content.isAssignablePhysicalContent()
				&& !content.getAssignablePhysicalContent().isAssignedBlock()) {

			if (content.isText()) {
				lineNode.addChild(createWordNode(content.getText(), region.getPageNumber()));
			} else if (content.isGraphic()) {
				lineNode.addChild(new WordNode(content.getPosition(), region.getPageNumber(), GraphicContent.STYLE_FOR_GRAPHIC, "[FIG]", 0.0f));
			} else {
				throw new RuntimeException("asd");
			}

			content.getAssignablePhysicalContent().setBlockNum(1);
		}
	}
	return lineNode;
}
}
