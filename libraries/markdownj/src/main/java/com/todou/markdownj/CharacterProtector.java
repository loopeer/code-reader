/*
Copyright (c) 2005, Pete Bevin.
<http://markdownj.petebevin.com>

All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

* Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.

* Neither the name "Markdown" nor the names of its contributors may
  be used to endorse or promote products derived from this software
  without specific prior written permission.

This software is provided by the copyright holders and contributors "as
is" and any express or implied warranties, including, but not limited
to, the implied warranties of merchantability and fitness for a
particular purpose are disclaimed. In no event shall the copyright owner
or contributors be liable for any direct, indirect, incidental, special,
exemplary, or consequential damages (including, but not limited to,
procurement of substitute goods or services; loss of use, data, or
profits; or business interruption) however caused and on any theory of
liability, whether in contract, strict liability, or tort (including
negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.

*/

package com.todou.markdownj;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CharacterProtector {
    private final ConcurrentMap<String, String> protectMap = new ConcurrentHashMap<String, String>();
    private final ConcurrentMap<String, String> unprotectMap = new ConcurrentHashMap<String, String>();
    private static final String GOOD_CHARS = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
    private Random rnd = new Random();


    public String encode(String literal) {
        String encoded = protectMap.get(literal);
        if (encoded == null) {
            synchronized (protectMap) {
                encoded = protectMap.get(literal);
                if (encoded == null) {
                    encoded = addToken(literal);
                }
            }
        }
        return encoded;
    }

    public String decode(String coded) {
        return unprotectMap.get(coded);
    }

    public Collection<String> getAllEncodedTokens() {
        return Collections.unmodifiableSet(unprotectMap.keySet());
    }

    private String addToken(String literal) {
        String encoded = longRandomString();

        protectMap.put(literal, encoded);
        unprotectMap.put(encoded, literal);

        return encoded;
    }

    private String longRandomString() {
        StringBuilder sb = new StringBuilder();
        final int CHAR_MAX = GOOD_CHARS.length();
        for (int i = 0; i < 20; i++) {
            sb.append(GOOD_CHARS.charAt(rnd.nextInt(CHAR_MAX)));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return protectMap.toString();
    }
}
