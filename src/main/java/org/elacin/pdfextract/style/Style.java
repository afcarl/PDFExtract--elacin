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

package org.elacin.pdfextract.style;


import java.io.Serializable;

/**
 * Created by IntelliJ IDEA. User: elacin Date: Mar 18, 2010 Time: 2:32:20 PM <p/> A style is a
 * collection of a font (described by a string), its X and Y sizes, and an estimate of the widt of a
 * space in this font. All these properties are immutable.
 */
public class Style implements Serializable {
// ------------------------------ FIELDS ------------------------------

public final int xSize, ySize;
public final String font;
public final int    id;

private transient boolean toStringCreated;
private transient String  toStringCache;

// --------------------------- CONSTRUCTORS ---------------------------

public Style(final String font, final int xSize, final int ySize, final int id) {
	this.font = font;
	this.xSize = xSize;
	this.ySize = ySize;
	this.id = id;
}

// ------------------------ CANONICAL METHODS ------------------------

@Override
public boolean equals(final Object o) {
	if (this == o) { return true; }
	if (o == null || getClass() != o.getClass()) { return false; }

	final Style style = (Style) o;

	if (xSize != style.xSize) { return false; }
	if (ySize != style.ySize) { return false; }
	if (font != null ? !font.equals(style.font) : style.font != null) { return false; }

	return true;
}

@Override
public int hashCode() {
	int result = xSize;
	result = 31 * result + ySize;
	result = 31 * result + (font != null ? font.hashCode() : 0);
	return result;
}

@Override
public String toString() {
	if (!toStringCreated) {
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(font);
		sb.append(", x=").append(xSize);
		sb.append(", y=").append(ySize);
		sb.append('}');
		toStringCache = sb.toString();
		toStringCreated = true;
	}

	return toStringCache;
}

public boolean isCompatibleWith(final Style style) {
	if (xSize != style.xSize) {
		return false;
	}

	if (ySize != style.ySize) {
		return false;
	}

	/* for this purpose, ignore font styles like bold and italic.
	*   this means that we need to separate out that part from the name of the font
	* Two typical font names can be:
	* - LPPMinionUnicode-Italic
	* - LPPMyriadCondLightUnicode (as apposed to for example LPPMyriadCondUnicode and
	* LPPMyriadLightUnicode-Bold)
	*
	* I want to separate the LPPMinion part for example from the first, so i look for the
	 *  index of the first capital letter after a small one. Then i compare the substring
	  * preceeding that letter to the same lengt substring of the other string
	*
	* */
	if (font.equals(style.font)) {
		return true;
	}

	boolean foundLowercase = false;
	int index = -1;
	for (int i = 0; i < font.length(); i++) {
		if (!foundLowercase && Character.isLowerCase(font.charAt(i))) {
			foundLowercase = true;
		}
		if (foundLowercase && Character.isUpperCase(font.charAt(i))) {
			index = i;
			break;
		}
	}

	if (index == -1) {
		return false;
	}

	return font.substring(0, index).equals(style.font.substring(0, index));

}
}
