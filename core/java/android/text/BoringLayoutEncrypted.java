/* 
 * SchrodinText - Strong Protection of Sensitive Textual Content of Mobile Applications
 * File: BoringLayoutEncrypted.java
 * Description: Offload pre-computed ratios here to keep original file clean (BoringLayout.java).
 *
 * Copyright (c) 2016-2019 University of California - Irvine, Irvine, USA
 * All rights reserved.
 *
 * Authors: Ardalan Amiri Sani
 *			Nicholas Wei
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions, and the following disclaimer,
 *    without modification.
 * 2. Redistributions in binary form must reproduce at minimum a disclaimer
 *    substantially similar to the "NO WARRANTY" disclaimer below
 *    ("Disclaimer") and any redistribution must be conditioned upon
 *    including a substantially similar Disclaimer requirement for further
 *    binary redistribution.
 * 3. Neither the names of the above-listed copyright holders nor the names
 *    of any contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * Alternatively, this software may be distributed under the terms of the
 * GNU General Public License ("GPL") version 2 as published by the Free
 * Software Foundation.
 *
 * NO WARRANTY
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR CONTRIBUTORS BE LIABLE FOR SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 */

package android.text;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

// This is only a class to offload the pre-computed ratios here
/** @hide */
public class BoringLayoutEncrypted {

	public static HashMap<Integer, Double> symbol_map = new HashMap<Integer, Double>();
	public static HashMap<Integer, Double> lowercase_map = new HashMap<Integer, Double>();
	public static HashMap<Integer, Double> uppercase_map = new HashMap<Integer, Double>();
	public static double symbol_ratio = 0.2128;
	public static double lowercase_ratio = 0.7626;
	public static double uppercase_ratio = 0.0246;

	public static void fillRatios() {
		// Sorted by ratio (greatest to least, excluding ratios less than 0.0001
		// Key = ASCII Number - 32; Value = ratio

		// Symbol Ratios:
		symbol_map.put(0, 0.8075);		 // (space)
		symbol_map.put(12, 0.0683);	 	 // ,
		symbol_map.put(14, 0.0427);		 // .
		symbol_map.put(2, 0.0220);		 // "
		symbol_map.put(13, 0.0158);		 // -
		symbol_map.put(7, 0.0152);		 // '
		symbol_map.put(27, 0.0070);		 // ;
		symbol_map.put(31, 0.0037); 	 // ?
		symbol_map.put(1, 0.0033);		 // !
		symbol_map.put(63, 0.0028);		 // _
		symbol_map.put(26, 0.0017);		 // :
		symbol_map.put(17, 0.0014);		 // 1
		symbol_map.put(9, 0.0009);		 // )
		symbol_map.put(8, 0.0009);		 // (
		symbol_map.put(18, 0.0008);		 // 2
		symbol_map.put(16, 0.0008);		 // 0
		symbol_map.put(59, 0.0006);		 // [
		symbol_map.put(61, 0.0006);		 // ]
		symbol_map.put(19, 0.0006);		 // 3
		symbol_map.put(24, 0.0006); 	 // 8
		symbol_map.put(21, 0.0005);		 // 5
		symbol_map.put(20, 0.0005);		 // 4
		symbol_map.put(23, 0.0004);		 // 7
		symbol_map.put(22, 0.0004);		 // 6
		symbol_map.put(25, 0.0003);		 // 9
		symbol_map.put(10, 0.0003);		 // *
		symbol_map.put(92, 0.0001);		 // |
		
		// Lowercase Ratios:
		lowercase_map.put(69, 0.1277);	 // e
		lowercase_map.put(84, 0.0916);	 // t
		lowercase_map.put(65, 0.0808);	 // a
		lowercase_map.put(79, 0.0779);	 // o
		lowercase_map.put(78, 0.0700);	 // n
		lowercase_map.put(73, 0.0650);	 // i
		lowercase_map.put(72, 0.0645);	 // h
		lowercase_map.put(83, 0.0631);	 // s
		lowercase_map.put(82, 0.0595);	 // r
		lowercase_map.put(68, 0.0440);	 // d
		lowercase_map.put(76, 0.0411);	 // l
		lowercase_map.put(85, 0.0291);	 // u
		lowercase_map.put(77, 0.0244);	 // m
		lowercase_map.put(67, 0.0239);	 // c
		lowercase_map.put(70, 0.0230);	 // f
		lowercase_map.put(87, 0.0228);	 // w
		lowercase_map.put(89, 0.0199);	 // y
		lowercase_map.put(71, 0.0198);	 // g
		lowercase_map.put(80, 0.0163);	 // p
		lowercase_map.put(66, 0.0144);	 // b
		lowercase_map.put(86, 0.0100);	 // v
		lowercase_map.put(75, 0.0077);	 // k
		lowercase_map.put(88, 0.0015);	 // x
		lowercase_map.put(81, 0.0010);	 // q
		lowercase_map.put(74, 0.0009);	 // j
		lowercase_map.put(90, 0.0005);	 // z

		// Uppercase Ratios:
		uppercase_map.put(41, 0.1693);	 // I
		uppercase_map.put(52, 0.1003);	 // T
		uppercase_map.put(33, 0.0730);	 // A
		uppercase_map.put(51, 0.0640);	 // S
		uppercase_map.put(40, 0.0606);	 // H
		uppercase_map.put(45, 0.0590);	 // M
		uppercase_map.put(34, 0.0457);	 // B
		uppercase_map.put(55, 0.0440);	 // W
		uppercase_map.put(35, 0.0415);	 // C
		uppercase_map.put(37, 0.0394);	 // E
		uppercase_map.put(46, 0.0330);	 // N
		uppercase_map.put(44, 0.0326);	 // L
		uppercase_map.put(47, 0.0314);	 // O
		uppercase_map.put(48, 0.0299);	 // P
		uppercase_map.put(50, 0.0296);	 // R
		uppercase_map.put(36, 0.0289);	 // D
		uppercase_map.put(39, 0.0263);	 // G
		uppercase_map.put(38, 0.0252);	 // F
		uppercase_map.put(57, 0.0199);	 // Y
		uppercase_map.put(42, 0.0151);	 // J
		uppercase_map.put(54, 0.0088);	 // V
		uppercase_map.put(43, 0.0082);	 // K
		uppercase_map.put(53, 0.0078);	 // U
		uppercase_map.put(56, 0.0031);	 // X
		uppercase_map.put(49, 0.0020);	 // Q
		uppercase_map.put(58, 0.0011);	 // Z
	}

	public static double calculateWeightedAverage(int[] charWidths) {
		double weightedSymbol = 0.0;
		double weightedLower = 0.0;
		double weightedUpper = 0.0;

		Iterator it = BoringLayoutEncrypted.symbol_map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Double> pair = (Map.Entry) it.next();
			weightedSymbol += (charWidths[pair.getKey()] * pair.getValue());
		}

		it = BoringLayoutEncrypted.lowercase_map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Double> pair = (Map.Entry) it.next();
			weightedLower += (charWidths[pair.getKey()] * pair.getValue());
		}

		it = BoringLayoutEncrypted.uppercase_map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Double> pair = (Map.Entry) it.next();
			weightedUpper += (charWidths[pair.getKey()] * pair.getValue());
		}

		return (BoringLayoutEncrypted.symbol_ratio * weightedSymbol) + 
				(BoringLayoutEncrypted.lowercase_ratio * weightedLower) +
				(BoringLayoutEncrypted.uppercase_ratio * weightedUpper);
	}
}
