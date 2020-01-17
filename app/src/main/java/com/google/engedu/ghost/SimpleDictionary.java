/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;
    private Random random = new Random();

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while ((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
                words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    public int binarySearch(String prefix) {
        int first = 0;
        int last = words.size() - 1;
        while (first < last) {
            System.out.println("loop 1, " + last + "  " + first);
            int m =  (first + (last - 1)) / 2;
            String wordAtM = words.get(m);
            if (words.get(m).startsWith(prefix)) {
                System.out.println("loop 2, " + last + "  " + first);
                return m;
            } else if (words.get(m).compareTo(prefix) < 0) { //if prefix is greater, ignore left half
                System.out.println("loop 3, " + last + "  " + first);
                first = m + 1;
            } else { //else, igonre right half
                System.out.println("loop 4, " + last + "  " + first);
                last = m - 1;
            }
        }
        System.out.println("loop g");
        return -1;
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        String myWord = "";
//        sortForSearch();
        if (prefix == null | prefix == "") {
            int rand = random.nextInt(words.size() - 1);
            return words.get(rand);
        } else {
            System.out.println("right before binary search");
            System.out.println(prefix);
            int index = binarySearch(prefix);
            System.out.println("right after binary seatrch");
            if (index >= 0){
                return words.get(index);
            }
            else{
                return null;
            }
        }
    }


/*
using binary search to determine the whole range of words that start with the given prefix.
dividing the words between odd lengths and even lengths
randomly selecting a word from the appropriate set (whether it's even or odd depends on who went first)
 */

    @Override
    public String getGoodWordStartingWith(String prefix) {
        ArrayList<String> even = new ArrayList<String>();
        ArrayList<String> odd = new ArrayList<String>();
//        String selected = null;
        int index = binarySearch(prefix);
        int ptvCounter = index + 1;
        int ngtCounter = index - 1;
        if (index>=0){
            while (words.get(ptvCounter).startsWith(prefix) && ptvCounter <= words.size()){
                ptvCounter++;
            }
            while (words.get(ngtCounter).startsWith(prefix) && ngtCounter >=0){
                ngtCounter--;
            }
            for (int i = ngtCounter; i <= ptvCounter; i++){
                if (words.get(i).length()%2 ==0){
                    even.add(words.get(i));
                }
                else{
                    odd.add(words.get(i));
                }
            }
        }
        else{
            return null;
        }
        if (prefix.length()%2 ==0 ){
            int select = random.nextInt(even.size());
            return even.get(select);
        }
        else{
            int select = random.nextInt(odd.size());
            return odd.get(select);
        }
    }
}
