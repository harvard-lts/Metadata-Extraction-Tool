/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package nz.govt.natlib.adapter.works;

import java.util.HashMap;

import nz.govt.natlib.adapter.word.LanguageMap;

/**
 * 
 * @author Angela McCormack
 * @version 1.0
 */

public class OLELanguageMap extends HashMap implements LanguageMap {
	private OLELanguageMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public Object put(int id, String idString) {
		return put(new Integer(id), idString);
	}

	public Object get(int id) {
		return get(new Integer(id));
	}

	public static LanguageMap getLanguageMap() {
		LanguageMap m = new OLELanguageMap(100, (float) 1.0);

		m.put(0x0401, "Arabic");
		m.put(0x0402, "Bulgarian");
		m.put(0x0403, "Catalan");
		m.put(0x0404, "Traditional Chinese");
		m.put(0x0804, "Simplified Chinese");
		m.put(0x0405, "Czech");
		m.put(0x0406, "Danish");
		m.put(0x0407, "German");
		m.put(0x0807, "Swiss German");
		m.put(0x0408, "Greek");
		m.put(0x0409, "U.S. English");
		m.put(0x0809, "U.K. English");
		m.put(0x0c09, "Australian English");
		m.put(0x040a, "Castilian Spanish");
		m.put(0x080a, "Mexican Spanish");
		m.put(0x040b, "Finnish");
		m.put(0x040c, "French");
		m.put(0x080c, "Belgian French");
		m.put(0x0c0c, "Canadian French");
		m.put(0x100c, "Swiss French");
		m.put(0x040d, "Hebrew");
		m.put(0x040e, "Hungarian");
		m.put(0x040f, "Icelandic");
		m.put(0x0410, "Italian");
		m.put(0x0810, "Swiss Italian");
		m.put(0x0411, "Japanese");
		m.put(0x0412, "Korean");
		m.put(0x0413, "Dutch");
		m.put(0x0813, "Belgian Dutch");
		m.put(0x0414, "Norwegian - Bokmal");
		m.put(0x0814, "Norwegian - Nynorsk");
		m.put(0x0415, "Polish");
		m.put(0x0416, "Portuguese");
		m.put(0x0816, "Brazilian Portuguese");
		m.put(0x0417, "Rhaeto-Romanic");
		m.put(0x0418, "Romanian");
		m.put(0x0419, "Russian");
		m.put(0x041a, "Croato-Serbian (Latin)");
		m.put(0x081a, "Serbo-Croatian (Cyrillic)");
		m.put(0x041b, "Slovak");
		m.put(0x041c, "Albanian");
		m.put(0x041d, "Swedish");
		m.put(0x041e, "Thai");
		m.put(0x041f, "Turkish");
		m.put(0x0420, "Urdu");
		m.put(0x0421, "Bahasa");
		m.put(0x0422, "Ukrainian");
		m.put(0x0423, "Byelorussian");
		m.put(0x0424, "Slovenian");
		m.put(0x0425, "Estonian");
		m.put(0x0426, "Latvian");
		m.put(0x0427, "Lithuanian");
		m.put(0x0429, "Farsi");
		m.put(0x042D, "Basque");
		m.put(0x042F, "Macedonian");
		m.put(0x0436, "Afrikaans");
		m.put(0x043E, "Malaysian");
		return m;
	}

}
