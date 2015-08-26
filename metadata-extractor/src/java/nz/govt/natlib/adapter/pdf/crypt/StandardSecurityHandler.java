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

/*
 * Created on 4/06/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nz.govt.natlib.adapter.pdf.crypt;

import java.security.MessageDigest;

import nz.govt.natlib.adapter.pdf.dom.DictionaryNode;
import nz.govt.natlib.adapter.pdf.dom.ObjectNode;
import nz.govt.natlib.adapter.pdf.dom.PDFDocument;
import nz.govt.natlib.adapter.pdf.dom.PDFNode;
import nz.govt.natlib.adapter.pdf.dom.PDFUtil;
import nz.govt.natlib.adapter.pdf.dom.StringNode;

/**
 * @author nevans
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class StandardSecurityHandler implements PDFSecurityHandler {

	private PDFDocument document;

	private MessageDigest md5;

	private byte[] mkey = null;

	private byte state[] = new byte[256];

	private int x;

	private int y;

	private byte key[];

	private int keySize;

	private byte extra[] = new byte[5];

	public StandardSecurityHandler(PDFDocument doc) {
		this.document = doc;

		md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		mkey = calculateGlobalEncryptionKey(document.getIdBytes(),
				padPassword(""), document.getOwnerTokenBytes(), document
						.getPermissions(), document.isKeyStrength128());

		// check to see if there is a user password...
		// if there is then we can't read the doc without a password being
		// passed in...
		String pwd = document.getUserPassword();
		if (!checkPassword(pwd)) {
			if (pwd == null) {
				throw new RuntimeException(
						"User password set on PDF Document - cannot decrypt document without password");
			} else {
				throw new RuntimeException("Incorrect user password supplied");
			}
		}
	}

	public boolean checkPassword(String user) {
		byte[] ukey = calculateGlobalEncryptionKey(document.getIdBytes(),
				padPassword(user), document.getOwnerTokenBytes(), document
						.getPermissions(), document.isKeyStrength128());

		prepareRC4Key(ukey);
		byte[] checkBytes = new byte[32];
		encryptRC4(document.getUserTokenBytes(), 0, 32, checkBytes);

		// if the password is correct then the checkbytes will equal the pad
		// bytes...
		boolean match = true;
		for (int i = 0; i < checkBytes.length; i++) {
			if (checkBytes[i] != pad[i]) {
				match = false;
				break;
			}
		}

		return match;
	}

	static final byte pad[] = { (byte) 0x28, (byte) 0xBF, (byte) 0x4E,
			(byte) 0x5E, (byte) 0x4E, (byte) 0x75, (byte) 0x8A, (byte) 0x41,
			(byte) 0x64, (byte) 0x00, (byte) 0x4E, (byte) 0x56, (byte) 0xFF,
			(byte) 0xFA, (byte) 0x01, (byte) 0x08, (byte) 0x2E, (byte) 0x2E,
			(byte) 0x00, (byte) 0xB6, (byte) 0xD0, (byte) 0x68, (byte) 0x3E,
			(byte) 0x80, (byte) 0x2F, (byte) 0x0C, (byte) 0xA9, (byte) 0xFE,
			(byte) 0x64, (byte) 0x53, (byte) 0x69, (byte) 0x7A };

	/**
	 */
	private static byte[] padPassword(String userPassword) {
		return padPassword(PDFUtil.getISOBytes(userPassword));
	}

	private static byte[] padPassword(byte userPassword[]) {
		byte userPad[] = new byte[32];
		if (userPassword == null) {
			System.arraycopy(pad, 0, userPad, 0, 32);
		} else {
			System.arraycopy(userPassword, 0, userPad, 0, Math.min(
					userPassword.length, 32));
			if (userPassword.length < 32)
				System.arraycopy(pad, 0, userPad, userPassword.length,
						32 - userPassword.length);
		}

		return userPad;
	}

	/**
	 * 
	 * 
	 */
	private byte[] calculateGlobalEncryptionKey(byte[] documentID,
			byte userPad[], byte ownerKey[], int permissions,
			boolean strength128Bits) {
		byte[] mkey = new byte[strength128Bits ? 16 : 5];
		md5.reset();
		md5.update(userPad);
		md5.update(ownerKey);

		byte ext[] = new byte[4];
		ext[0] = (byte) permissions;
		ext[1] = (byte) (permissions >> 8);
		ext[2] = (byte) (permissions >> 16);
		ext[3] = (byte) (permissions >> 24);
		md5.update(ext, 0, 4);
		if (documentID != null)
			md5.update(documentID);

		byte digest[] = md5.digest();

		if (mkey.length == 16) {
			for (int k = 0; k < 50; ++k)
				digest = md5.digest(digest);
		}

		System.arraycopy(digest, 0, mkey, 0, mkey.length);

		// System.out.println("docID:"+new String(documentID));
		// System.out.println("userPad:"+new String(userPad));
		// System.out.println("ownerKey:"+new String(ownerKey));
		// System.out.println("mkey:"+new String(mkey));
		// for (int i=0;i<mkey.length;i++) {
		// System.out.println("mkey,"+i+":"+mkey[i]);
		// }
		// System.out.println("permissions:"+new String(ext));

		return mkey;
	}

	private boolean hasUserKey() {
		return false;
	}

	public void decrypt(PDFNode value) {
		ObjectNode obj = value.getContainingObject();
		if (obj == null)
			return; // time to go back!
		int objId = obj.getId();
		int objVer = obj.getVersion();

		// take all the strings and decrypt them...
		if (value instanceof StringNode) {
			StringNode strNode = (StringNode) value;
			strNode.setValue(decryptString(strNode.getStringValue(), objId,
					objVer));
		}

		if (value instanceof ObjectNode) {
			ObjectNode o = (ObjectNode) value;
			decrypt(o.getValue());
		}

		if (value instanceof DictionaryNode) {
			DictionaryNode dict = (DictionaryNode) value;
			for (int i = 0; i < dict.size(); i++) {
				PDFNode val = dict.get(i);
				decrypt(val);
			}
		}

		// todo extend through to all types of node - don't need any more for
		// now.
	}

	public String decryptString(String value, int objNum, int objGen) {
		String result = value;
		setHashKey(objNum, objGen);
		prepareKey();
		byte[] bytes = PDFUtil.getISOBytes(value);
		encryptRC4(bytes);
		result = new String(bytes);
		return result;
	}

	private void setHashKey(int number, int generation) {
		md5.reset(); // added by ujihara
		extra[0] = (byte) number;
		extra[1] = (byte) (number >> 8);
		extra[2] = (byte) (number >> 16);
		extra[3] = (byte) generation;
		extra[4] = (byte) (generation >> 8);
		md5.update(mkey);
		key = md5.digest(extra);
		keySize = mkey.length + 5;
		if (keySize > 16)
			keySize = 16;
	}

	public void prepareKey() {
		prepareRC4Key(key, 0, keySize);
	}

	public void prepareRC4Key(byte key[]) {
		prepareRC4Key(key, 0, key.length);
	}

	public void prepareRC4Key(byte key[], int off, int len) {
		int index1 = 0;
		int index2 = 0;
		for (int k = 0; k < 256; ++k)
			state[k] = (byte) k;
		x = 0;
		y = 0;
		byte tmp;
		for (int k = 0; k < 256; ++k) {
			index2 = (key[index1 + off] + state[k] + index2) & 255;
			tmp = state[k];
			state[k] = state[index2];
			state[index2] = tmp;
			index1 = (index1 + 1) % len;
		}
	}

	private void encryptRC4(byte dataIn[], int off, int len, byte dataOut[]) {
		int length = len + off;
		byte tmp;
		for (int k = off; k < length; ++k) {
			x = (x + 1) & 255;
			y = (state[x] + y) & 255;
			tmp = state[x];
			state[x] = state[y];
			state[y] = tmp;
			dataOut[k] = (byte) (dataIn[k] ^ state[(state[x] + state[y]) & 255]);
		}
	}

	public void encryptRC4(byte data[], int off, int len) {
		encryptRC4(data, off, len, data);
	}

	public void encryptRC4(byte dataIn[], byte dataOut[]) {
		encryptRC4(dataIn, 0, dataIn.length, dataOut);
	}

	public void encryptRC4(byte data[]) {
		encryptRC4(data, 0, data.length, data);
	}
}
