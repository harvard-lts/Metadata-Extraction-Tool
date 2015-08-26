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

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import nz.govt.natlib.meta.log.LogManager;
import nz.govt.natlib.meta.log.LogMessage;

/**
 * Transform an XML stream usings an XSLT transformation.
 * 
 * @author bbeaumont
 *
 */
public class XSLTransformer extends TransformProcessor {

	private Transformer transformer;

	public XSLTransformer(final String xsl) {
		try {
			// Use the static TransformerFactory.newInstance() method to
			// instantiate
			// a TransformerFactory. The
			// javax.xml.transform.TransformerFactory
			// system property setting determines the actual class to
			// instantiate --
			// org.apache.xalan.transformer.TransformerImpl.
			TransformerFactory tFactory = TransformerFactory.newInstance();
			// Use the TransformerFactory to instantiate a Transformer that
			// will work with
			// the stylesheet you specify. This method call also processes
			// the stylesheet
			// into a compiled Templates object.
			transformer = tFactory.newTransformer(new StreamSource(xsl));

			transformer.setErrorListener(new ErrorListener() {
				public void fatalError(TransformerException ex) {
					LogMessage msg = new LogMessage(LogMessage.CRITICAL,
							ex, "Fatal Error while processing",
							"fix the xsl:" + xsl + " stylesheet");
					// LogManager.logMessage(msg);
					throw new RuntimeException(
							"Error while transforming using " + xsl
									+ " stylesheet:" + ex.getMessage());
				}

				public void error(TransformerException ex) {
					LogMessage msg = new LogMessage(LogMessage.ERROR, ex,
							"Error while processing", "fix the xsl:" + xsl
									+ " stylesheet");
					LogManager.getInstance().logMessage(msg);
				}

				public void warning(TransformerException ex) {
					LogMessage msg = new LogMessage(
							LogMessage.INFO,
							ex,
							"Warning while processing, some elements may not be included",
							"fix the xsl:" + xsl + " stylesheet");
					LogManager.getInstance().logMessage(msg);
				}
			});
		} catch (Exception ex) {
			throw new RuntimeException("Transformation error: "
					+ ex.getMessage());
		}
	}

	public void transform(InputStream in, OutputStream out) {
		try {
			// Use the Transformer to apply the associated Templates object
			// to an XML document
			// (foo.xml) and write the output to a file (foo.out).
			transformer.transform(new StreamSource(in), new StreamResult(
					out));
		} catch (TransformerException ex) {
			// error(ex);
			throw new RuntimeException("Transformation error: "
					+ ex.getMessage());
		}
	}
}