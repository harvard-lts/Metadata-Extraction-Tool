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

package nz.govt.natlib.fx;

/**
 * 
 * @author Nic Evans
 * @version 1.0
 */

public class BitFieldUtil {

	/**
	 * 
	 * @param mask
	 *            the bit field containing the number of shifts that it would
	 *            take to make the number zero. 'Why would you do such a thing'
	 *            you may ask! And the answer is simple, if you have a bit field
	 *            such as 01011011 where the first 4 bits represent a value and
	 *            the last 4 bits represent another number. You may wish to
	 *            extract the last 4 to do that you would shift the bit field 4
	 *            bits and extract the number. This method decides how many
	 *            times to shift the original number to retrive the required
	 *            bits. It should be used in conjuction with logically anding
	 *            the mask with the originalo number to 'zero' the bits that are
	 *            not required.
	 * @return a number representing the number of shits to make to make the
	 *         mask zero
	 */
	protected static int getShiftCount(int mask) {
		int count = 0;
		int bit_pattern = mask;

		if (bit_pattern != 0) {
			while ((bit_pattern & 1) == 0) {
				count++;
				bit_pattern >>= 1;
			}
		}
		return count;
	}

	/**
	 * Returns the value of a number accoring to the bit mask given. for
	 * instance: 01010100 - original number 00110000 - mask -------- 4 - because
	 * the method only considers the 'masked' bits and then shifts them right
	 * till they are the least significant bits.
	 * 
	 * see the javadoc for the getShiftCount for a fuller understanding of how
	 * why or how this is done.
	 * 
	 * @param value
	 *            the bitfield containing a number to be 'extracted'
	 * @param mask
	 *            the bit mask to identify the relevant bits.
	 * @return the value of the bits 'extracted'
	 */
	public static int getValue(int value, int mask) {
		return getRawValue(value, mask) >> getShiftCount(mask);
	}

	/**
	 * Same as the getValue(int,int) method but allows an arbitrary shift to
	 * take place.
	 * 
	 * @param value
	 *            the bitfield containing a number to be 'extracted'
	 * @param mask
	 *            the bit mask to identify the relevant bits.
	 * @param shift
	 *            the number of bits to shift
	 * @return the value of the bits 'extracted'
	 */
	public static int getValue(int value, int mask, int shift) {
		return getRawValue(value, mask) >> shift;
	}

	/**
	 * Same as the getValue(int,int) method but no shift takes place.
	 * 
	 * @param value
	 *            the bitfield containing a number to be 'extracted'
	 * @param mask
	 *            the bit mask to identify the relevant bits.
	 * @return the value of the bits 'extracted' in their original positions
	 *         (and thus binary values)
	 */
	public static int getRawValue(int value, int mask) {
		return value & mask;
	}

	/**
	 * Checks a number to see if a particular bit (or any bit of a mask) is set.
	 * 
	 * @param value
	 *            the value to be checked
	 * @param mask
	 *            the mask to check against
	 * @return true if a value has one or more of the bits in the mask set.
	 */
	public static boolean isSet(int value, int mask) {
		return ((value & mask) != 0);
	}

	public static byte[] getBytes(int number) {
		byte ext[] = new byte[4];
		ext[0] = (byte) number;
		ext[1] = (byte) (number >> 8);
		ext[2] = (byte) (number >> 16);
		ext[3] = (byte) (number >> 24);
		return ext;
	}

	public static int getNumber(int number, int startbit, int endbit) {
		String bits = getBits(number);
		String subByte = bits.substring(bits.length() - startbit, bits.length()
				- endbit + 1);
		return getValue(subByte);
	}

	public static String getBits(int bytes) {
		String res = "";
		String val = Integer.toBinaryString(bytes);
		if (val.length() > 24) {
			val = val.substring(24);
		} else if (val.length() > 16) {
			val = val.substring(16);
		} else if (val.length() > 8) {
			val = val.substring(8);
		}
		val = "00000000" + val; // do some padding!
		res += val.substring(val.length() - 8); // undo some of the padding!
		return res;
	}

	public static String getBits(byte[] bytes) {
		String res = "";
		for (int i = 0; i < bytes.length; i++) {
			String val = Integer.toBinaryString((int) bytes[i]);
			if (val.length() > 24) {
				val = val.substring(24);
			} else if (val.length() > 16) {
				val = val.substring(16);
			} else if (val.length() > 8) {
				val = val.substring(8);
			}
			val = "00000000" + val; // do some padding!
			res += val.substring(val.length() - 8); // undo some of the padding!
		}
		return res;
	}

	public static int getValue(String bits) {
		return Integer.parseInt(bits, 2);
	}

}
