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

package nz.govt.natlib.meta;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;


import nz.govt.natlib.meta.config.Config;
import nz.govt.natlib.meta.config.ConfigMapEntry;

/**
 * This is really just an adapter - you might say! But, it is slightly simpler
 * and different and rationalising it would be twisting it into something it
 * isn't. This class is also a factory, that produces Transformers by name or
 * function.
 * 
 * @author unascribed
 * @version 1.0
 */

public abstract class TransformProcessor {

	private static HashMap tformers = new HashMap();

	/**
	 * This returns a TransformProcessor that is capable of transforming an XML
	 * document from one DTD to the other. At the moment it uses the config
	 * 'Map' to decide how to transform something. This method could be smarter
	 * and determine that if it couldn't directly get from one format to
	 * another, it would take a multistep approach... Too cool to build for this
	 * project.
	 * 
	 * @param inDTD
	 * @param outDTD
	 */
	public static TransformProcessor getInstance(String inDTD, String outDTD) {
		ConfigMapEntry tform = Config.getInstance().getMapping(inDTD, outDTD);

		// if the transform can't be done
		if ((outDTD != null) && (tform == null)) {
			throw new RuntimeException(
					"No transformation script available to convert " + inDTD
							+ " into " + outDTD);
		}

		// decide what method to use
		String xsl = null;
		if (tform == null) {
			xsl = null;
		} else {
			xsl = tform.getXsltFunction();
		}

		xsl = xsl == null ? null : Config.getInstance().getXMLBaseURL() + "/"
				+ xsl;
		// LogManager.getInstance().logMessage(LogMessage.WORTHLESS_CHATTER,"Identifying
		// transformer for :"+inDTD+" to "+outDTD+" = "+xsl);
		return getInstance(xsl);
	}

	public static TransformProcessor getInstance(String xsl) {
		if (xsl == null) {
			// no transform - just blank...
			return new DoNothingTransformer(); // no tform.
		}

		// construct one...
		TransformProcessor transformer = (TransformProcessor) tformers.get(xsl);
		if (transformer == null) {
			transformer = new XSLTransformer(xsl);
			tformers.put(xsl, transformer);
		}
		return transformer;
	}

	protected TransformProcessor() {
	}

	/**
	 * transforms one xml file into another using the xsl style sheet specified.
	 * This is the streaming way of doing it - which would be the best way for
	 * large scale uses of this tool.
	 */
	public abstract void transform(InputStream in, OutputStream out);

	/**
	 * transforms one xml file into another using the xsl style sheet specified.
	 * This is the 'in-memory' way of doing it - best for transforming data that
	 * is already in memory. This is a less efficient way of doing this and
	 * should only be used where a stream based method would be impossible.
	 */
	public byte[] transform(byte[] buffer) {
		// organise the data as a "stream"
		ByteArrayInputStream in = new ByteArrayInputStream(buffer);
		ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
		transform(in, out);
		return out.toByteArray();
	}

}